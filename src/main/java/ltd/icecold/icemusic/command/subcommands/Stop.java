package ltd.icecold.icemusic.command.subcommands;

import ltd.icecold.icemusic.command.BaseCommand;
import ltd.icecold.icemusic.config.Config;
import ltd.icecold.icemusic.config.Language;
import ltd.icecold.icemusic.music.lyric.Lyric;
import ltd.icecold.icemusic.utils.PluginMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Stop extends BaseCommand {

    public Stop() {
        super("stop");
    }

    @Override
    public void onCommand(CommandSender sender, String command, ArrayList<String> args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("[IceMusic] > "+ ChatColor.DARK_RED +"请在游戏内使用本指令");
            return;
        }
        Player player = (Player)sender;
        if ((Boolean) Config.get("play.stop.soundStop")){
            PluginMessage.send(player,"{type: \"stop_all\"}");
        }else {
            PluginMessage.send(player,"{type: \"stop\"}");
        }
        Lyric.stopLyc(player);
        player.sendMessage(Language.getLang("language.lang4"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Object subCmd, ArrayList<String> args) {
        return null;
    }
}
