package io.github.tanguygab.cctv.config;

import io.github.tanguygab.cctv.CCTV;
import org.bukkit.ChatColor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageFile extends YamlConfigurationFile {

    public LanguageFile(InputStream source, File destination) throws IllegalStateException, YAMLException, IOException {
        super(source, destination);
    }

    private String get(String str, String def) {
        return ChatColor.translateAlternateColorCodes('&',getString(str,def));
    }

    public final String NO_PERMISSIONS = get("no-permissions","&cYou don't have the right permission to do that!");
    public final String PLAYER_NOT_FOUND = get("player-not-found","&cThis player doesn't exist.");
    public final String PLAYER_ALREADY_ADDED = get("player-already-added","&cPlayer has already been added!");
    public final String PLAYER_ADDED = get("player-added","&aPlayer has been added!");
    public final String PLAYER_REMOVED = get("player-removed","&cPlayer has been removed!");
    public final String PLAYER_NOT_IN_LIST = get("player-not-in-list","&cThis player isn't in this list!");
    public final String TOO_MANY_PAGES = get("to-many-pages","&cThere aren't that many pages!");
    private final String LIST = get("list.list","&6- &7%ID%");
    public String getList(String id) {
        return LIST.replace("%ID%",id);
    }
    private final String LIST_ADMIN = get("list.admin","&6- (%player%) &7%ID%");
    public String getListAdmin(String player, String id) {
        return LIST_ADMIN.replace("%player%",player).replace("%ID%",id);
    }
    private final String LIST_SEARCH = get("list.search","&eResults for %search% %value%");
    public String getListSearch(String search, String value) {
        return LIST_SEARCH.replace("%search%",search).replace("%value%",value);
    }
    private final String LIST_NO_RESULT = get("list.search","&cWe couldn't find any results for %search% %value%!");
    public String getListNoResult(String search, String value) {
        return LIST_NO_RESULT.replace("%search%",search).replace("%value%",value);
    }
    public final String MAX_ROTATION = get("max-rotation","&cThis is the limit of rotation!");
    public final String NO_CAMERAS = get("no-cameras","&cThere aren't any cameras!");
    public final String SWITCHING_NOT_POSSIBLE = get("switching-not-possible","&cSwitching through cameras is not possible!");

    public final String CAMERA_CREATE = get("cameras.create","&aCamera created!");
    public final String CAMERA_DELETE = get("cameras.delete","&cCamera deleted!");
    public final String CAMERA_ALREADY_EXISTS = get("cameras.already-exists","&cThis camera already exists!");
    public final String CAMERA_NOT_FOUND = get("cameras.not-found","&cThis camera doesn't exist.");
    public final String CAMERA_ID = get("cameras.id","&eCamera ID: %cameraID%");
    public String getCameraID(String id) {
        return CAMERA_ID.replace("%cameraID%",id);
    }
    private final String CAMERA_OWNER_CHANGED = get("cameras.owner-changed","&6Camera owner is set to &a%player%&6!");
    public String getCameraOwnerChanged(String player) {
        return CAMERA_OWNER_CHANGED.replace("%player%",player);
    }
    public final String CAMERA_PLAYER_ALREADY_OWNER = get("cameras.player-already-owner","&cThat player is already the owner of that camera!");
    public final String CAMERA_CHANGE_NO_PERMS = get("cameras.change-no-perms","&cYou can only change your own cameras!");
    private final String CAMERA_RENAMED = get("cameras.renamed","&aCamera renamed to '%cameraID%'!");
    public String getCameraRenamed(String id) {
        return CAMERA_RENAMED.replace("%cameraID%",id);
    }
    public final String CAMERA_CONNECTING = get("cameras.connecting","&aConnecting...");
    public final String CAMERA_DISCONNECTING = get("cameras.disconnecting","&cDisconnecting...");
    private final String CAMERA_ENABLED = get("cameras.enabled","&aCamera &2%cameraID%&a is now &2enabled&a!");
    public String getCameraEnabled(String id) {
        return CAMERA_ENABLED.replace("%cameraID%",id);
    }
    private final String CAMERA_DISABLED = get("cameras.disabled","&aCamera &2%cameraID%&a is now &cdisabled&a!");
    public String getCameraDisabled(String id) {
        return CAMERA_DISABLED.replace("%cameraID%",id);
    }
    public final String CAMERA_OFFLINE = get("cameras.offline","&cThis camera is offline!");
    public final String CAMERA_OFFLINE_OVERRIDE = get("cameras.offline-override","&cNOTE: This camera is offline! You can view this camera because you can bypass it!");
    public final String CAMERA_ALREADY_ENABLED = get("cameras.already-enabled","&cThis camera is already enabled!");
    public final String CAMERA_ALREADY_DISABLED = get("cameras.already-disabled","&cThis camera is already disabled!");
    public final String CAMERA_MOVED = get("cameras.moved","&aCamera moved to your location!");
    private final String CAMERA_VIEW_COUNT = get("cameras.view-count","&aThere are currently &2%count%&a players watching camera &2%cameraID%&a!");
    public String getCameraViewCount(int count, String id) {
        return CAMERA_VIEW_COUNT.replace("%count%",count+"").replace("%cameraID%",id);
    }
    public final String CAMERA_DELETED_BECAUSE_BUGGED = get("cameras.deleted-because-bugged","&cSorry but this camera was bugged, so we removed it!");
    public final String CAMERA_ITEM_NAME = get("cameras.item-name", "&9Camera");

    public final String GROUP_CREATE = get("groups.create","&aGroup created!");
    public final String GROUP_DELETE = get("groups.delete","&aGroup deleted!");
    public final String GROUP_ALREADY_EXISTS = get("groups.already-exists","&cThis group already exists!");
    public final String GROUP_NOT_FOUND = get("groups.not-found","&cThis group doesn't exist!");
    private final String GROUP_ID = get("groups.id","&eGroup ID: %groupID%");
    public String getGroupID(String id) {
        return GROUP_ID.replace("%groupID%",id);
    }
    private final String GROUP_OWNER_CHANGED = get("groups.owner-changed","&6Group owner is set to &a%player%&6!");
    public String getGroupOwnerChanged(String player) {
        return GROUP_OWNER_CHANGED.replace("%player%",player);
    }
    public final String GROUP_PLAYER_ALREADY_OWNER = get("groups.player-already-owner","&cThis player is already the owner of that group!");
    private final String GROUP_RENAMED = get("groups.renamed","&aGroup renamed to '%groupID%'!");
    public String getGroupRenamed(String id) {
        return GROUP_RENAMED.replace("%groupID%",id);
    }
    public final String GROUP_NO_CAMERAS_ADDED = get("groups.no-cameras-added","&cThis group doesn't have any cameras!");
    public final String GROUP_CAMERA_ALREADY_ADDED = get("groups.camera-already-added","&cThis camera has already been added to that group!");
    public final String GROUP_CAMERA_ADDED = get("groups.camera-added","&aCamera added to the group!");
    public final String GROUP_REMOVE_CAMERA = get("groups.remove-camera","&aCamera removed from this group!");
    public final String GROUP_DOES_NOT_CONTAIN_CAMERA = get("groups.does-not-contain-camera","&cThis group does not contain a camera with that ID!");
    public final String GROUP_ASSIGNED_TO_COMPUTER = get("groups.assigned-to-computer","&aGroup assigned to this PC!");
    public final String GROUP_REMOVED_FROM_COMPUTER = get("groups.removed-from-computer","&aGroup removed from this computer!");

    public final String COMPUTER_CREATE = get("computers.create", "&aComputer created!");
    public final String COMPUTER_DELETE = get("computers.delete", "&cComputer deleted!");
    public final String COMPUTER_ALREADY_EXISTS = get("computers.already-exists", "&cThis block already is a computer!");
    public final String COMPUTER_NOT_FOUND = get("computers.not-found", "&cThis computer doesn't exist!");
    private final String COMPUTER_ID = get("computers.id", "&eComputer ID: %computerID%");
    public String getComputerID(String id) {
        return COMPUTER_ID.replace("%computerID%",id);
    }
    private final String COMPUTER_OWNER_CHANGED = get("computers.owner-changed", "&6Computer owner is set to &a%player%&6!");
    public String getComputerOwnerChanged(String player) {
        return COMPUTER_OWNER_CHANGED.replace("%player%",player);
    }
    public final String COMPUTER_CHANGE_NO_PERMS = get("computers.change-no-perms", "&cYou can only edit your own computers!");
    public final String COMPUTER_ONLY_OWNER_CAN_CHANGE_OWNER = get("computers.only-owner-can-change-owner", "&cOnly the owner of this computer can change the owner!");
    public final String COMPUTER_NOT_ALLOWED = get("computers.not-allowed", "&cYou aren't allowed to open this computer!");
    public final String COMPUTER_ITEM_NAME = get("computers.item-name", "&9Computer");
    public final String COMPUTER_NO_GROUP = get("computers.no-group","&cNo group assigned to this computer!");

    public final String GUI_CAMERA_SETTINGS = get("gui.camera.settings", "&eSettings");
    public final String GUI_CAMERA_DELETE = get("gui.camera.delete", "&eDelete camera %cameraID%");
    public String getGuiCameraDelete(String id) {
        return regex(GUI_CAMERA_DELETE,"%cameraID%",id,false);
    }
    public final String GUI_CAMERA_DELETE_ITEM_CANCEL = get("gui.camera.delete-item-cancel", "&cCancel");
    public final String GUI_CAMERA_DELETE_ITEM_DELETE = get("gui.camera.delete-item-delete", "&2Delete");
    public final String GUI_COMPUTER_DEFAULT = get("gui.computer.default", "&eCCTV (page: %page%)");
    public String getGuiComputerDefault(String page) {
        return regex(GUI_COMPUTER_DEFAULT,"%page%",page,true);
    }
    public final String GUI_COMPUTER_OPTIONS_ITEM = get("gui.computer.options-item", "&eOptions");
    public final String GUI_COMPUTER_SET_GROUP = get("gui.computer.set-group", "&eSet camera group (page: %page%)");
    public String getGuiComputerSetGroup(String page) {
        return regex(GUI_COMPUTER_SET_GROUP,"%page%",page,true);
    }
    public final String GUI_COMPUTER_REMOVE_PLAYER = get("gui.computer.remove-player", "&cRemove player (page: %page%)");
    public String getGuiComputerRemovePlayer(String page) {
        return regex(GUI_COMPUTER_REMOVE_PLAYER,"%page%",page,true);
    }
    public final String GUI_COMPUTER_DEFAULT_ITEM_OPTION = get("gui.computer.default-item.option", "&6Options");
    public final String GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE = get("gui.computer.default-item.next-page", "&8Next Page");
    public final String GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE = get("gui.computer.default-item.previous-page", "&8Previous Page");
    public final String GUI_COMPUTER_DEFAULT_ITEM_EXIT = get("gui.computer.default-item.exit", "&4Exit");
    public final String GUI_COMPUTER_DEFAULT_ITEM_BACK = get("gui.computer.default-item.back", "&8Back");
    public final String GUI_COMPUTER_OPTIONS_SET_CAMERA_GROUP = get("gui.computer.options.set-camera-group", "&aSet camera group");
    public final String GUI_COMPUTER_OPTIONS_ADD_PLAYER = get("gui.computer.options.add-player", "&aAdd player");
    public final String GUI_COMPUTER_OPTIONS_REMOVE_PLAYER = get("gui.computer.options.remove-player", "&cRemove player");

    public final String CAMERA_VIEW_OPTION = get("camera-view.option", "&eOptions");
    public final String CAMERA_VIEW_ROTATE_LEFT = get("camera-view.rotate-left", "&6Rotate Left");
    public final String CAMERA_VIEW_ROTATE_RIGHT = get("camera-view.rotate-right", "&6Rotate Right");
    public final String CAMERA_VIEW_PREVIOUS = get("camera-view.previous", "&bPrevious Camera");
    public final String CAMERA_VIEW_NEXT = get("camera-view.next", "&bNext Camera");
    public final String CAMERA_VIEW_EXIT = get("camera-view.exit", "&4Exit");
    public final String CAMERA_VIEW_ZOOM = get("camera-view.zoom", "&6Zoom: &a%level%x");
    public String getCameraViewZoom(int zoom) {
        return regex(CAMERA_VIEW_ZOOM,"%level%",zoom == -1 ? null : zoom+"",true);
    }
    public final String CAMERA_VIEW_OPTIONS_NIGHTVISION_OFF = get("camera-view.options.nightvision-off", "&6Night-Vision: &4Off");
    public final String CAMERA_VIEW_OPTIONS_NIGHTVISION_ON = get("camera-view.options.nightvision-on", "&6Night-Vision: &aOn");
    public final String CAMERA_VIEW_OPTIONS_ZOOM_OFF = get("camera-view.options.zoom-off", "&6Zoom: &4Off");
    public final String CAMERA_VIEW_OPTIONS_BACK = get("camera-view.options.back", "&8Back");
    public final String CAMERA_VIEW_OPTIONS_SPOT = get("camera-view.options.spot", "&6Spotting");

    public final String CHAT_PROVIDE_PLAYER = get("chat-provide-player", "&aProvide the player that you would like to add in the chat!");
    public final String CHAT_TYPE_EXIT = get("chat-type-exit", "&aType 'exit' to exit player adding!");


    public String regex(String str, String placeholder, String value, boolean num) {
        if (value == null) return quote(str).replace(placeholder,"(?<placeholder>"+(num ? "\\d+" : ".+")+")");
        return str.replace(placeholder,value);
    }
    public String quote(String str) {
        return str.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
    }

    public String getMatcher(String patt, String match, String string, String placeholder) {
        Pattern pattern = Pattern.compile(patt);
        Matcher m = pattern.matcher(match);
        if (!m.matches()) {
            CCTV.get().getLogger().info("The language message '"+string+"' doesn't contain "+placeholder+"!");
            return null;
        }
        return m.group("placeholder");
    }
}
