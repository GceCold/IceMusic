package ltd.icecold.icemusic.config;

import ltd.icecold.icemusic.IceMusic;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


/**
 * @author ice_cold
 */
public class Login {
    private static String username;
    private static String code;
    private static String wyPhone;
    private static String wyUserName;
    private static String wyPassword;

    public static void init(){
        File loginFile = new File(IceMusic.getInstance().getDataFolder() , "login.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(loginFile);
        username = yamlConfiguration.getString("IceMusic.username");
        code = yamlConfiguration.getString("IceMusic.code");
        wyPhone = yamlConfiguration.getString("netease.phone");
        wyUserName = yamlConfiguration.getString("netease.email");
        wyPassword = yamlConfiguration.getString("netease.md5Password");
    }

    public static String getUsername() {
        return username;
    }

    public static String getCode() {
        return code;
    }

    public static String getWyUserName() {
        return wyUserName;
    }

    public static String getWyPassword() {
        return wyPassword;
    }

    public static String getWyPhone() {
        return wyPhone;
    }
}
