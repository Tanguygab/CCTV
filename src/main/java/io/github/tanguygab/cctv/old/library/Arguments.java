package io.github.tanguygab.cctv.old.library;

import org.bukkit.ChatColor;

public class Arguments {
  
  public static String no_perms = ChatColor.RED + "You don't have the right permission to do that!";
  
  public static String player_not_found = ChatColor.RED + "Are you sure that this player has played on this server before?";

  
  public static String wrong_syntax = ChatColor.RED + "There went something wrong with the command syntax!";
  
  public static String list = ChatColor.GOLD + "- " + ChatColor.GRAY + "%ID%";
  
  public static String list_admin = ChatColor.GOLD + "- " + "(%Player%) " + ChatColor.GRAY + "%ID%";

  public static String camera_not_found = ChatColor.RED + "Camera has not been found!";


  public static String list_search = ChatColor.YELLOW + "These are the results for %search% %value%";
  
  public static String list_no_result = ChatColor.RED + "We couldn't find any results for %search% %value%!";
  
  public static String camera_id = ChatColor.YELLOW + "Camera ID: %CameraID%";

  public static String group_no_cameras_added = ChatColor.RED + "This group hasn't got any Cameras added!";
  
  public static String group_already_exist = ChatColor.RED + "Group already exist!";
  
  public static String group_not_found = ChatColor.RED + "Group has not been found!";
  
  public static String group_not_exist = ChatColor.RED + "That group does not exist!";
  
  public static String group_camera_already_added = ChatColor.RED + "Camera has already been added to that group!";
  
  public static String group_camera_added = ChatColor.GREEN + "Camera has been added to the group!";
  
  public static String group_or_camera_not_exist = ChatColor.RED + "That Camera or Group does not exist!";
  
  public static String group_delete_camera = ChatColor.GREEN + "Camera has been deleted!";
  
  public static String group_contains_not_camera = ChatColor.RED + "This group does not contain a camera with that ID!";
  
  public static String group_set_to_computer = ChatColor.GREEN + "Group has been set to this PC!";
  
  public static String group_removed_from_computer = ChatColor.GREEN + "Group has been removed from that Computer!";
  
  public static String group_delete = ChatColor.GREEN + "Group has been deleted!";
  
  public static String group_create = ChatColor.GREEN + "Group has been made!";
  
  public static String group_only_change_your_own = ChatColor.RED + "You can only change your own groups!";
  
  public static String group_player_already_owner = ChatColor.RED + "That player is already the owner of that group!";
  
  public static String group_id = ChatColor.YELLOW + "Group ID: %GroupID%";
  
  public static String group_owner_set_to = ChatColor.GOLD + "Group owner is set to " + ChatColor.GREEN + "%Player%" + ChatColor.GOLD + "!";
  
  public static String group_renamed_to = ChatColor.GREEN + "Group is renamed to '%GroupID%'!";
  
  public static String computer_not_allowed = ChatColor.RED + "You aren't allowed to see these camera's on this Computer!";
  
  public static String computer_no_group_set = ChatColor.RED + "That block already had no Group assigned to it!";
  
  public static String computer_create = ChatColor.GREEN + "Computer has been created!";

  public static String computer_not_stair = ChatColor.RED + "This block is not a Nether Brick Stairs";
  
  public static String computer_exist = ChatColor.RED + "This block already is a Computer!";
  
  public static String computer_not_exist = ChatColor.RED + "This computer does not exist!";
  
  public static String computer_delete = ChatColor.RED + "Computer has been deleted!";
  
  public static String computer_not_found = ChatColor.RED + "Computer has not been found!";
  
  public static String computer_change_no_perms = ChatColor.RED + "You may not edit other people's Computer!";
  
  public static String computer_only_owner_change_owner_no_perm = ChatColor.RED + "Only the owner of this computer can change the owner!";
  
  public static String computer_owner_set = ChatColor.GREEN + "Owner has been set!";
  
  public static String camera_deleted_because_bugged = "&cSorry but this camera was bugged, so we removed it!";
  
  public static String computer_id = ChatColor.YELLOW + "Computer ID: %ComputerID%";
  
  public static String gui_camera_settings = "&eSETTINGS";
  
  public static String gui_camera_delete = "&eDelete Camera %CameraID%";
  
  public static String gui_computer_default = "&eCCTV (page: %page%)";
  
  public static String gui_computer_options = "&eOPTIONS";
  
  public static String gui_computer_setgroup = "&eSet CameraGroup (page: %page%)";
  
  public static String gui_computer_removeplayer = "&cRemove Player (page: %page%)";
  
  public static String gui_computer_default_item_option = "&6Options";
  
  public static String gui_computer_default_item_next_page = "&8Next Page";
  
  public static String gui_computer_default_item_prev_page = "&8Previous Page";
  
  public static String gui_computer_default_item_exit = "&4Exit";
  
  public static String gui_computer_default_item_back = "&8Back";
  
  public static String gui_computer_options_item_setcameragroup = "&aSet CameraGroup";
  
  public static String gui_computer_options_item_addplayer = "&aAdd Player";
  
  public static String gui_computer_options_item_removeplayer = "&cRemove Player";
  
  public static String gui_camera_delete_item_cancel = "&cCancel";
  
  public static String gui_camera_delete_item_delete = "&2Delete";
  
  public static String item_camera_view_option = "&6Options";
  
  public static String item_camera_view_rotate_left = "&6Rotate Left";
  
  public static String item_camera_view_rotate_right = "&6Rotate Right";
  
  public static String item_camera_view_group_prev = "&bPrevious Camera";
  
  public static String item_camera_view_group_next = "&bNext Camera";
  
  public static String item_camera_view_exit = "&4Exit";
  
  public static String item_camera_view_options_nightvision_off = "&6Nightvision: &4Off";
  
  public static String item_camera_view_options_nightvision_on = "&6Nightvision: &aOn";
  
  public static String item_camera_view_options_zoom_off = "&6Zoom: &4Off";
  
  public static String item_camera_view_options_zoom = "&6Zoom: &a%level%x";
  
  public static String item_camera_view_options_back = "&8Back";
  
  public static String item_camera_view_options_spot = "&6Spotting";
  
  public static String chat_set_name_to_add = "&aSet the player name that you would like to add in the chat!";
  
  public static String chat_type_exit = "&aType 'exit' to exiting player adding!";
  
  public static String camera_item_name = ChatColor.translateAlternateColorCodes('&', "&9Camera");

}
