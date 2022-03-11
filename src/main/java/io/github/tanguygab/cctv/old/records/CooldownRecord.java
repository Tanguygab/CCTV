package io.github.tanguygab.cctv.old.records;

import java.util.ArrayList;
import java.util.Date;

public class CooldownRecord {
  public static class cooldownRec {
    public String name;
    
    public String uuid;
    
    public Date cooldown;
  }
  
  public static ArrayList<cooldownRec> cooldowns = new ArrayList<>();
}

