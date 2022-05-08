package jo.fundy.watchdog;

import jo.fundy.WebServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerMessanger implements Listener {

    public static JavaPlugin plugin = WebServer.getPlugin(WebServer.class);

    @EventHandler
    public static void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        if (e.getPlayer().hasPermission("webserver.admin") || e.getPlayer().isOp()){
            if (e.getPlayer() instanceof Player && (plugin.getConfig().getString("language").equals("NONE"))) {
                 player.sendMessage(ChatColor.translateAlternateColorCodes('&',WebServer.prefix + " Hey you dont have set the Language! Please set the Language to DE or EN"));
            }
        }
    }
}
