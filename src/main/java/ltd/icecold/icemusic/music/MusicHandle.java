package ltd.icecold.icemusic.music;

import com.google.gson.Gson;
import ltd.icecold.icemature.NeteaseMusic;
import ltd.icecold.icemusic.IceMusic;
import ltd.icecold.icemusic.bean.PlayMusicMessageBean;
import ltd.icecold.icemusic.command.subcommands.Accept;
import ltd.icecold.icemusic.config.Config;
import ltd.icecold.icemusic.config.Language;
import ltd.icecold.icemusic.config.Login;
import ltd.icecold.icemusic.utils.PluginMessage;
import ltd.icecold.icemusic.utils.Utils;
import ltd.icecold.icemusic.utils.VaultHandle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ice_cold
 * @date Create in 3:43 2020/8/6
 */
public class MusicHandle {
      public static Map<String, Long> sendCoolDown = new HashMap<>();
      public static MusicMessage getMusicInfo(String musicName, CommandSender player){
            NeteaseMusic neteaseMusic = new NeteaseMusic();
            String search = neteaseMusic.search(musicName, 1);


            return new MusicMessage(musicMessage,player);
      }
      public static void playMusic2Player(String music,Player player){
            MusicMessage musicInfo = getMusicInfo(music, player);
            PluginMessage.send(player,new Gson().toJson(musicInfo2Bean(musicInfo)));
      }

      public static void playServerMusic2Player(String music, CommandSender from){
            MusicMessage musicInfo = getMusicInfo(music, from);
            String json = new Gson().toJson(musicInfo2Bean(musicInfo));
            for (Player player:Utils.getOnlinePlayers()){
                  PluginMessage.send(player,json);
                  player.sendMessage(Language.getLang("language.lang13").replace("${SERVER_PLAYER}",from.getName()).replace("${MUSIC_NAME}",musicInfo.getMusicName()));
            }
      }

      public static PlayMusicMessageBean musicInfo2Bean(MusicMessage musicInfo) {
            PlayMusicMessageBean playMusicMessageBean = new PlayMusicMessageBean();
            PlayMusicMessageBean.MessageBean messageBean = new PlayMusicMessageBean.MessageBean();
            messageBean.setMusicName(musicInfo.getMusicName());
            messageBean.setMusicArtist(musicInfo.getMusicArtists());
            messageBean.setMusicCover(musicInfo.getMusicPic());
            messageBean.setMusicUrl(musicInfo.getMusicUrl());
            playMusicMessageBean.setMessage(messageBean);
            playMusicMessageBean.setType("play_music_now");
            return playMusicMessageBean;
      }

      public static void serverMusic(Player player,MusicMessage music){
            if (player.hasPermission("music.server")){
                  for (Player target:Utils.getOnlinePlayers()){
                        sendRequest(player,target,music);
                  }
                  player.sendMessage("[IceMusic] > 已发送请求");
            }else {
                  player.sendMessage("[IceMusic] >§c 您没有权限");
                  return;
            }
      }

      public static void sendRequest(Player player,Player target,MusicMessage music){
            Accept.playerResponse.put(target.getName(),music);
            if (!player.hasPermission("music.send")){
                 player.sendMessage("[IceMusic] >§c 您没有权限");
                 return;
            }
            if (!player.isOp()) {
                  long lastIssuedTime;
                  if (sendCoolDown.containsKey(player.getName())) {
                        long interval = (Integer) Config.get("send.cooldown");
                        lastIssuedTime = sendCoolDown.get(player.getName());
                        long ts = System.currentTimeMillis();
                        if ((ts - lastIssuedTime) / 1000 < interval) {
                              String mess = Language.getLang("language.lang8").replace("${CD_TIME}", String.valueOf((interval * 1000 + lastIssuedTime - ts) / 1000));
                              player.sendMessage(mess);
                              return;
                        } else {
                              sendCoolDown.put(player.getName(), ts);
                        }
                  } else {
                        long ts = System.currentTimeMillis();
                        sendCoolDown.put(player.getName(), ts);
                  }
            }
            Integer cost = (Integer) Config.get("send.paid.cost");
            if ((Boolean) Config.get("send.paid.enable")){
                  if (!VaultHandle.hasMoney(player.getName(), cost)) {
                        player.sendMessage(Language.getLang("language.lang14").replace("${MUSIC_NAME}", music.getMusicName()).replace("${MONEY}", String.valueOf(cost)).replace("${NOW}", VaultHandle.getMoney(player.getName()).toString()));
                        return;
                  }
                  VaultHandle.delMoney(player.getName(), cost);
            }
            Accept.playerResponse.put(target.getName(),music);
            player.sendMessage(Language.getLang("language.lang15").replace("${MUSIC_NAME}", music.getMusicName()).replace("${MONEY}", String.valueOf(cost)).replace("${NOW}", VaultHandle.getMoney(player.getName()).toString()));

            target.sendMessage(Language.getLang("language.lang15").replace("${MUSIC_NAME}", music.getMusicName()).replace("${PLAYER}",player.getName()));

      }
}
