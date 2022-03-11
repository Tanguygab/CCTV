package io.github.tanguygab.cctv.old.events;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.old.functions.camerafunctions;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.functions.groupfunctions;
import io.github.tanguygab.cctv.old.functions.viewfunctions;
import io.github.tanguygab.cctv.old.library.Arguments;
import io.github.tanguygab.cctv.old.records.ChatRecord;
import io.github.tanguygab.cctv.utils.CameraUtils;
import io.github.tanguygab.cctv.utils.Heads;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerInventoryClickEvent {
  public void onInventoryClickEvent(InventoryClickEvent event) {
    Player player = (Player)event.getWhoClicked();
    if (CCTV.get().getViewers().exists(player)) {
      event.setCancelled(true);
      if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
        return; 
      if (event.getView().getTitle() != null && event.getView().getTitle().matches(Arguments.gui_camera_settings.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"))) {
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
          ItemStack itemStack = event.getCurrentItem();
          viewfunctions.settingFunctions(player, itemStack);
        } 
        return;
      } 
      ItemStack item = event.getCurrentItem();
      viewfunctions.switchFunctions(player, item);
    } else if (event.getView() != null && event.getView().getTitle() != null && event.getView().getTitle().matches(Arguments.gui_computer_default.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\\\d+"))) {
      if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
        return; 
      event.setCancelled(true);
      ItemStack item = event.getCurrentItem();
      ComputerRecord.computerRec pc = computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player));
      if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_option)) {
        if (pc == null) {
          player.closeInventory();
          return;
        } 
        if (pc.owner.equals(player.getUniqueId().toString()) || player.hasPermission("cctv.computer.other")) {
          player.openInventory(optionsInv(player));
        } else {
          player.sendMessage(Arguments.no_perms);
        } 
      } else {
        if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_exit)) {
          player.closeInventory();
          return;
        } 
        if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).startsWith("Camera:")) {
          String CameraName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).substring(8);
          CameraUtils.viewCamera(CameraName, player, (computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player))).cameraGroup);
          player.closeInventory();
          return;
        } 
        if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_next_page)) {
          Pattern p = Pattern.compile(Arguments.gui_computer_default.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\(\\\\d+\\)"));
          Matcher m = p.matcher(event.getView().getTitle());
          if (!m.matches()) {
            Bukkit.getLogger().info("The gui of 'gui_computer_default' doesn't contain %page%!");
            return;
          } 
          int currentpage = Integer.parseInt(m.group(1));
          double maxpages = (((pc.cameraGroup != null) ? pc.cameraGroup.cameras.size() : 0) + 0.0D) / 48.0D;
          if (currentpage >= maxpages)
            return; 
          player.openInventory(CCTV(player, currentpage + 1));
        } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_prev_page)) {
          Pattern p = Pattern.compile(Arguments.gui_computer_default.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\(\\\\d+\\)"));
          Matcher m = p.matcher(event.getView().getTitle());
          if (!m.matches()) {
            Bukkit.getLogger().info("The gui of 'gui_computer_default' doesn't contain %page%!");
            return;
          } 
          int currentpage = Integer.parseInt(m.group(1));
          if (currentpage == 1)
            return; 
          player.openInventory(CCTV(player, currentpage - 1));
        } 
      } 
    } else if (event.getView() != null && event.getView().getTitle() != null && event.getView().getTitle().matches(Arguments.gui_computer_options.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"))) {
      event.setCancelled(true);
      if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
        return; 
      ItemStack item = event.getCurrentItem();
      if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_options_item_setcameragroup)) {
        player.openInventory(setCameraGroup(player, 1));
      } else {
        if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_options_item_addplayer)) {
          for (ChatRecord.ChatRec chatRec : ChatRecord.chats) {
            if (chatRec.uuid.equals(player.getUniqueId().toString())) {
              chatRec.chat = false;
              player.closeInventory();
              player.sendMessage(Arguments.chat_set_name_to_add);
              player.sendMessage(Arguments.chat_type_exit);
              return;
            } 
          } 
          ChatRecord.ChatRec chat = new ChatRecord.ChatRec();
          ChatRecord.chats.add(chat);
          chat.uuid = player.getUniqueId().toString();
          chat.chat = false;
          player.closeInventory();
          player.sendMessage(Arguments.chat_set_name_to_add);
          player.sendMessage(Arguments.chat_type_exit);
          return;
        } 
        if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_options_item_removeplayer)) {
          player.openInventory(removePlayer(player, 1));
        } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_back)) {
          camerafunctions.getCCTVFromComputer(player, computerfunctions.getLastClickedComputerFromPlayer(player));
        } 
      } 
    } else if (event.getView() != null && event.getView().getTitle() != null && event.getView().getTitle().matches(Arguments.gui_computer_setgroup.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\\\d+"))) {
      if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
        return; 
      event.setCancelled(true);
      ItemStack item = event.getCurrentItem();
      ArrayList<CameraGroupRecord.groupRec> groups = new ArrayList<>();
      for (String id : groupfunctions.getGroupsFromPlayer(player)) {
        if (groupfunctions.groupExist(id)) {
          CameraGroupRecord.groupRec rec = groupfunctions.getGroupFromID(id);
          groups.add(rec);
        } 
      } 
      if (ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()).startsWith("group:")) {
        String group = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()).substring(7);
        CameraGroupRecord.groupRec grouprec = groupfunctions.getGroupFromID(group);
        (computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player))).cameraGroup = grouprec;
        player.openInventory(optionsInv(player));
        player.sendMessage(ChatColor.GOLD + "Group has been changed!");
        player.sendMessage(ChatColor.YELLOW + "Set to: " + grouprec.id);
      } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_back)) {
        player.openInventory(optionsInv(player));
      } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_next_page)) {
        Pattern p = Pattern.compile(Arguments.gui_computer_setgroup.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\(\\\\d+\\)"));
        Matcher m = p.matcher(event.getView().getTitle());
        if (!m.matches()) {
          Bukkit.getLogger().info("The gui of 'gui_computer_setgroup' doesn't contain %page%!");
          return;
        } 
        int currentpage = Integer.parseInt(m.group(1));
        double maxpages = (((groups != null) ? groups.size() : 0) + 0.0D) / 48.0D;
        if (currentpage >= maxpages)
          return; 
        player.openInventory(setCameraGroup(player, currentpage + 1));
      } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_prev_page)) {
        Pattern p = Pattern.compile(Arguments.gui_computer_setgroup.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\(\\\\d+\\)"));
        Matcher m = p.matcher(event.getView().getTitle());
        if (!m.matches()) {
          Bukkit.getLogger().info("The gui of 'gui_computer_setgroup' doesn't contain %page%!");
          return;
        } 
        int currentpage = Integer.parseInt(m.group(1));
        if (currentpage == 1)
          return; 
        player.openInventory(setCameraGroup(player, currentpage - 1));
      } 
    } else if (event.getView() != null && event.getView().getTitle() != null && event.getView().getTitle().matches(Arguments.gui_computer_removeplayer.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\\\d+"))) {
      if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
        return; 
      event.setCancelled(true);
      ItemStack item = event.getCurrentItem();
      ComputerRecord.computerRec pc = computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player));
      if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_back)) {
        player.openInventory(optionsInv(player));
      } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_next_page)) {
        Pattern p = Pattern.compile(Arguments.gui_computer_removeplayer.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\(\\\\d+\\)"));
        Matcher m = p.matcher(event.getView().getTitle());
        if (!m.matches()) {
          Bukkit.getLogger().info("The gui of 'gui_computer_removeplayer' doesn't contain %page%!");
          return;
        } 
        int currentpage = Integer.parseInt(m.group(1));
        double maxpages = (((pc.allowedPlayers != null) ? pc.allowedPlayers.size() : 0) + 0.0D) / 48.0D;
        if (currentpage >= maxpages)
          return; 
        player.openInventory(removePlayer(player, currentpage + 1));
      } else if (item.getItemMeta().getDisplayName().equals(Arguments.gui_computer_default_item_prev_page)) {
        Pattern p = Pattern.compile(Arguments.gui_computer_removeplayer.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%page%", "*\\(\\\\d+\\)"));
        Matcher m = p.matcher(event.getView().getTitle());
        if (!m.matches()) {
          Bukkit.getLogger().info("The gui of 'gui_computer_removeplayer' doesn't contain %page%!");
          return;
        } 
        int currentpage = Integer.parseInt(m.group(1));
        if (currentpage == 1)
          return; 
        player.openInventory(removePlayer(player, currentpage - 1));
      } else if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("player:")) {
        String play = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()).substring(8);
        OfflinePlayer off = Bukkit.getOfflinePlayer(play);
        for (int a = 0; a < pc.allowedPlayers.size(); a++) {
          String speler = pc.allowedPlayers.get(a);
          if (off.getUniqueId().toString().equals(speler)) {
            pc.allowedPlayers.remove(a);
            int currentpage = Integer.parseInt(ChatColor.stripColor(event.getView().getTitle().substring(23, event.getView().getTitle().length() - 1)));
            player.openInventory(removePlayer(player, currentpage));
            player.sendMessage(ChatColor.RED + "You removed player '" + play + "' from this computer!");
            return;
          } 
        } 
      } 
    } 
    if (event.getView() != null && event.getView().getTitle() != null && event.getView().getTitle().matches(Arguments.gui_camera_delete.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%CameraID%", "*.+"))) {
      if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
        return; 
      if (event.getSlot() == 3) {
        player.closeInventory();
      } else if (event.getSlot() == 5) {
        player.closeInventory();
        Pattern p = Pattern.compile(Arguments.gui_camera_delete.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("%CameraID%", "*\\(.+\\)"));
        Matcher m = p.matcher(event.getView().getTitle());
        if (!m.matches()) {
          Bukkit.getLogger().info("The gui of 'gui_computer_removeplayer' doesn't contain %page%!");
          return;
        } 
        String name = m.group(1);
        CCTV.get().getCameras().delete(name, player);
      } 
    } 
  }
  
  public Inventory optionsInv(Player player) {
    Inventory inv = Bukkit.createInventory(null, 9, Arguments.gui_computer_options);
    ItemStack setGroup = Heads.CHEST.get();
    ItemMeta setGroupMeta = setGroup.getItemMeta();
    setGroupMeta.setDisplayName(Arguments.gui_computer_options_item_setcameragroup);
    setGroup.setItemMeta(setGroupMeta);
    ItemStack removePlayer = Heads.RED_MIN.get();
    ItemMeta removePlayerMeta = removePlayer.getItemMeta();
    removePlayerMeta.setDisplayName(Arguments.gui_computer_options_item_removeplayer);
    removePlayer.setItemMeta(removePlayerMeta);
    ItemStack addPlayer = Heads.GREEN_PLUS.get();
    ItemMeta addPlayerMeta = addPlayer.getItemMeta();
    addPlayerMeta.setDisplayName(Arguments.gui_computer_options_item_addplayer);
    addPlayer.setItemMeta(addPlayerMeta);
    inv.setItem(3, setGroup);
    inv.setItem(4, addPlayer);
    inv.setItem(5, removePlayer);
    ItemStack back = Heads.ARROW_BACK.get();
    ItemMeta backMeta = back.getItemMeta();
    backMeta.setDisplayName(Arguments.gui_computer_default_item_back);
    back.setItemMeta(backMeta);
    inv.setItem(0, back);
    return inv;
  }
  
  public Inventory setCameraGroup(Player player, int page) {
    Inventory inv = Bukkit.createInventory(null, 54, Arguments.gui_computer_setgroup.replaceAll("%page%", page+""));
    ItemStack holder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    ItemMeta holdermeta = holder.getItemMeta();
    holdermeta.setDisplayName(" ");
    holder.setItemMeta(holdermeta);
    ItemStack pageNext = Heads.ARROW_RIGHT_IRON.get();
    ItemMeta pageNextM = pageNext.getItemMeta();
    pageNextM.setDisplayName(Arguments.gui_computer_default_item_next_page);
    pageNext.setItemMeta(pageNextM);
    ItemStack pageBack = Heads.ARROW_LEFT_IRON.get();
    ItemMeta pageBackM = pageBack.getItemMeta();
    pageBackM.setDisplayName(Arguments.gui_computer_default_item_prev_page);
    pageBack.setItemMeta(pageBackM);
    ItemStack back = Heads.ARROW_BACK.get();
    ItemMeta backMeta = back.getItemMeta();
    backMeta.setDisplayName(Arguments.gui_computer_default_item_back);
    back.setItemMeta(backMeta);
    inv.setItem(0, holder);
    inv.setItem(9, holder);
    inv.setItem(18, holder);
    inv.setItem(27, pageNext);
    inv.setItem(36, pageBack);
    inv.setItem(45, back);
    ArrayList<CameraGroupRecord.groupRec> groups = new ArrayList<>();
    for (String id : groupfunctions.getGroupsFromPlayer(player)) {
      if (groupfunctions.groupExist(id)) {
        CameraGroupRecord.groupRec rec = groupfunctions.getGroupFromID(id);
        groups.add(rec);
      } 
    } 
    for (int a = (page - 1) * 48; a < 48 * page && a < groups.size(); a++) {
      CameraGroupRecord.groupRec rec = groups.get(a);
      ItemStack group = Heads.CAMERA_1.get();
      ItemMeta GroupM = group.getItemMeta();
      GroupM.setDisplayName(ChatColor.GOLD + "Group: " + ChatColor.YELLOW + rec.id);
      group.setItemMeta(GroupM);
      inv.addItem(group);
    } 
    return inv;
  }
  
  public Inventory removePlayer(Player player, int page) {
    Inventory inv = Bukkit.createInventory(null, 54, Arguments.gui_computer_removeplayer.replaceAll("%page%", page+""));
    ComputerRecord.computerRec pc = computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player));
    ItemStack holder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    ItemMeta holdermeta = holder.getItemMeta();
    holdermeta.setDisplayName(" ");
    holder.setItemMeta(holdermeta);
    ItemStack pageNext = Heads.ARROW_RIGHT_IRON.get();
    ItemMeta pageNextM = pageNext.getItemMeta();
    pageNextM.setDisplayName(Arguments.gui_computer_default_item_next_page);
    pageNext.setItemMeta(pageNextM);
    ItemStack pageBack = Heads.ARROW_LEFT_IRON.get();
    ItemMeta pageBackM = pageBack.getItemMeta();
    pageBackM.setDisplayName(Arguments.gui_computer_default_item_prev_page);
    pageBack.setItemMeta(pageBackM);
    ItemStack back = Heads.ARROW_BACK.get();
    ItemMeta backMeta = back.getItemMeta();
    backMeta.setDisplayName(Arguments.gui_computer_default_item_back);
    back.setItemMeta(backMeta);
    inv.setItem(0, holder);
    inv.setItem(9, holder);
    inv.setItem(18, holder);
    inv.setItem(27, pageNext);
    inv.setItem(36, pageBack);
    inv.setItem(45, back);
    for (int a = (page - 1) * 48; a < 48 * page && a < pc.allowedPlayers.size(); a++) {
      String play = pc.allowedPlayers.get(a);
      OfflinePlayer off = Bukkit.getOfflinePlayer(UUID.fromString(play));
      ItemStack item = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta)item.getItemMeta();
      meta.setOwningPlayer(off);
      meta.setDisplayName(ChatColor.YELLOW + "player: " + off.getName());
      item.setItemMeta(meta);
      inv.addItem(item);
    } 
    return inv;
  }
  
  public static Inventory CCTV(Player player, int page) {
    ComputerRecord.computerRec pc = computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player));
    Inventory inv = Bukkit.createInventory(null, 54, Arguments.gui_computer_default.replaceAll("%page%", page+""));
    ItemStack setting = Heads.OPTIONS.get();
    ItemMeta settingM = setting.getItemMeta();
    settingM.setDisplayName(Arguments.gui_computer_default_item_option);
    setting.setItemMeta(settingM);
    ItemStack pageNext = Heads.ARROW_RIGHT_IRON.get();
    ItemMeta pageNextM = pageNext.getItemMeta();
    pageNextM.setDisplayName(Arguments.gui_computer_default_item_next_page);
    pageNext.setItemMeta(pageNextM);
    ItemStack pageBack = Heads.ARROW_LEFT.get();
    ItemMeta pageBackM = pageBack.getItemMeta();
    pageBackM.setDisplayName(Arguments.gui_computer_default_item_prev_page);
    pageBack.setItemMeta(pageBackM);
    ItemStack exit = Heads.EXIT.get();
    ItemMeta exitM = exit.getItemMeta();
    exitM.setDisplayName(Arguments.gui_computer_default_item_exit);
    exit.setItemMeta(exitM);
    ItemStack holder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    ItemMeta holdermeta = holder.getItemMeta();
    holdermeta.setDisplayName(" ");
    holder.setItemMeta(holdermeta);
    inv.setItem(0, setting);
    inv.setItem(9, holder);
    inv.setItem(18, holder);
    inv.setItem(27, pageNext);
    inv.setItem(36, pageBack);
    inv.setItem(45, exit);
    if (pc.cameraGroup != null)
      for (int a = (page - 1) * 48; a < 48 * page && a < pc.cameraGroup.cameras.size(); a++) {
        CameraRecord.cameraRec crec = pc.cameraGroup.cameras.get(a);
        ItemStack cam = Heads.CAMERA_1.get();
        ItemMeta meta = cam.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Camera: " + crec.id);
        cam.setAmount(1);
        cam.setItemMeta(meta);
        inv.addItem(cam);
      }  
    return inv;
  }
}
