package io.github.tanguygab.cctv.old.functions;

import java.util.Calendar;
import java.util.Date;
import io.github.tanguygab.cctv.old.CooldownRecord;
import org.bukkit.entity.Player;

public class cooldownfunctions {
  public static boolean cooldownRecordExist(Player player) {
    for (CooldownRecord.cooldownRec rec : CooldownRecord.cooldowns) {
      if (rec.uuid.equals(player.getUniqueId().toString()))
        return true; 
    } 
    return false;
  }
  
  public static CooldownRecord.cooldownRec getCooldownRecordFromPlayer(Player player) {
    for (CooldownRecord.cooldownRec rec : CooldownRecord.cooldowns) {
      if (rec.uuid.equals(player.getUniqueId().toString()))
        return rec; 
    } 
    return null;
  }
  
  public static void addCoolDown(Player player, int cooldown) {
    if (!cooldownRecordExist(player)) {
      CooldownRecord.cooldownRec record = new CooldownRecord.cooldownRec();
      CooldownRecord.cooldowns.add(record);
      record.uuid = player.getUniqueId().toString();
      record.name = player.getName();
    } 
    CooldownRecord.cooldownRec rec = getCooldownRecordFromPlayer(player);
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.SECOND, cooldown);
    rec.cooldown = cal.getTime();
  }
  
  public static boolean isCooldownActive(Player player) {
    if (cooldownRecordExist(player)) {
      CooldownRecord.cooldownRec rec = getCooldownRecordFromPlayer(player);
      Calendar cal = Calendar.getInstance();
      Date date = cal.getTime();
      if (rec.cooldown.getTime() >= date.getTime())
        return true; 
    } 
    return false;
  }
}
