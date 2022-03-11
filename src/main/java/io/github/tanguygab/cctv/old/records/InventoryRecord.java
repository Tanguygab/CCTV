package io.github.tanguygab.cctv.old.records;

import java.util.ArrayList;
import java.util.Date;
import org.bukkit.inventory.ItemStack;

public class InventoryRecord {
  public static class InventoryRec {
    public String name;
    
    public String uuid;
    
    public ItemStack[] inv;
    
    public Date date;
  }
  
  public static ArrayList<InventoryRec> inventorylist = new ArrayList<>();
}

