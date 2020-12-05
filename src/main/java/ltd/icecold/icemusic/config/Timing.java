package ltd.icecold.icemusic.config;

import ltd.icecold.icemusic.IceMusic;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Timing {
    public static YamlConfiguration yamlConfiguration;
    public static void init(){
        File timingFile = new File(IceMusic.getInstance().getDataFolder(),"timing.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(timingFile);
    }
    public static Object get(String path){
        return yamlConfiguration.get(path);
    }
    public static List<String> timingName(){
        Set<String> key = yamlConfiguration.getKeys(true);
        key.remove("timing");
        key.remove("timing.enable");
        List<String> command = new ArrayList<>();
        for (String str : key) {
            str = str.replace("timing.","");
            if (!str.contains(".")){
                command.add(str);
            }
        }
        return command;
    }
    public static String getTime(String name){ return yamlConfiguration.getString("timing."+name+".time"); }
    public static String getMusicName(String name){ return yamlConfiguration.getString("timing."+name+".musicName"); }

}
