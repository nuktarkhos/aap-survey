package com.nuktarkhos.aap_survey.utility;

import com.nuktarkhos.aap_survey.R;

public enum IssueSeverity {
    REQUIRES_ATTENTION, URGENT, UNKNOWN;

    public static int toResourceId(int severity){
        return toResourceId(fromInt(severity));
    }

    public static int toResourceId(IssueSeverity severity) {
        switch (severity) {
            case REQUIRES_ATTENTION:
                return R.string.severity_attention;
            case URGENT:
                return R.string.severity_urgent;
            default:
                return R.string.unknown_string;
        }
    }

    public static String toString(int severity) {
        switch (fromInt(severity)) {
            case REQUIRES_ATTENTION:
                return "Requires attention";
            case URGENT:
                return "Urgent";
            default:
                return "Uknown";
        }
    }

    public static IssueSeverity fromInt(int severity) {
        switch (severity) {
            case 0:
                return REQUIRES_ATTENTION;
            case 1:
                return URGENT;
            default:
                return UNKNOWN;
        }
    }
}
