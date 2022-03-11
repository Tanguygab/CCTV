package io.github.tanguygab.cctv.config;

import org.bukkit.ChatColor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LanguageFile extends YamlConfigurationFile {

    public LanguageFile(InputStream source, File destination) throws IllegalStateException, YAMLException, IOException {
        super(source, destination);
    }

    private String get(String str, String def) {
        return ChatColor.translateAlternateColorCodes('&',getString(str,def));
    }

    public final String NO_PERMISSIONS = get("no_permissions","&cYou don't have the right permission to do that!");
    public final String PLAYER_NOT_FOUND = get("player_not_found","&cThis player doesn't exist.");
    public final String PLAYER_ALREADY_ADDED = get("player_already_added","&cPlayer has already been added!");
    public final String PLAYER_ADDED = get("player_added","&aPlayer has been added!");
    public final String PLAYER_REMOVED = get("player_removed","&cPlayer has been removed!");
    public final String PLAYER_NOT_IN_LIST = get("player_not_in_list","&cThis player isn't in this list!");
    public final String TOO_MANY_PAGES = get("to_many_pages","&cThere aren't that many pages!");
    public final String CLICK_PREVIOUS_PAGE = get("click_previous_page","&aPrevious Page");
    public final String CLICK_NEXT_PAGE = get("click_next_page","&aNext Page");
    public final String INCORRECT_SYNTAX = get("incorrect_syntax","&cIncorrect syntax!");
    public final String ONLY_OWNER = get("only_owner","&cYou can only edit your own cameras!");
    private final String LIST = get("list","&6- &7%ID%");
    public String getList(String id) {
        return CAMERA_ENABLED.replace("%ID%",id);
    }
    private final String LIST_ADMIN = get("list_admin","&6- (%player%) &7%ID%");
    public String getListAdmin(String player, String id) {
        return CAMERA_ENABLED.replace("%player%",player).replace("%ID%",id);
    }
    public final String MAX_ROTATION = get("max_rotation","&cThis is the limit of rotation!");
    public final String NO_CAMERAS = get("no_cameras","&cThere aren't any cameras!");
    public final String SWITCHING_NOT_POSSIBLE = get("switching_not_possible","&cSwitching through cameras is not possible!");

    public final String CAMERA_CREATE = get("camera.create","&aCamera created!");
    public final String CAMERA_DELETE = get("camera.delete","&cCamera deleted!");
    public final String CAMERA_ALREADY_EXISTS = get("camera.already-exists","&cThis camera already exists!");
    public final String CAMERA_NOT_FOUND = get("camera.not-found","&cThis camera doesn't exist.");
    public final String CAMERA_CONNECTING = get("camera.connecting","&aConnecting...");
    public final String CAMERA_DISCONNECTING = get("camera.disconnecting","&cDisconnecting...");
    private final String CAMERA_ENABLED = get("camera.enabled","&aCamera &2%cameraID%&a is now &2enabled&a!");
    public String getCameraEnabled(String id) {
        return CAMERA_ENABLED.replace("%cameraID%",id);
    }
    private final String CAMERA_DISABLED = get("camera.disabled","&aCamera &2%cameraID%&a is now &cdisabled&a!");
    public String getCameraDisabled(String id) {
        return CAMERA_DISABLED.replace("%cameraID%",id);
    }
    public final String CAMERA_OFFLINE = get("camera.offline","&cThis camera is offline!");
    public final String CAMERA_OFFLINE_OVERRIDE = get("camera.offline-override","&cNOTE: This camera is offline! You can view this camera because you can bypass it!");

    public final String CAMERA_ALREADY_ENABLED = get("camera.already-enabled","&cThis camera is already enabled!");
    public final String CAMERA_ALREADY_DISABLED = get("camera.already-disabled","&cThis camera is already disabled!");
    public final String CAMERA_MOVED = get("camera.moved","&aCamera moved to your location!");
    public final String CAMERA_CHANGE_NO_PERMS = get("camera.change-no-perms","&cYou can only change your own camera!");
    public final String CAMERA_PLAYER_ALREADY_OWNER = get("camera.player-already-owner","&cThat player is already the owner of that camera!");
    private final String CAMERA_OWNER_CHANGED = get("camera.owner-changed","&6Camera owner is set to &a%player%&6!");
    public String getCameraOwnerChanged(String player) {
        return CAMERA_OWNER_CHANGED.replace("%player%",player);
    }
    private final String CAMERA_VIEW_COUNT = get("camera.view-count","&aThere are currently &2%count%&a players watching camera &2%cameraID%&a!");
    public String getCameraViewCount(int count, String id) {
        return CAMERA_VIEW_COUNT.replace("%count%",count+"").replace("%cameraID%",id);
    }
    public final String CAMERA_ID = get("camera.id","&eCamera ID: %cameraID%");
    public String getCameraID(String id) {
        return CAMERA_ID.replace("%cameraID%",id);
    }
    private final String CAMERA_RENAMED = get("camera.renamed","&aCamera renamed to '%cameraID%'!");
    public String getCameraRenamed(String id) {
        return CAMERA_RENAMED.replace("%cameraID%",id);
    }
    public final String CAMERA_DELETED_BECAUSE_BUGGED = get("camera.deleted-because-bugged","&cSorry but this camera was bugged, so we removed it!");

}
