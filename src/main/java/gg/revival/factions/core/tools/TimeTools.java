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

}
