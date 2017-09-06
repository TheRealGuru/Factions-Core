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

}
