package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.ListMenu;
import io.github.tanguygab.cctv.menus.cameras.CameraMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ComputerMainMenu extends ListMenu {

    protected final Computer computer;

    public ComputerMainMenu(Player p, Computer computer) {
        super(p);
        this.computer = computer;
    }

    @Override
    protected String getTitle(int page) {
        return lang.getGuiComputerDefault(page);
    }

    private boolean showCoords() {
        return !cctv.hasToggledComputerCoords(player);
    }
    private boolean canEdit() {
        return computer.getOwner().equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other");
    }

    @Override
    protected void onOpen() {
        addClickableItem(45,Heads.EXIT.get());
        addClickableItem(0, getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        addClickableItem(9, getItem(Material.COMPASS,(showCoords() ? ChatColor.GREEN : ChatColor.RED)+lang.GUI_COMPUTER_TOGGLE_COORDS));

        if (computer.isAdmin()) {
            List<Computable> list = new ArrayList<>();
            list.addAll(cctv.getCameras().values());
            list.addAll(cctv.getGroups().values());
            list(list,this::loadItem);
        } else list(computer.getCameras(),this::loadItem);
    }

    @Override
    protected void onClick(String name, ClickType click) {
        if (name.startsWith("group.")) {
            CameraGroup group = cctv.getGroups().get(name.substring(6));
            if (group == null) {
                player.sendMessage(lang.GROUP_NOT_FOUND);
                return;
            }
            handleClick(click, group, () -> {
                if (!group.getOwner().equals(player.getUniqueId().toString()) && !player.hasPermission("cctv.group.other"))
                    player.sendMessage(lang.NO_PERMISSIONS);
                player.closeInventory();
                player.performCommand("cctv group info "+group.getName());
            });
            return;
        }

        Camera camera = cctv.getCameras().get(name);
        if (camera == null) {
            player.sendMessage(lang.CAMERA_NOT_FOUND);
            return;
        }
        handleClick(click,camera,()->{
            if (!camera.getOwner().equals(player.getUniqueId().toString()) && !player.hasPermission("cctv.camera.other"))
                player.sendMessage(lang.NO_PERMISSIONS);
            else open(new CameraMenu(player,camera));
        });
    }

    @Override
    protected void onClick(int slot) {
        switch (slot) {
            case 45 -> player.closeInventory();
            case 0 -> {
                if (canEdit()) open(new ComputerOptionsMenu(player,computer));
                else player.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
            }
            case 9 -> {
                cctv.toggleComputerCoords(player);
                open();
            }
        }
    }

    private void loadItem(Computable computable) {
        ItemStack item = computable instanceof Camera camera
                ? getItem(cctv.getCustomHeads().get(camera.getSkin()),cctv.getCustomHeads().getChatColor(camera.getSkin())+computable.getName())
                : getItem(((CameraGroup)computable).getIcon(),computable.getName());

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        Location loc = showCoords() && computable instanceof Camera camera ? camera.getLocation() : null;
        String lore = (loc != null ? "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_X+posFormat.format(loc.getX())
                + "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_Y+posFormat.format(loc.getY())
                + "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_Z+posFormat.format(loc.getZ())+"\n" : "")
                + "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_VIEW
                + "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_EDIT
                + "\n\n"+lang.GUI_COMPUTER_CAMERA_ITEM_GO_UP
                + "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_GO_DOWN
                + (canEdit() ? "\n"+lang.GUI_COMPUTER_CAMERA_ITEM_REMOVE : "");
        meta.setLore(List.of(lore.split("\n")));
        setMeta(meta,(computable instanceof CameraGroup ? "group.":"")+computable.getName());
        item.setItemMeta(meta);
        inv.addItem(item);
    }

    private void handleClick(ClickType click, Computable computable, Runnable rightClick) {
        switch (click) {
            case LEFT -> {
                cctv.getCameras().viewCamera(player, computable, computer);
                player.closeInventory();
            }
            case RIGHT -> rightClick.run();
            case SHIFT_LEFT, SHIFT_RIGHT -> {
                int bound = click == ClickType.SHIFT_LEFT ? 0 : computer.getCameras().size()-1;
                int inc = click == ClickType.SHIFT_LEFT ? -1 : 1;

                int index = computer.getCameras().indexOf(computable);
                if (index == bound) return;
                computer.removeCamera(computable);
                computer.getCameras().add(index+inc,computable);
                open();
            }
            case DROP -> {
                if (!canEdit()) return;
                computer.removeCamera(computable);
                player.sendMessage(lang.getEditCameras(false,true,computable instanceof Camera,true));
                open();
            }
        }
    }
}
