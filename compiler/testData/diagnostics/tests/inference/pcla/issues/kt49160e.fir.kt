// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-49160
// CHECK_TYPE_WITH_EXACT

fun test() {
    val outerBuildee = build outerBuild@ {
        val middleBuildee = build middleBuild@ {
            val innerBuildee = build innerBuild@ {
                this@outerBuild.setTypeVariable(TargetType())
                this@middleBuild.setTypeVariable(TargetType())
                this@innerBuild.setTypeVariable(TargetType())
            }
            // exact type equality check — turns unexpected compile-time behavior into red code
            // considered to be non-user-reproducible code for the purposes of these tests
            checkExactType<Buildee<TargetType>>(innerBuildee)
        }
        // exact type equality check — turns unexpected compile-time behavior into red code
        // considered to be non-user-reproducible code for the purposes of these tests
        checkExactType<Buildee<TargetType>>(middleBuildee)
    }
    // exact type equality check — turns unexpected compile-time behavior into red code
    // considered to be non-user-reproducible code for the purposes of these tests
    checkExactType<Buildee<TargetType>>(outerBuildee)
}




class TargetType

class Buildee<TV> {
    fun setTypeVariable(value: TV) { storage = value }
    private var storage: TV = null!!
}

fun <PTV> build(instructions: Buildee<PTV>.() -> Unit): Buildee<PTV> {
    return Buildee<PTV>().apply(instructions)
}

/* GENERATED_FIR_TAGS: assignment, checkNotNullCall, classDeclaration, functionDeclaration, functionalType,
lambdaLiteral, localProperty, nullableType, propertyDeclaration, stringLiteral, thisExpression, typeParameter,
typeWithExtension */
