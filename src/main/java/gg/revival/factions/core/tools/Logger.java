package gg.revival.factions.core.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger {

    public static void log(String str) {
        log(Level.INFO, str);
    }

    public static void log(Level lvl, String str) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/mm '@' hh:mm:ss aa");

        System.out.println("[" + format.format(date) + "][Factions Core][" + lvl.toString() + "] " + str);
    }

}
