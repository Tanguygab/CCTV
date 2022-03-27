package io.github.tanguygab.cctv.config;

import org.bukkit.ChatColor;

import java.io.File;
import java.io.InputStream;

public class LanguageFile extends YamlConfigurationFile {

    public LanguageFile(InputStream source, File destination) throws Exception {
        super(source, destination);
    }

    private String get(String str, String def) {
        return ChatColor.translateAlternateColorCodes('&',getString(str,def));
    }

    public final String NO_PERMISSIONS = get("no-permissions","&cYou don't have the right permission to do that!");
    public final String PLAYER_NOT_FOUND = get("player-not-found","&cThis player doesn't exist.");
    public final String PLAYER_ADDED = get("player-added","&aPlayer has been added!");
    public final String PLAYER_REMOVED = get("player-removed","&cPlayer has been removed!");

    public final String MAX_ROTATION = get("max-rotation","&cThis is the limit of rotation!");
    public final String NO_CAMERAS = get("no-cameras","&cThere aren't any cameras!");
    public final String SWITCHING_NOT_POSSIBLE = get("switching-not-possible","&cSwitching through cameras is not possible!");

    public final String CAMERA_CREATE = get("cameras.create","&aCamera created!");
    public final String CAMERA_DELETE = get("cameras.delete","&cCamera deleted!");
    public final String CAMERA_ALREADY_EXISTS = get("cameras.already-exists","&cThis camera already exists!");
    public final String CAMERA_NOT_FOUND = get("cameras.not-found","&cThis camera doesn't exist.");
    private final String CAMERA_ID = get("cameras.id","&eCamera ID: %cameraID%");
    public String getCameraID(String id) {
        return CAMERA_ID.replace("%cameraID%",id);
    }
    private final String CAMERA_OWNER_CHANGED = get("cameras.owner-changed","&6Camera owner is set to &a%player%&6!");
    public String getCameraOwnerChanged(String player) {
        return CAMERA_OWNER_CHANGED.replace("%player%",player);
    }
    public final String CAMERA_PLAYER_ALREADY_OWNER = get("cameras.player-already-owner","&cThis player is already the owner of this camera!");
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
    public final String CAMERA_ALREADY_ENABLED = get("cameras.already-enabled","&cThis camera is already enabled!");
    public final String CAMERA_ALREADY_DISABLED = get("cameras.already-disabled","&cThis camera is already disabled!");
    public final String CAMERA_OFFLINE = get("cameras.offline","&cThis camera is offline!");
    public final String CAMERA_OFFLINE_OVERRIDE = get("cameras.offline-override","&cNOTE: This camera is offline! You can view this camera because you can bypass it!");
    private final String CAMERA_SHOWN = get("cameras.shown","&aCamera &2%cameraID%&a is now &2shown&a!");
    public String getCameraShown(String id) {
        return CAMERA_SHOWN.replace("%cameraID%",id);
    }
    private final String CAMERA_HIDDEN = get("cameras.hidden","&aCamera &2%cameraID%&a is now &chidden&a!");
    public String getCameraHidden(String id) {
        return CAMERA_HIDDEN.replace("%cameraID%",id);
    }
    public final String CAMERA_ALREADY_SHOWN = get("cameras.already-shown","&cThis camera is already shown!");
    public final String CAMERA_ALREADY_HIDDEN = get("cameras.already-hidden","&cThis camera is already hidden!");
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
    public final String GROUP_PLAYER_ALREADY_OWNER = get("groups.player-already-owner","&cThis player is already the owner of this group!");
    private final String GROUP_RENAMED = get("groups.renamed","&aGroup renamed to '%groupID%'!");
    public String getGroupRenamed(String id) {
        return GROUP_RENAMED.replace("%groupID%",id);
    }
    public final String GROUP_CAMERA_ALREADY_ADDED = get("groups.camera-already-added","&cThis camera has already been added to this group!");
    public final String GROUP_CAMERA_ADDED = get("groups.camera-added","&aCamera added to the group!");
    public final String GROUP_REMOVE_CAMERA = get("groups.remove-camera","&aCamera removed from this group!");
    public final String GROUP_DOES_NOT_CONTAIN_CAMERA = get("groups.does-not-contain-camera","&cThis group does not contain a camera with that ID!");
    public final String GROUP_ASSIGNED_TO_COMPUTER = get("groups.assigned-to-computer","&aGroup assigned to this PC!");

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
    public final String COMPUTER_PLAYER_ALREADY_OWNER = get("computers.player-already-owner","&cThis player is already the owner of this computer!");
    public final String COMPUTER_CHANGE_NO_PERMS = get("computers.change-no-perms", "&cYou can only edit your own computers!");
    public final String COMPUTER_NOT_ALLOWED = get("computers.not-allowed", "&cYou aren't allowed to open this computer!");
    public final String COMPUTER_ITEM_NAME = get("computers.item-name", "&9Computer");

    private final String GUI_CAMERA = get("gui.camera.title", "&eCamera %cameraID%");
    public String getGuiCamera(String id) {
        return GUI_CAMERA.replace("%cameraID%",id);
    }
    public final String GUI_CAMERA_CHANGE_SKIN = get("gui.camera.change-skin", "&aChange Camera Skin");
    public final String GUI_CAMERA_DELETE = get("gui.camera.delete", "&4Delete");
    public final String GUI_CAMERA_EXIT = get("gui.camera.exit", "&cExit");

    private final String GUI_COMPUTER_DEFAULT = get("gui.computer.default", "&eCCTV (page: %page%)");
    public String getGuiComputerDefault(String page) {
        return GUI_COMPUTER_DEFAULT.replace("%page%",page);
    }
    public final String GUI_COMPUTER_OPTIONS_ITEM = get("gui.computer.options-item", "&eOptions");
    public final String GUI_COMPUTER_SET_GROUP = get("gui.computer.set-group", "&eSet camera group (page: %page%)");
    public String getGuiComputerSetGroup(String page) {
        return GUI_COMPUTER_SET_GROUP.replace("%page%",page);
    }
    private final String GUI_COMPUTER_REMOVE_PLAYER = get("gui.computer.remove-player", "&cRemove player (page: %page%)");
    public String getGuiComputerRemovePlayer(String page) {
        return GUI_COMPUTER_REMOVE_PLAYER.replace("%page%",page);
    }
    private final String GUI_COMPUTER_ADD_PLAYER = get("gui.computer.add-player", "&aAdd player (page: %page%)");
    public String getGuiComputerAddPlayer(String page) {
        return GUI_COMPUTER_ADD_PLAYER.replace("%page%",page);
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
    public final String CAMERA_VIEW_ROTATE_UP = get("camera-view.rotate-up", "&6Rotate Up");
    public final String CAMERA_VIEW_ROTATE_LEFT = get("camera-view.rotate-left", "&6Rotate Left");
    public final String CAMERA_VIEW_ROTATE_RIGHT = get("camera-view.rotate-right", "&6Rotate Right");
    public final String CAMERA_VIEW_ROTATE_DOWN = get("camera-view.rotate-down", "&6Rotate Down");
    public final String CAMERA_VIEW_PREVIOUS = get("camera-view.previous", "&bPrevious Camera");
    public final String CAMERA_VIEW_NEXT = get("camera-view.next", "&bNext Camera");
    public final String CAMERA_VIEW_EXIT = get("camera-view.exit", "&4Exit");
    private final String CAMERA_VIEW_ZOOM = get("camera-view.zoom", "&6Zoom: &a%level%x");
    public String getCameraViewZoom(int zoom) {
        return CAMERA_VIEW_ZOOM.replace("%level%",zoom+"");
    }
    public final String CAMERA_VIEW_OPTIONS_TITLE = get("camera-view.options.title", "&eSettings");
    public final String CAMERA_VIEW_OPTIONS_NIGHTVISION_OFF = get("camera-view.options.nightvision-off", "&6Night-Vision: &4Off");
    public final String CAMERA_VIEW_OPTIONS_NIGHTVISION_ON = get("camera-view.options.nightvision-on", "&6Night-Vision: &aOn");
    public final String CAMERA_VIEW_OPTIONS_ZOOM_OFF = get("camera-view.options.zoom-off", "&6Zoom: &4Off");
    public final String CAMERA_VIEW_OPTIONS_BACK = get("camera-view.options.back", "&8Back");
    public final String CAMERA_VIEW_OPTIONS_SPOT = get("camera-view.options.spot", "&6Spotting");

    public final String CHAT_PROVIDE_NAME = get("chat-provide-name", "&aProvide the new name you would like your camera to have!");
    public final String CHAT_TYPE_CANCEL = get("chat-type-cancel", "&aType 'cancel' to cancel!");

}
