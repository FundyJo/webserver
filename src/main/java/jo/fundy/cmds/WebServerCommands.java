package jo.fundy.cmds;

import jo.fundy.WebServer;
import jo.fundy.handlers.HttpServerHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WebServerCommands implements CommandExecutor {
    private static final JavaPlugin plugin = WebServer.getPlugin(WebServer.class);

    public boolean onCommand(CommandSender sender, Command cmd, String tag, String[] args) {
        if (cmd.getName().equals("webserver")) {
            if (sender instanceof Player) {

                Player player = (Player) sender;

                if (player.hasPermission("webserver.admin") || player.isOp()) {
                    return serverCommandFunctions(sender, args);
                }

            } else if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                return serverCommandFunctions(sender, args);
            }
        }
        return false;
    }

    public static void sendTitle(final CommandSender sender,String title, String subtitle){
        if (sender instanceof Player){
            ((Player) sender).sendTitle(ChatColor.translateAlternateColorCodes('&',title),ChatColor.translateAlternateColorCodes('&',subtitle));
        }
    }

    private static boolean serverCommandFunctions(final CommandSender sender, String[] args) {
        switch (args[0]) {
            case "reload":
                WebServer.reloadConfiguration();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  All Files are Successfuly Reloaded and now enabled again!"));
                sendTitle(sender,"&c&lReloading","&fWebServer");
                return true;

            case "version":
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix +
                        ChatColor.RESET + ChatColor.BOLD + "version 1.4.0 (beta)"));
                return true;
            case "stop":
                if (!HttpServerHandler.isRunning()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Shutingdown all runned Taskes"));
                    return true;
                }
                try {
                    HttpServerHandler.stop();
                    sendTitle(sender,"&c&lShuting Down","&fWebServer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!HttpServerHandler.isRunning()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Website Successfuly shutted down!"));
                    return true;
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Website can't shutdown unknown problem"));
                return true;
            case "start":
                if (HttpServerHandler.isRunning()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Your Website is already running?!"));
                    return true;
                }
                try {
                    HttpServerHandler.start();
                    sendTitle(sender,"&c&lStarting ...","&fWebServer");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (HttpServerHandler.isRunning()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Your Website is already running?!"));
                    return true;
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Website can't shutdown unknown problem"));
                return true;
            case "info":
                if (args[1].equals("discord")){
                    TextComponent message = new TextComponent("§9§lDiscord");
                    TextComponent message_real = new TextComponent(WebServer.prefix + " Join our Discord Server on ");

                    message_real.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&fJoin the Community"))));
                    message_real.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/wUDUTP5hPW" ) );

                    message_real.addExtra(message);

                    sender.spigot().sendMessage(message_real);
                }else if (args[1].equals("commands")){

                    TextComponent nextpage = new TextComponent("§f§l»");
                    TextComponent skipper = new TextComponent(WebServer.prefix + "                                            ");

                    skipper.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/webserver info commands 1"));

                    skipper.addExtra(nextpage);

                    TextComponent nextpage1 = new TextComponent("§f§l«");
                    TextComponent skipper1 = new TextComponent(WebServer.prefix + "");

                    skipper1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/webserver info commands"));

                    skipper1.addExtra(nextpage1);

                    if (args.length == 2) {

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " -----------[WebServer]-----------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " Page: 0"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " "));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f&l Stop &f(WebServer)"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f will set your Webserver Offline"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " "));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f&l Restart &f(WebServer)"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f will reboot the WebServer"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " "));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f&l Info &f(WebServer)"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f show you all the second Commands!"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "" ));
                        sender.spigot().sendMessage(skipper);
                    }else if (args.length == 3 && args[2].equals("1")){

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " -----------[WebServer]-----------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " Page: 1"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " "));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f&l Reload &f(WebServer)"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f reload your Config files"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " "));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f&l Version &f(WebServer)"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " &f show you the Plugin Version"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "" ));
                        sender.spigot().sendMessage(skipper1);
                    }
                }
                return true;
            case "restart":
                if (!HttpServerHandler.isRunning()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + " Website not running try to start now!"));
                    try {
                        HttpServerHandler.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (HttpServerHandler.isRunning()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Website is running!"));
                        return true;
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Impossible to start Website! try to reload if you can"));
                    return true;
                }
                try {
                    HttpServerHandler.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    (new BukkitRunnable() {
                        public void run() {
                            try {
                                HttpServerHandler.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    ).runTaskLater(plugin, 60L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                (new BukkitRunnable() {
                    public void run() {
                        if (HttpServerHandler.isRunning()) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Your Website is restarted with success"));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebServer.prefix + "  Not possible to restart your website!"));
                        }
                    }
                }
                ).runTaskLater(plugin, 60L);
                return true;
        }
        return false;
    }
}