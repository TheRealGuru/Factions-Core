package gg.revival.factions.core.tools;

public class TimeTools
{

    /**
     * Returns a fancy looking formatted decimal from a long
     * @param showDecimal Show decimal point?
     * @param duration Duration in date/long format
     * @return The fancy looking time
     */
    public static String getFormattedCooldown(boolean showDecimal, long duration) {
        if(showDecimal) {
            double seconds = Math.abs(duration / 1000.0f);
            return String.format("%.1f", seconds);
        }

        else {
            return String.valueOf((int)duration / 1000L);
        }
    }

    /**
     * Formats a string in to Hours, minutes and seconds
     * @param duration Time in seconds
     * @return The fancy looking time
     */
    public static String formatIntoHHMMSS(int duration) {
        int remainder = duration % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        return new StringBuilder().append(minutes).append(":").append(seconds < 10 ? "0" : "").append(seconds).toString();
    }

    public static String convertSchedule(int day, int hr, int min) {
        String dayName = "Unknown";
        String timeOfDay = "AM";

        switch(day) {
            case 1: dayName = "Saturday";
                break;
            case 2: dayName = "Sunday";
                break;
            case 3: dayName = "Monday";
                break;
            case 4: dayName = "Tuesday";
                break;
            case 5: dayName = "Wednesday";
                break;
            case 6: dayName = "Thursday";
                break;
            case 7: dayName = "Friday";
                break;
        }

        if(hr >= 12) {
            timeOfDay = "PM";

            if(hr > 12)
                hr -= 12;
        }

        if(min == 0)
            return dayName + " @ " + hr + ":" + min + 0 + " " + timeOfDay;
        else
            return dayName + " @ " + hr + ":" + min + " " + timeOfDay;
    }

}
