package io.github.tanguygab.cctv.menus.computers;

import io.github.tanguygab.cctv.entities.Camera;
import io.github.tanguygab.cctv.entities.CameraGroup;
import io.github.tanguygab.cctv.entities.Computable;
import io.github.tanguygab.cctv.entities.Computer;
import io.github.tanguygab.cctv.menus.cameras.CameraMenu;
import io.github.tanguygab.cctv.menus.ComputerMenu;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ComputerMainMenu extends ComputerMenu {

    public ComputerMainMenu(Player p, Computer computer) {
        super(p,computer);
    }

    @Override
    public void open() {
        inv = Bukkit.getServer().createInventory(null, 54, lang.getGuiComputerDefault(page));

        fillSlots(18);
        inv.setItem(0, getItem(Heads.OPTIONS,lang.GUI_COMPUTER_DEFAULT_ITEM_OPTION));
        inv.setItem(9, getItem(Material.COMPASS,(showCoords() ? ChatColor.GREEN : ChatColor.RED)+lang.GUI_COMPUTER_TOGGLE_COORDS));
        inv.setItem(27, Heads.MENU_NEXT.get());
        inv.setItem(36, Heads.MENU_PREVIOUS.get());
        inv.setItem(45, getItem(Heads.EXIT,lang.GUI_COMPUTER_DEFAULT_ITEM_EXIT));

        if (computer.isAdmin()) {
            List<Computable> list = new ArrayList<>();
            list.addAll(cctv.getCameras().values());
            list.addAll(cctv.getGroups().values());
            list(list,this::loadItem);
        } else list(computer.getCameras(),this::loadItem);
        p.openInventory(inv);
    }

    private boolean showCoords() {
        return !cctv.hasToggledComputerCoords(p);
    }

    private void loadItem(Computable computable) {
        ItemStack item = computable instanceof Camera camera
                ? getItem(cctv.getCustomHeads().get(camera.getSkin()), lang.GUI_COMPUTER_CAMERA_ITEM_NAME+camera.getName())
                : getItem(((CameraGroup)computable).getIcon(),lang.GUI_COMPUTER_GROUP_ITEM+computable.getName());

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
        lore = ChatColor.translateAlternateColorCodes('&',lore);
        meta.setLore(List.of(lore.split("\n")));
        meta.getPersistentDataContainer().set(computable instanceof Camera
                ? cctv.getCameras().cameraKey
                : itemKey, PersistentDataType.STRING, computable.getName());
        item.setItemMeta(meta);
        inv.addItem(item);
    }

    private boolean canEdit() {
        return computer.getOwner().equals(p.getUniqueId().toString()) || p.hasPermission("cctv.computer.other");
    }

    @Override
    public void onClick(ItemStack item, int slot, ClickType click) {
        switch (slot) {
            case 0 -> {
                if (canEdit()) open(new ComputerOptionsMenu(p,computer));
                else p.sendMessage(lang.COMPUTER_CHANGE_NO_PERMS);
            }
            case 9 -> {
                cctv.toggleComputerCoords(p);
                open();
            }
            case 27,36 -> setPage(slot == 27 ? page+1 : page-1);
            case 45 -> p.closeInventory();
            default -> {
                String cam = getKey(item,cctv.getCameras().cameraKey);
                if (cam == null) {
                    CameraGroup group = cctv.getGroups().get(getKey(item,itemKey));
                    if (group == null) {
                        p.sendMessage(lang.GROUP_NOT_FOUND);
                        return;
                    }
                    handleClick(click,group,()->{
                        if (!group.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.group.other"))
                            p.sendMessage(lang.NO_PERMISSIONS);
                        //else open(new GroupMenu(p,group));
                    });
                    return;
                }
                Camera camera = cctv.getCameras().get(cam);
                if (camera == null) {
                    p.sendMessage(lang.CAMERA_NOT_FOUND);
                    return;
                }
                handleClick(click,camera,()->{
                    if (!camera.getOwner().equals(p.getUniqueId().toString()) && !p.hasPermission("cctv.camera.other"))
                        p.sendMessage(lang.NO_PERMISSIONS);
                    else open(new CameraMenu(p,camera));
                });
            }
        }
    }

    private void handleClick(ClickType click, Computable computable, Runnable rightClick) {
        switch (click) {
            case RIGHT -> rightClick.run();
            case LEFT -> {
                cctv.getCameras().viewCamera(p, computable instanceof Camera camera ? camera : null, computable, computer);
                p.closeInventory();
            }
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
                p.sendMessage(lang.COMPUTER_CAMERA_REMOVED); // need group/camera check
                open();
            }
        }
    }
}
