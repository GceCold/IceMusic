package ltd.icecold.icemusic.config;

import ltd.icecold.icemusic.IceMusic;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language {
    public static YamlConfiguration yamlConfiguration;
    public static void init(){
        File languageFile = new File(IceMusic.getInstance().getDataFolder(),"language.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(languageFile);
    }
    public static String getLang(String path){
        return yamlConfiguration.getString(path);
    }
}
