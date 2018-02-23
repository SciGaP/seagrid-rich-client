package legacy.editor.commons;

import java.io.File;

public class Settings {
    public static final int ONE_SECOND = 1000;
    public static final String APP_NAME_GAUSSIAN = "GAUSSIAN";
    public static final String APP_NAME_GAMESS = "GAMES";
    public static final String APP_NAME_NWCHEM = "NWCHEM";
    public static final String APP_NAME_MOLPRO = "MOLPRO";
    public static final String APP_NAME_PSI4 = "PSI4";
    public static final String APP_NAME_MOLCAS = "MOLCAS";

    private static Settings ourInstance = new Settings();
    public static boolean authenticated = true;
    public static String username = "master";
    public static String defaultDirStr = "";
    public static String fileSeparator = File.separator;
    private static String applicationDataDir = defaultDataDirectory() + "legacy.editors";
    public static String jobDir = defaultDataDirectory() + "legacy.editors";
    public static String httpsGateway = "https://ccg-mw1.ncsa.uiuc.edu/cgi-bin/";

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }

    public static String getApplicationDataDir() {
        return applicationDataDir;
    }

    private static String defaultDataDirectory()
    {
        return System.getProperty("user.home") + File.separator + "SEAGrid" + File.separator + ".ApplicationData" + File.separator;
    }
}
