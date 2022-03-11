package io.github.tanguygab.cctv.listeners;

import io.github.tanguygab.cctv.old.events.PlayerInventoryClickEvent;
public class InvClickEvent {

    public static void on(org.bukkit.event.inventory.InventoryClickEvent e) {
        new PlayerInventoryClickEvent().onInventoryClickEvent(e);
    }
}
