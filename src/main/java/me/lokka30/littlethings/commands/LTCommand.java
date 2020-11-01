package me.lokka30.littlethings.commands;

import me.lokka30.littlethings.LittleThings;
import me.lokka30.microlib.MicroUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class LTCommand implements TabExecutor {

    private final LittleThings instance;

    public LTCommand(final LittleThings instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("littlethings.command")) {
            if (args.length == 0) {
                sender.sendMessage(" ");
                sender.sendMessage(MicroUtils.colorize("&f&nAbout:"));
                sender.sendMessage(MicroUtils.colorize("&bLittleThings v" + instance.getDescription().getVersion() + "&7 by &flokka30"));
                sender.sendMessage(MicroUtils.colorize("&7LittleThings is a small plugin which allows server administrators to modify certain events such as disabling fire spread and adding arms to armor stands."));
                sender.sendMessage(" ");
                sender.sendMessage(MicroUtils.colorize("&f&nAvailable Commands:"));
                sender.sendMessage(MicroUtils.colorize("&8 &m->&b /" + label + " &8- &7View plugin information."));
                sender.sendMessage(MicroUtils.colorize("&8 &m->&b /" + label + " reload &8- &7Reload the config file."));
                sender.sendMessage(" ");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("littlethings.reload")) {
                    sender.sendMessage(MicroUtils.colorize("&b&lLittleThings: &7Reloading configuration..."));
                    instance.reloadConfig();
                    sender.sendMessage(MicroUtils.colorize("&b&lLittleThings: &7Reloading complete."));
                } else {
                    sender.sendMessage(MicroUtils.colorize("&b&lLittleThings: &7You don't have access to that."));
                }
            } else {
                sender.sendMessage(MicroUtils.colorize("&b&lLittleThings: &7Usage: &b/" + label + " [reload]"));
            }
        } else {
            sender.sendMessage(MicroUtils.colorize("&b&lLittleThings: &7You don't have access to that."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 0) {
            suggestions.add("reload");
        }

        return suggestions;
    }
}
