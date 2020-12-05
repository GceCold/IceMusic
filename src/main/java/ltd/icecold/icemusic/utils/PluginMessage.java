package ltd.icecold.icemusic.utils;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ltd.icecold.icemusic.IceMusic;
import ltd.icecold.icemusic.interfaceservice.MessageService;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author ice_cold
 * @date 2019/8/7 17:00
 */
public class PluginMessage {
    private static final int IDX = 6666;

    /**
     * 兼容高版本forge发送信息
     * @param player 玩家
     * @param msg 信息
     */
    public static void send(Player player, String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(bytes.length + 1);
        buf.writeByte(IDX);
        buf.writeBytes(bytes);
        player.sendPluginMessage(IceMusic.getInstance(), IceMusic.getChannel(), buf.array());
    }

    /**
     * 兼容高版本forge读取信息
     * @param array 数据
     */
    public static String read(byte[] array) {
        ByteBuf buf = Unpooled.wrappedBuffer(array);
        if (buf.readUnsignedByte() == IDX) {
            return buf.toString(StandardCharsets.UTF_8);
        } else {
            throw new RuntimeException();
        }
    }


}
