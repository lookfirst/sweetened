package com.googlecode.sweetened.typedef;

/**
 * Defines the scope of a jar file. Right now, there are
 * only four options: runtime, compile, unit and all.
 */
public enum SweetenedScope {
    RUNTIME("runtime"), COMPILE("compile"), UNIT("unit"), ALL("all");
    private String scope;

    /** */
    private SweetenedScope(String scope) {
        this.scope = scope;
    }

    /** */
    public String getScope() {
        return scope;
    }

    /** */
    public static SweetenedScope safeValueOf(String scope) {
        try {
            return valueOf(scope.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
