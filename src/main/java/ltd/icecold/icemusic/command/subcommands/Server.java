package ltd.icecold.icemusic.command.subcommands;

import ltd.icecold.icemusic.command.BaseCommand;
import ltd.icecold.icemusic.gui.SearchMusicGui;
import ltd.icecold.icemusic.music.MusicHandle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Server extends BaseCommand {

    public Server() {
        super("server");
    }

    @Override
    public void onCommand(CommandSender sender, String command, ArrayList<String> args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name:args){
            stringBuilder.append(name).append(" ");
        }
        if (sender.isOp()){
            MusicHandle.playServerMusic2Player(stringBuilder.toString(),sender);
        }else if (sender.hasPermission("music.server")){
            SearchMusicGui.OpenPlayerGUI((Player) sender);
        }else {
            sender.sendMessage("[IceMusic] >§c 您没有权限");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Object subCmd, ArrayList<String> args) {
        return null;
    }
}
