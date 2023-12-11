/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See native/swift/sir/tree-generator/Readme.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.sir.visitors

import org.jetbrains.kotlin.sir.*

/**
 * Auto-generated by [org.jetbrains.kotlin.sir.tree.generator.printer.VisitorVoidPrinter]
 */
abstract class SirVisitorVoid : SirVisitor<Unit, Nothing?>() {

    final override fun visitElement(element: SirElement, data: Nothing?) {
        visitElement(element)
    }

    open fun visitElement(element: SirElement) {
    }

    final override fun visitModule(module: SirModule, data: Nothing?) {
        visitModule(module)
    }

    open fun visitModule(module: SirModule) {
        visitElement(module)
    }

    final override fun visitDeclarationContainer(declarationContainer: SirDeclarationContainer, data: Nothing?) {
        visitDeclarationContainer(declarationContainer)
    }

    open fun visitDeclarationContainer(declarationContainer: SirDeclarationContainer) {
        visitElement(declarationContainer)
    }

    final override fun visitDeclaration(declaration: SirDeclaration, data: Nothing?) {
        visitDeclaration(declaration)
    }

    open fun visitDeclaration(declaration: SirDeclaration) {
        visitElement(declaration)
    }

    final override fun visitForeignDeclaration(foreignDeclaration: SirForeignDeclaration, data: Nothing?) {
        visitForeignDeclaration(foreignDeclaration)
    }

    open fun visitForeignDeclaration(foreignDeclaration: SirForeignDeclaration) {
        visitDeclaration(foreignDeclaration)
    }

    final override fun visitNamedDeclaration(namedDeclaration: SirNamedDeclaration, data: Nothing?) {
        visitNamedDeclaration(namedDeclaration)
    }

    open fun visitNamedDeclaration(namedDeclaration: SirNamedDeclaration) {
        visitDeclaration(namedDeclaration)
    }

    final override fun visitEnum(enum: SirEnum, data: Nothing?) {
        visitEnum(enum)
    }

    open fun visitEnum(enum: SirEnum) {
        visitNamedDeclaration(enum)
    }

    final override fun visitStruct(struct: SirStruct, data: Nothing?) {
        visitStruct(struct)
    }

    open fun visitStruct(struct: SirStruct) {
        visitNamedDeclaration(struct)
    }

    final override fun visitCallable(callable: SirCallable, data: Nothing?) {
        visitCallable(callable)
    }

    open fun visitCallable(callable: SirCallable) {
        visitDeclaration(callable)
    }

    final override fun visitFunction(function: SirFunction, data: Nothing?) {
        visitFunction(function)
    }

    open fun visitFunction(function: SirFunction) {
        visitCallable(function)
    }

    final override fun visitForeignFunction(foreignFunction: SirForeignFunction, data: Nothing?) {
        visitForeignFunction(foreignFunction)
    }

    open fun visitForeignFunction(foreignFunction: SirForeignFunction) {
        visitCallable(foreignFunction)
    }
}
