package io.github.tanguygab.cctv.old.events;

import io.github.tanguygab.cctv.CCTV;
import io.github.tanguygab.cctv.config.LanguageFile;
import io.github.tanguygab.cctv.old.functions.computerfunctions;
import io.github.tanguygab.cctv.old.records.ChatRecord;
import io.github.tanguygab.cctv.entities.Computer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent {
  public void onChat(AsyncPlayerChatEvent event) {
    LanguageFile lang = CCTV.get().getLang();
    Player player = event.getPlayer();
    if (CCTV.get().getViewers().exists(player) && !CCTV.get().getConfiguration().getBoolean("allowed_to_chat",false)) {
      event.setCancelled(true);
      return;
    }
    String[] Message = event.getMessage().split(" ");
    for (ChatRecord.ChatRec chat : ChatRecord.chats) {
      if (chat.uuid.equals(player.getUniqueId().toString()) && !chat.chat) {
        event.setCancelled(true);
        if (Message.length > 1) {
          player.sendMessage(ChatColor.RED + "A player name can't be longer that 1 word!");
        } else {
          if (Message[0].equals("exit")) {
            player.sendMessage(ChatColor.RED + "You have stopped adding players to the computer!");
            chat.chat = true;
            return;
          }
          byte b;
          int i;
          OfflinePlayer[] arrayOfOfflinePlayer;
          for (i = (arrayOfOfflinePlayer = Bukkit.getOfflinePlayers()).length, b = 0; b < i; ) {
            OfflinePlayer off = arrayOfOfflinePlayer[b];
            if (off != null && off.getName() != null && off.getName().equalsIgnoreCase(Message[0])) {
              Computer pc = computerfunctions.getComputerRecordFromLocation(computerfunctions.getLastClickedComputerFromPlayer(player));
              if (pc.isAllowedPlayers(off) || pc.getOwner().equals(off.getUniqueId().toString())) {
                player.sendMessage(lang.PLAYER_ALREADY_ADDED);
              } else {
                pc.getAllowedPlayers().add(off.getUniqueId().toString());
                player.sendMessage(lang.PLAYER_ADDED);
              }
              chat.chat = true;
              return;
            }
            b++;
          }
          player.sendMessage(lang.PLAYER_NOT_FOUND);
        }
        chat.chat = true;
      }
    }
  }
}

