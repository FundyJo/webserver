package jo.fundy.lib;

public class ErrorPages {

    public static String createButton(String name, String addr) {
        String string = "<a class=\"border-button\" href=\"https://www.spigotmc.org/resources/spigot-http-server-beta.37999/\" target=\"_blank\">{NAME}</a>";
        string = string.replace("{NAME}", name);
        string = string.replace("{ADDR}", addr);
        return string;
    }
}