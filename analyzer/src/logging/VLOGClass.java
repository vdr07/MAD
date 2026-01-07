package logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

public class VLOGClass {
    private static final Logger LOG = LogManager.getLogger(VLOGClass.class);

    public static final Level V1 = Level.forName("MAD", 350);
    public static final Level V2 = Level.forName("VERBOSE", 400);
    public static final Level V3 = Level.forName("DEBUG", 1000);

    private static int LOG_LEVEL = 1;

    public static void setLogLevel(int level) { LOG_LEVEL = level; }

    public static void VLOG(int level, String message) {
        if (level <= LOG_LEVEL) {
            if (level == 1) LOG.log(V1, message);
            else if (level == 2) LOG.log(V2, message);
            else if (level == 3) LOG.log(V3, message);

        }
    }

    public static void ERROR(String message) {
        LOG.error(message);
    }

    public static void FATAL(String message) {
        LOG.fatal(message);
    }
}