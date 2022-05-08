package jo.fundy;

import java.io.File;

import jo.fundy.cmds.WebServerCommands;
import jo.fundy.cmds.TabCompletion;
import jo.fundy.events.PlayerEvents;
import jo.fundy.handlers.HttpServerHandler;
import jo.fundy.osmg.OSSettings;
import jo.fundy.versions.Detector;
import jo.fundy.watchdog.PlayerMessanger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;



public class WebServer extends JavaPlugin {

    public static final String pluginVersion = "version 1.3.0 (beta)";
    public static final double startTime = (System.currentTimeMillis() / 1000L);
    public static String prefix = ChatColor.translateAlternateColorCodes('&',"&c&lWebServer System &f| &7");


    public void onEnable() {

        System.out.println("°------------°[WebLauncher " + pluginVersion + "]°------------°");
        System.out.println(" this Plugin was developed by FundyJo i hope you enjoy it!");
        System.out.println(" ");
        System.out.println("          You are Currently Using the Version: " + Detector.usedVersion());
        System.out.println(" ");
        System.out.println(" Please note that this plugin will still have some Bugs and problems");
        System.out.println("      if you wanna you can give me a vote up on Spigot! ");
        System.out.println("    °------------°------------°------------°------------°");

        OSSettings.loadSettings();

        createConfig();

        getCommand("webserver").setExecutor(new WebServerCommands());

        getCommand("webserver").setTabCompleter(new TabCompletion());

        Bukkit.getPluginManager().registerEvents(new PlayerMessanger(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(),this);

        try {
            try {
                HttpServerHandler.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        try {
            HttpServerHandler.stop();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " WebServer Server has stopped."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createConfig() {
        try {
            File serverStore = new File(getDataFolder().getPath() + "/public_html");
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            if (!serverStore.exists()) {
                serverStore.mkdirs();
            }
            File config = new File(getDataFolder(), "config.yml");

            if (!config.exists()) {
                Bukkit.getConsoleSender().sendMessage((ChatColor.translateAlternateColorCodes('&',prefix + " Config.yml not found, creating it for you!")));
                saveDefaultConfig();
            }

            File index = new File(getDataFolder() + "/public_html/index.html");

            if (!index.exists() &&
                    getConfig().isSet("generated-index") &&
                    !getConfig().getBoolean("generated-index")) {
                saveResource("public_html/index.html", false);
                getConfig().set("generated-index", Boolean.TRUE);
                saveConfig();
            }

            File accessLogs = new File(getDataFolder() + "/access.logs");

            if (!accessLogs.exists()) {
                saveResource("access.logs", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfiguration() {
        Bukkit.getPluginManager().getPlugin("WebServer").reloadConfig();
    }


}