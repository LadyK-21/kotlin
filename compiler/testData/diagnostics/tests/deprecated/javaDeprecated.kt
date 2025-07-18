// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -NO_VALUE_FOR_PARAMETER
// FILE: A.java

@Deprecated
public class A {
    @Deprecated
    public String getFoo(String text) {
        return text;
    }
}

// FILE: B.kt

class B(private @property:Deprecated val foo: String) : <!DEPRECATION!>A<!>() {
    override fun getFoo(text: String): String = super.<!DEPRECATION!>getFoo<!>(text + <!DEPRECATION!>foo<!>)
}

/* GENERATED_FIR_TAGS: additiveExpression, annotationUseSiteTargetProperty, classDeclaration, flexibleType,
functionDeclaration, javaFunction, javaType, override, primaryConstructor, propertyDeclaration, superExpression */
