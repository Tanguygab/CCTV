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
    public final String COMMAND_BLOCKED = get("command-blocked","&cYou can't use this command right now!");
    public final String DOT_IN_NAME = get("dot-in-name","&cName can't contain any dot.");
    public final String UNSUPPORTED = get("unsupported","&4Unsupported!");

    public final String CHAT_PROVIDE_NAME = get("chat.provide-name", "&aProvide a name you would like your camera to have!");
    public final String CHAT_PROVIDE_PLAYER = get("chat.provide-player", "&aProvide a player to add!");
    public final String CHAT_TYPE_CANCEL = get("chat.type-cancel", "&aType 'cancel' to cancel!");
    public final String CHAT_CANCELLED = get("chat.cancelled", "&cCancelled!");

    private final String COMMANDS_TYPES_CAMERA = get("commands.types.camera","camera");
    private final String COMMANDS_TYPES_GROUP = get("commands.types.group","group");
    private final String COMMANDS_TYPES_COMPUTER = get("commands.types.computer","computer");

    public final String COMMANDS_PROVIDE_NAME = get("commands.provide-name","&cPlease specify a name!");
    private final String COMMANDS_PROVIDE_TYPE_NAME = get("commands.provide-type-name","&cPlease specify a %type% name!");
    public String getCommandsProvideCameraName() {
        return COMMANDS_PROVIDE_TYPE_NAME.replace("%type%",COMMANDS_TYPES_CAMERA);
    }
    public String getCommandsProvideGroupName() {
        return COMMANDS_PROVIDE_TYPE_NAME.replace("%type%",COMMANDS_TYPES_GROUP);
    }

    public final String COMMANDS_LIST_PREVIOUS = get("commands.list.previous", "&ePrevious Page");
    public final String COMMANDS_LIST_NEXT = get("commands.list.next", "&eNext Page");
    public final String COMMANDS_LIST_INFO = get("commands.list.info", "&eClick to view info");
    public final String COMMANDS_LIST_CAMERAS = get("commands.list.cameras", "Cameras");
    public final String COMMANDS_LIST_GROUPS = get("commands.list.groups", "Groups");
    public final String COMMANDS_LIST_COMPUTERS = get("commands.list.computers", "Computers");
    public final String COMMANDS_NEW_OWNER = get("commands.new-owner", "&cPlease specify a new owner!");
    public final String COMMANDS_RENAME = get("commands.rename","&cPlease specify a new name!");

    private final String COMMANDS_EDIT_CAMS_ADDED = get("commands.edit-cameras-&-groups.added","&aThis %camera-type% was added to this %type%!");
    private final String COMMANDS_EDIT_CAMS_ALREADY_ADDED = get("commands.edit-cameras-&-groups.already-added","&cThis %camera-type% has already been added to this %type%!");
    private final String COMMANDS_EDIT_CAMS_REMOVED = get("commands.edit-cameras-&-groups.removed","&aThis %camera-type% was removed from this %type%!");
    private final String COMMANDS_EDIT_CAMS_NOT_FOUND = get("commands.edit-cameras-&-groups.not-found","&cThis %type% does not contain a %camera-type% with that name!");
    public String getEditCameras(boolean add, boolean success, boolean camera, boolean computer) {
        String string = add ? success ? COMMANDS_EDIT_CAMS_ADDED : COMMANDS_EDIT_CAMS_ALREADY_ADDED
                : success ? COMMANDS_EDIT_CAMS_REMOVED : COMMANDS_EDIT_CAMS_NOT_FOUND;
        return string.replace("%camera-type%",camera ? COMMANDS_TYPES_CAMERA : COMMANDS_TYPES_GROUP)
                .replace("%type%",computer ? COMMANDS_TYPES_COMPUTER : COMMANDS_TYPES_GROUP);
    }

    private final String CAMERA_CREATED = get("cameras.created","&aCamera %camera% created!");
    public String getCameraCreated(String name) {
        return CAMERA_CREATED.replace("%camera%",name);
    }
    private final String CAMERA_DELETED = get("cameras.deleted","&cCamera %camera% deleted!");
    public String getCameraDeleted(String name) {
        return CAMERA_DELETED.replace("%camera%",name);
    }
    public final String CAMERA_ALREADY_EXISTS = get("cameras.already-exists","&cThis camera already exists!");
    public final String CAMERA_NOT_FOUND = get("cameras.not-found","&cThis camera doesn't exist.");
    private final String CAMERA_OWNER_CHANGED = get("cameras.owner-changed","&6Camera owner is set to &a%player%&6!");
    public String getCameraOwnerChanged(String player) {
        return CAMERA_OWNER_CHANGED.replace("%player%",player);
    }
    public final String CAMERA_PLAYER_ALREADY_OWNER = get("cameras.player-already-owner","&cThis player is already the owner of this camera!");
    private final String CAMERA_RENAMED = get("cameras.renamed","&aCamera renamed to '%camera%'!");
    public String getCameraRenamed(String name) {
        return CAMERA_RENAMED.replace("%camera%",name);
    }
    public final String CAMERA_CONNECTING = get("cameras.connecting","&aConnecting...");
    public final String CAMERA_DISCONNECTING = get("cameras.disconnecting","&cDisconnecting...");
    public final String CAMERA_OFFLINE = get("cameras.offline","&cThis camera is offline!");
    public final String CAMERA_OFFLINE_OVERRIDE = get("cameras.offline-override","&cNOTE: This camera is offline! You can view this camera because you can bypass it!");
    public final String CAMERA_MOVED = get("cameras.moved","&aCamera moved to your location!");
    private final String CAMERA_VIEW_COUNT = get("cameras.view-count","&aThere are currently &2%count%&a players watching camera &2%camera%&a!");
    public String getCameraViewCount(int count, String name) {
        return CAMERA_VIEW_COUNT.replace("%count%", String.valueOf(count)).replace("%camera%",name);
    }
    public final String CAMERA_DELETED_BECAUSE_BUGGED = get("cameras.deleted-because-bugged","&cSorry but this camera was bugged, so we removed it!");
    public final String CAMERA_TOO_FAR = get("cameras.too-far","&cThis camera is too far away from you!");
    public final String CAMERA_ITEM_NAME = get("cameras.item-name", "&9Camera");
    public final String CAMERA_ITEM_PLACE = get("cameras.item-place","&2Place down this item to create a camera!");

    private final String GROUP_CREATED = get("groups.created","&aGroup %group% created!");
    public String getGroupCreated(String name) {
        return GROUP_CREATED.replace("%group%",name);
    }
    private final String GROUP_DELETED = get("groups.deleted","&aGroup %group% deleted!");
    public String getGroupDeleted(String name) {
        return GROUP_DELETED.replace("%group%",name);
    }
    public final String GROUP_ALREADY_EXISTS = get("groups.already-exists","&cThis group already exists!");
    public final String GROUP_NOT_FOUND = get("groups.not-found","&cThis group doesn't exist!");
    private final String GROUP_OWNER_CHANGED = get("groups.owner-changed","&6Group owner is set to &a%player%&6!");
    public String getGroupOwnerChanged(String player) {
        return GROUP_OWNER_CHANGED.replace("%player%",player);
    }
    public final String GROUP_PLAYER_ALREADY_OWNER = get("groups.player-already-owner","&cThis player is already the owner of this group!");
    private final String GROUP_RENAMED = get("groups.renamed","&aGroup renamed to '%group%'!");
    public String getGroupRenamed(String name) {
        return GROUP_RENAMED.replace("%group%",name);
    }

    public final String GROUP_ICON_PROVIDE = get("groups.provide","&cPlease specify an icon!");
    private final String GROUP_ICON_INVALID = get("groups.invalid","&cThis icon is invalid! Valid Icons: %icons%");
    public String getGroupIconInvalid(String icons) {
        return GROUP_ICON_INVALID.replace("%icons%",icons);
    }
    public final String GROUP_ICON_CHANGED = get("groups.changed","&aGroup Icon changed!");

    public final String COMPUTER_CREATED = get("computers.created", "&aComputer %computer% created!");
    public String getComputerCreated(String name) {
        return COMPUTER_CREATED.replace("%computer%",name);
    }
    public final String COMPUTER_DELETED = get("computers.deleted", "&cComputer %computer% deleted!");
    public String getComputerDeleted(String name) {
        return COMPUTER_DELETED.replace("%computer%",name);
    }
    public final String COMPUTER_NOT_FOUND = get("computers.not-found", "&cThis computer doesn't exist!");
    private final String COMPUTER_OWNER_CHANGED = get("computers.owner-changed", "&6Computer owner is set to &a%player%&6!");
    public String getComputerOwnerChanged(String player) {
        return COMPUTER_OWNER_CHANGED.replace("%player%",player);
    }
    public final String COMPUTER_PLAYER_ALREADY_OWNER = get("computers.player-already-owner","&cThis player is already the owner of this computer!");
    public final String COMPUTER_CHANGE_NO_PERMS = get("computers.change-no-perms", "&cYou can only edit your own computers!");
    public final String COMPUTER_NOT_ALLOWED = get("computers.not-allowed", "&cYou aren't allowed to interact with this computer!");
    public final String COMPUTER_ITEM_NAME = get("computers.item-name", "&9Computer");
    public final String COMPUTER_ITEM_NAME_ADMIN = get("computers.item-name-admin", "&dAdmin Computer");
    public final String COMPUTER_ITEM_PLACE = get("computers.item-place","&2Place down this item to create a computer!");

    private final String GUI_CAMERA = get("gui.camera.title", "&eCamera %camera%");
    public String getGuiCamera(String name) {
        return GUI_CAMERA.replace("%camera%",name);
    }
    public final String GUI_CAMERA_CHANGE_SKIN = get("gui.camera.change-skin", "&aChange Camera Skin");
    public final String GUI_CAMERA_DELETE = get("gui.camera.delete", "&4Delete");
    public final String GUI_CAMERA_EXIT = get("gui.camera.exit", "&cExit");

    public final String GUI_CAMERA_RENAME = get("gui.camera.rename", "&aRename Camera");
    public final String GUI_CAMERA_VIEW = get("gui.camera.view", "&aView");
    public final String GUI_CAMERA_SHOWN = get("gui.camera.shown", "&aCamera Shown");
    public final String GUI_CAMERA_HIDDEN = get("gui.camera.hidden", "&cCamera Hidden");
    public final String GUI_CAMERA_ENABLED = get("gui.camera.enabled", "&aCamera Enabled");
    public final String GUI_CAMERA_DISABLED = get("gui.camera.disabled", "&cCamera Disabled");
    private final String GUI_CAMERA_SKIN_TITLE = get("gui.camera.skin.title", "&eCamera Skins (page: %page%)");
    public String getGuiCameraSkin(int page) {
        return GUI_CAMERA_SKIN_TITLE.replace("%page%",String.valueOf(page));
    }
    public final String GUI_CAMERA_SKIN_CURRENT = get("gui.camera.skin.current", "&aCurrent Skin: ");
    public final String GUI_CAMERA_SKIN_DEFAULT = get("gui.camera.skin.default", "Default");

    private final String GUI_COMPUTER_DEFAULT = get("gui.computer.default", "&eCCTV (page: %page%)");
    public String getGuiComputerDefault(int page) {
        return GUI_COMPUTER_DEFAULT.replace("%page%",String.valueOf(page));
    }
    public final String GUI_COMPUTER_OPTIONS_TITLE = get("gui.computer.options-title", "&eOptions");
    public final String GUI_COMPUTER_TOGGLE_COORDS = get("gui.computer.toggle-coords","Toggle Camera Coordinates");
    public final String GUI_COMPUTER_CAMERA_ITEM_X = get("gui.computer.camera-item.x"," &eX: &7");
    public final String GUI_COMPUTER_CAMERA_ITEM_Y = get("gui.computer.camera-item.y", " &eY: &7");
    public final String GUI_COMPUTER_CAMERA_ITEM_Z = get("gui.computer.camera-item.z"," &eZ: &7");
    public final String GUI_COMPUTER_CAMERA_ITEM_VIEW = get("gui.computer.camera-item.view","&eLeft-Click to View");
    public final String GUI_COMPUTER_CAMERA_ITEM_EDIT = get("gui.computer.camera-item.edit","&eRight-Click to Edit");
    public final String GUI_COMPUTER_CAMERA_ITEM_GO_UP = get("gui.computer.camera-item.go-up","&eShift-Left to go up");
    public final String GUI_COMPUTER_CAMERA_ITEM_GO_DOWN = get("gui.computer.camera-item.go-down","&eShift-Right to go down");
    public final String GUI_COMPUTER_CAMERA_ITEM_REMOVE = get("gui.computer.camera-item.remove","&eDrop to remove");

    private final String GUI_COMPUTER_REMOVE_PLAYER = get("gui.computer.remove-player", "&cRemove player (page: %page%)");
    public String getGuiComputerRemovePlayer(int page) {
        return GUI_COMPUTER_REMOVE_PLAYER.replace("%page%",String.valueOf(page));
    }
    private final String GUI_COMPUTER_ADD_PLAYER = get("gui.computer.add-player", "&aAdd player (page: %page%)");
    public String getGuiComputerAddPlayer(int page) {
        return GUI_COMPUTER_ADD_PLAYER.replace("%page%",String.valueOf(page));
    }
    private final String GUI_COMPUTER_ADD_CAMERA = get("gui.computer.add-camera", "&aAdd Camera (page: %page%)");
    public String getGuiComputerAddCamera(int page) {
        return GUI_COMPUTER_ADD_CAMERA.replace("%page%",String.valueOf(page));
    }
    private final String GUI_COMPUTER_ADD_GROUP = get("gui.computer.add-group", "&aAdd Group (page: %page%)");
    public String getGuiComputerAddGroup(int page) {
        return GUI_COMPUTER_ADD_GROUP.replace("%page%",String.valueOf(page));
    }
    public final String GUI_COMPUTER_DEFAULT_ITEM_OPTION = get("gui.computer.default-item.option", "&6Options");
    public final String GUI_COMPUTER_DEFAULT_ITEM_NEXT_PAGE = get("gui.computer.default-item.next-page", "&8Next Page");
    public final String GUI_COMPUTER_DEFAULT_ITEM_PREVIOUS_PAGE = get("gui.computer.default-item.previous-page", "&8Previous Page");
    public final String GUI_COMPUTER_DEFAULT_ITEM_EXIT = get("gui.computer.default-item.exit", "&4Exit");
    public final String GUI_COMPUTER_DEFAULT_ITEM_BACK = get("gui.computer.default-item.back", "&8Back");

    public final String GUI_COMPUTER_OPTIONS_ADD_PLAYER = get("gui.computer.options.add-player", "&aAdd player");
    public final String GUI_COMPUTER_OPTIONS_ADD_CAMERAS = get("gui.computer.options.add-cameras","&aAdd Cameras");
    public final String GUI_COMPUTER_OPTIONS_ADD_GROUPS = get("gui.computer.options.add-groups","&aAdd Groups");
    public final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_NAME = get("gui.computer.options.access-item.name", "&aComputer Access");
    private final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_STATUS = get("gui.computer.options.access-item.status", "&6Status: %status%");
    public final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_PUBLIC = get("gui.computer.options.access-item.public", "&aPublic");
    public final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_PRIVATE = get("gui.computer.options.access-item.private", "&cPrivate");
    public String getGuiComputerOptionsAccessItemStatus(boolean publik) {
        return GUI_COMPUTER_OPTIONS_ACCESS_ITEM_STATUS.replace("%status%",publik ? GUI_COMPUTER_OPTIONS_ACCESS_ITEM_PUBLIC : GUI_COMPUTER_OPTIONS_ACCESS_ITEM_PRIVATE);
    }
    public final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_TOGGLE_ACCESS = get("gui.computer.options.access-item.toggle-access", "&eShift-Left to toggle");
    public final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_ADD_PLAYERS = get("gui.computer.options.access-item.add-players", "&eLeft-Click to add players");
    public final String GUI_COMPUTER_OPTIONS_ACCESS_ITEM_REMOVE_PLAYERS = get("gui.computer.options.access-item.remove-players", "&eRight-Click to remove players");


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
        return CAMERA_VIEW_ZOOM.replace("%level%", String.valueOf(zoom));
    }
    public final String CAMERA_VIEW_OPTIONS_TITLE = get("camera-view.options.title", "&eSettings");
    public final String CAMERA_VIEW_OPTIONS_NIGHTVISION_OFF = get("camera-view.options.nightvision-off", "&6Night-Vision: &4Off");
    public final String CAMERA_VIEW_OPTIONS_NIGHTVISION_ON = get("camera-view.options.nightvision-on", "&6Night-Vision: &aOn");
    public final String CAMERA_VIEW_OPTIONS_ZOOM_OFF = get("camera-view.options.zoom-off", "&6Zoom: &4Off");
    public final String CAMERA_VIEW_OPTIONS_ZOOM_UNAVAILABLE = get("camera-view.options.zoom-unavailable", "&6Change your FOV!");
    public final String CAMERA_VIEW_OPTIONS_BACK = get("camera-view.options.back", "&8Back");
    public final String CAMERA_VIEW_OPTIONS_SPOT = get("camera-view.options.spot", "&6Spotting");

}
