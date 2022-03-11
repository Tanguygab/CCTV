package io.github.tanguygab.cctv.old.records;

import java.util.ArrayList;
import org.bukkit.Location;

public class LastClickedRecord {
  public static class LastClickedRec {
    public String uuid;
    
    public Location loc;
  }
  
  public static ArrayList<LastClickedRec> lastclickedComputer = new ArrayList<>();
}
