package io.snw.tutorial.commands;

import io.snw.tutorial.data.Getters;
import io.snw.tutorial.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialView implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!Permissions.VIEW.hasPerm(sender)) {
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------\nAvailable tutorials:"));
        StringBuilder sb = new StringBuilder();
        if (Getters.getGetters().getAllTutorials().isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are currently no tutorials setup.\nSet one up with /tutorial create <name>");
            return true;
        }

        for (String tutorial : Getters.getGetters().getAllTutorials()) {
            if (sb.length() > 0) {
                sb.append(',');
                sb.append(' ');
            }
            sb.append(tutorial);
        }
        player.sendMessage(sb.toString());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6-------------------------------"));
        return true;
    }
}
