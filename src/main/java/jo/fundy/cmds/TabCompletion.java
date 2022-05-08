package jo.fundy.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        Player player = (Player) sender;

        if (sender instanceof Player) {
            if (args.length == 1) {
                if (player.hasPermission("webserver.stop") || player.hasPermission("webserver.*"))
                    commands.add("stop");
                if (player.hasPermission("webserver.start") || player.hasPermission("webserver.*"))
                    commands.add("start");
                if (player.hasPermission("webserver.reload") || player.hasPermission("webserver.*"))
                    commands.add("reload");
                if (player.hasPermission("webserver.restart") || player.hasPermission("webserver.*"))
                    commands.add("restart");
                if (player.hasPermission("webserver.version") || player.hasPermission("webserver.*"))
                    commands.add("version");
                if (player.hasPermission("webserver.info") || player.hasPermission("webserver.*"))
                    commands.add("info");

                StringUtil.copyPartialMatches(args[0], commands, completions);
            } else if (args.length == 2) {
                if (args[0].equals("info")) {
                    if (player.hasPermission("webserver.info.discord") || player.hasPermission("webserver.*"))
                        commands.add("discord");
                    if (player.hasPermission("webserver.info.commands") || player.hasPermission("webserver.*")) ;
                    commands.add("commands");
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }
}