/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.phaser

import org.jetbrains.kotlin.backend.common.CodegenUtil
import org.jetbrains.kotlin.backend.common.LoweringContext
import org.jetbrains.kotlin.config.phaser.*
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

fun <Context : LoweringContext> performByIrFile(
    name: String,
    lower: List<CompilerPhase<Context, IrFile, IrFile>>,
): SameTypeNamedCompilerPhase<Context, IrModuleFragment> =
    SameTypeNamedCompilerPhase(
        name, emptySet(), PerformByIrFilePhase(lower), emptySet(), emptySet(), emptySet(),
        setOf(getIrDumper()), nlevels = 1,
    )

class PerformByIrFilePhase<Context : LoweringContext>(
    private val lower: List<CompilerPhase<Context, IrFile, IrFile>>,
) : SameTypeCompilerPhase<Context, IrModuleFragment> {
    override fun invoke(
        phaseConfig: PhaseConfig,
        phaserState: PhaserState<IrModuleFragment>,
        context: Context,
        input: IrModuleFragment
    ): IrModuleFragment {
        for (irFile in input.files) {
            try {
                val filePhaserState = phaserState.changePhaserStateType<IrModuleFragment, IrFile>()
                for (phase in lower) {
                    phase.invoke(phaseConfig, filePhaserState, context, irFile)
                }
            } catch (e: Throwable) {
                CodegenUtil.reportBackendException(e, "IR lowering", irFile.fileEntry.name) { offset ->
                    irFile.fileEntry.takeIf { it.supportsDebugInfo }?.let {
                        val (line, column) = it.getLineAndColumnNumbers(offset)
                        line to column
                    }
                }
            }
        }

        // TODO: no guarantee that module identity is preserved by `lower`
        return input
    }


    override fun getNamedSubphases(startDepth: Int): List<Pair<Int, NamedCompilerPhase<Context, *, *>>> =
        lower.flatMap { it.getNamedSubphases(startDepth) }
}
