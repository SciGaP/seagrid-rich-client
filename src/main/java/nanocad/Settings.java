package nanocad;

import java.io.File;

public class Settings {
    public static final int ONE_SECOND = 1000;
    public static final String APP_NAME_GAUSSIAN = "GAUSSIAN";
    public static final String APP_NAME_GAMESS = "GAMES";
    public static final String APP_NAME_NWCHEM = "NWCHEM";
    public static final String APP_NAME_MOLPRO = "MOLPRO";
    private static Settings ourInstance = new Settings();
    public static boolean authenticatedGridChem = true;
    public static String gridchemusername = "master";
    public static String defaultDirStr = "";
    public static String fileSeparator = File.separator;
    private static String applicationDataDir = Settings.class.getResource("/nanocad").getPath();
    public static String jobDir = Settings.class.getResource("/nanocad").getPath();

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }

    public static String getApplicationDataDir() {
        return applicationDataDir;
    }
}
