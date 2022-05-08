package jo.fundy.versions;

import org.bukkit.Bukkit;

public class Detector {

    public static String usedVersion(){
        String version = Bukkit.getBukkitVersion().split("-")[0];
        switch (version) {
            case "1.18.2":
            case "1.18.1":
            case "1.18":
            case "1.17.1":
            case "1.17":
            case "1.16.5":
            case "1.16.4":
            case "1.16.3":
            case "1.16.2":
            case "1.16.1":
            case "1.15.2":
            case "1.15.1":
            case "1.15":
            case "1.14.4":
            case "1.14.3":
            case "1.14.2":
            case "1.14.1":
            case "1.14":
            case "1.13.3":
            case "1.13.2":
            case "1.13.1":
            case "1.13":
            case "1.12.2":
            case "1.12.1":
            case "1.12":
            case "1.11.2":
            case "1.11.1":
            case "1.11":
            case "1.10.2":
            case "1.10.1":
            case "1.10":
            case "1.9.4":
            case "1.9.3":
            case "1.9.2":
            case "1.9.1":
            case "1.9":
            case "1.8.9":
            case "1.8.8":
            case "1.8.7":
            case "1.8.6":
            case "1.8.5":
            case "1.8.4":
            case "1.8.3":
            case "1.8.2":
            case "1.8.1":
            case "1.8":
                break;

        }
        return version;
    }
}
