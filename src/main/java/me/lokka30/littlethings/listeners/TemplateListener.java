package me.lokka30.littlethings.listeners;

import me.lokka30.littlethings.LittleThings;
import org.bukkit.event.Listener;

// This class only exists for ease of development. It isn't used in the plugin.

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class TemplateListener implements Listener {

    private final LittleThings instance;

    public TemplateListener(final LittleThings instance) {
        this.instance = instance;
    }
}
