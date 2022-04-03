package com.nuktarkhos.aap_survey.utility;

import com.nuktarkhos.aap_survey.R;

public enum IssueType {
    BLOCKED_DRAINAGE, PATH_FEATURE, OBSTRUCTION, WIDENING,
    SURFACE, OTHER, UNKNOWN;

    public static int toResourceId(int issue){
        return toResourceId(fromInt(issue));
    }

    public static int toResourceId(IssueType issue){
        switch (issue){
            case BLOCKED_DRAINAGE:
                return R.string.blocked_string;
            case PATH_FEATURE:
                return R.string.feature_string;
            case OBSTRUCTION:
                return R.string.obstruction_string;
            case WIDENING:
                return R.string.widening_string;
            case SURFACE:
                return R.string.surface_string;
            case OTHER:
                return R.string.other_string;
            default:
                return R.string.unknown_string;
        }
    }

    public static String toString(int issue){
        switch (fromInt(issue)){
            case BLOCKED_DRAINAGE:
                return "Blocked drainage";
            case PATH_FEATURE:
                return "Feature failure";
            case OBSTRUCTION:
                return "Obstruction";
            case WIDENING:
                return "Widening";
            case SURFACE:
                return "Path surface";
            case OTHER:
                return "Other";
            default:
                return "Unknown";
        }
    }

    public static IssueType fromInt(int issue){
        switch (issue){
            case 0:
                return BLOCKED_DRAINAGE;
            case 1:
                return PATH_FEATURE;
            case 2:
                return OBSTRUCTION;
            case 3:
                return WIDENING;
            case 4:
                return SURFACE;
            case 5:
                return OTHER;
            default:
                return UNKNOWN;
        }
    }
}
