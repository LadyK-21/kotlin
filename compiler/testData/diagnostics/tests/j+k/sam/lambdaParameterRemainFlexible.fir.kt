// RUN_PIPELINE_TILL: BACKEND
// FIR_DUMP
// LANGUAGE: -JavaTypeParameterDefaultRepresentationWithDNN -DontMakeExplicitJavaTypeArgumentsFlexible
// ISSUE: KT-67999

// FILE: J.java
public interface J<X> {
    void foo(X x);
}

// FILE: main.kt

fun main() {
    J<String?> { x ->
        <!RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS("String?;  This will become an error in language version 2.3. See https://youtrack.jetbrains.com/issue/KT-71718.")!>x<!>.length // Should not be unsafe call
    }
}

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaType, lambdaLiteral, nullableType */
