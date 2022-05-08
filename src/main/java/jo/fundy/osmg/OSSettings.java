package jo.fundy.osmg;

import jo.fundy.WebServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class OSSettings {

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void loadSettings(){
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " Your Current Device is using: &f" + OSManager.getOSName()));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " Your OS Version of your Device: &f" + OSManager.getOSVersion()));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " Processor of your Device: &f" + OSManager.getOSArch()));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix));
    }
}
