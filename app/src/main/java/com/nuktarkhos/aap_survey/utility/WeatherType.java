package com.nuktarkhos.aap_survey.utility;

import com.nuktarkhos.aap_survey.R;

public enum WeatherType {
    SUNNY, CLOUDY, OVERCAST, SHOWERS, LIGHT_RAIN, HEAVY_RAIN, UNKNOWN;

    public static int toResourceId(int severity){
        return toResourceId(fromInt(severity));
    }

    public static int toResourceId(WeatherType weather) {
        switch (weather) {
            case SUNNY:
                return R.string.weather_sunny;
            case CLOUDY:
                return R.string.weather_cloudy;
            case OVERCAST:
                return R.string.weather_overcast;
            case SHOWERS:
                return R.string.weather_showers;
            case LIGHT_RAIN:
                return R.string.weather_light_rain;
            case HEAVY_RAIN:
                return R.string.weather_heavy_rain;
            default:
                return R.string.unknown_string;
        }
    }

    public static String toString(int weather) {
        switch (fromInt(weather)) {
            case SUNNY:
                return "Sunny";
            case CLOUDY:
                return "Cloudy";
            case OVERCAST:
                return "Overcast";
            case SHOWERS:
                return "Showers";
            case LIGHT_RAIN:
                return "Light rain";
            case HEAVY_RAIN:
                return "Heavy rain";
            default:
                return "Unknown";
        }
    }

    public static WeatherType fromInt(int weather) {
        switch (weather) {
            case 0:
                return SUNNY;
            case 1:
                return CLOUDY;
            case 2:
                return OVERCAST;
            case 3:
                return SHOWERS;
            case 4:
                return LIGHT_RAIN;
            case 5:
                return HEAVY_RAIN;
            default:
                return UNKNOWN;
        }
    }
}
