package legacy.editors.nanocad;

import java.io.File;

public class Settings {
    public static final int ONE_SECOND = 1000;
    public static final String APP_NAME_GAUSSIAN = "GAUSSIAN";
    public static final String APP_NAME_GAMESS = "GAMES";
    public static final String APP_NAME_NWCHEM = "NWCHEM";
    public static final String APP_NAME_MOLPRO = "MOLPRO";
    private static Settings ourInstance = new Settings();
    public static boolean authenticated = true;
    public static String username = "master";
    public static String defaultDirStr = "";
    public static String fileSeparator = File.separator;
    private static String applicationDataDir = Settings.class.getResource("/legacy.editors/nanocad").getPath();
    public static String jobDir = Settings.class.getResource("/legacy.editors/nanocad").getPath();
    public static String httpsGateway = "https://ccg-mw1.ncsa.uiuc.edu/cgi-bin/";

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }

    public static String getApplicationDataDir() {
        return applicationDataDir;
    }
}
