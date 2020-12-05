package ltd.icecold.icemusic.utils;

import ltd.icecold.icemusic.IceMusic;
import org.bukkit.Bukkit;

/**
 * @author ice_cold
 * @date 2019/7/19 11:42
 */
public class VaultHandle {
    public static boolean delMoney(String name, int money)
    {
        if (!IceMusic.getEconomy().hasAccount(Bukkit.getPlayer(name))) {
            return false;
        }
        if (IceMusic.getEconomy().has(Bukkit.getPlayer(name), money)) {
            IceMusic.getEconomy().withdrawPlayer(Bukkit.getPlayer(name),money);
        }
        return true;
    }
    public static boolean hasMoney(String name, int money)
    {
        if ((name == null) || (name.length() <= 0)) {
            return false;
        }
        if (money <= 0.0D) {
            return true;
        }
        return IceMusic.getEconomy().has(Bukkit.getPlayer(name), money);
    }
    public static Integer getMoney(String name)
    {
        return (int) IceMusic.getEconomy().getBalance(Bukkit.getPlayer(name));
    }
}
