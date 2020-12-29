package me.lokka30.littlethings.commands;

import me.lokka30.littlethings.LittleThings;
import me.lokka30.microlib.MicroUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTCommand implements TabExecutor {

    private final LittleThings instance;

    public LTCommand(final LittleThings instance) {
        this.instance = instance;
    }

    private List<String> getColoredListFromConfig(String path) {
        List<String> messages = new ArrayList<>();

        for (String message : instance.getConfig().getStringList(path)) {
            message = message.replace("%prefix%",
                    Objects.requireNonNull(instance.getConfig().getString("messages.prefix")));
            message = MicroUtils.colorize(message);

            messages.add(message);
        }

        return messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("littlethings.command")) {
            if (args.length == 0) {
                getColoredListFromConfig("messages.main").forEach(message -> {
                    message = message.replace("%label%", label);
                    message = message.replace("%version%", instance.getDescription().getVersion());
                    sender.sendMessage(message);
                });

            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("littlethings.reload")) {
                    getColoredListFromConfig("messages.reload.start").forEach(sender::sendMessage);
                    instance.reloadConfig();
                    instance.reloadModules();
                    getColoredListFromConfig("messages.reload.finish").forEach(sender::sendMessage);
                } else {
                    getColoredListFromConfig("messages.no-permission").forEach(sender::sendMessage);
                }
            } else {
                getColoredListFromConfig("messages.usage").forEach(message -> sender.sendMessage(message.replace("%label%", label)));
            }
        } else {
            getColoredListFromConfig("messages.no-permission").forEach(sender::sendMessage);
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
