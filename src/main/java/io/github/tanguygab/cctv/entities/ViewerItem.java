package io.github.tanguygab.cctv.entities;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class ViewerItem {

    private final int slot;
    private final ItemStack item;
    private final boolean onlyShowWhenGroup;
    private final List<String> commands;

    public void giveItem(Player player, boolean isGroup) {
        if (isGroup || !onlyShowWhenGroup) player.getInventory().setItem(slot, item);
    }

    public void runCommands(Player player) {
        commands.forEach(command -> player.performCommand(command.replace("%player%", player.getName())));
    }
}
