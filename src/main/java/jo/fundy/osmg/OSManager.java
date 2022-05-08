package jo.fundy.osmg;

public class OSManager {

    protected static String getOSName(){
        return System.getProperty("os.name");
    }

    protected static String getOSVersion(){
        return System.getProperty("os.version");
    }

    protected static String getOSArch(){
        return System.getProperty("os.arch");
    }
}
