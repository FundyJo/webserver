package jo.fundy.events;

import jo.fundy.WebServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public static void onJoin(PlayerJoinEvent e){
        e.setJoinMessage(WebServer.prefix + " Welcome to the Server " + e.getPlayer().getName());
    }
}
