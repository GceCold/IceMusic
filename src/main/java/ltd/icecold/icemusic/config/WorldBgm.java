package ltd.icecold.icemusic.config;

import ltd.icecold.icemusic.IceMusic;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldBgm {
    public static YamlConfiguration yamlConfiguration;
    public static void init(){
        File worldFile = new File(IceMusic.getInstance().getDataFolder(),"worldBGM.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(worldFile);
    }
    public static Object get(String path){
        return yamlConfiguration.get(path);
    }

    public static List<String> getWorldList(){
        Set<String> key = yamlConfiguration.getKeys(true);
        key.remove("world");
        key.remove("world.enable");
        key.remove("world.overlay");
        List<String> command = new ArrayList<>();
        for (String str : key) {
            str = str.replace("world.","");
            if (!str.contains(".")){
                command.add(str);
            }
        }
        return command;
    }
}
