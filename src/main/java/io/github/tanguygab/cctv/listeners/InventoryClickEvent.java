package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.old.library.EventsHandler;

public class InventoryClickEvent {

    public static void on(org.bukkit.event.inventory.InventoryClickEvent e) {
        EventsHandler.playerinventoryclick.onInventoryClickEvent(e);
    }
}