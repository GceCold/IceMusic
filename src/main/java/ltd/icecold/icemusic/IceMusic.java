package ltd.icecold.icemusic;

import lk.vexview.api.VexViewAPI;
import ltd.icecold.icemusic.command.CommandHandler;
import ltd.icecold.icemusic.config.*;
import ltd.icecold.icemusic.events.MessageListener;
import ltd.icecold.icemusic.events.PlayerListener;
import ltd.icecold.icemusic.music.ExecutePlayMusicDay;
import ltd.icecold.icemusic.utils.PluginMessage;
import ltd.icecold.icemusic.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class IceMusic extends JavaPlugin {
    /**
     * 插件版本
     */
    private static final String version = "1.0.0";
    /**
     * 主类实例
     */
    private static IceMusic instance;
    /**
     * 插件通信名
     */
    private static final String CHANNEL = "vexmusic:message";
    /**
     * Vault
     */
    private static Economy economy;
    /**
     * 是否使用BossBarApi
     */
    public static boolean useBarApi = false;
    /**
     * NMS包名版本
     */
    public static String NMSVersion;
    /**
     * ActionBarAPI
     */
    public static Boolean useOldMethods = false;


    @Override
    public void onEnable() {
        System.out.println(Utils.isSpigot());
        instance = this;
        initFile();

        getServer().getConsoleSender().sendMessage("§a =============§6[IceMusic]§a=============");
        getServer().getConsoleSender().sendMessage("§a =  §3服务器核心版本:§f" + Bukkit.getBukkitVersion());
        getServer().getConsoleSender().sendMessage("§a =  插件版本：§f1.0.0");
        checkConfigFile();
        checkDepend();
        setupEconomy();
        register();
        new ExecutePlayMusicDay();
        getServer().getConsoleSender().sendMessage("§a ==================================== ");

        loginNetease();

        NMSVersion = Bukkit.getServer().getClass().getPackage().getName();
        NMSVersion = NMSVersion.substring(NMSVersion.lastIndexOf(".") + 1);
        if ("v1_8_R1".equalsIgnoreCase(NMSVersion) || NMSVersion.startsWith("v1_7_")) {
            useOldMethods = true;
        }

    }

    @Override
    public void onDisable() {

    }

    public void loginNetease(){
        if(Login.getWyPassword() == null || Login.getWyPassword().isEmpty()){
           return;
        }
        Map<String, String> loginMessage = new HashMap<>();
        if (!Login.getWyPhone().isEmpty()){
            loginMessage.put("username", Login.getWyPhone());
            Bukkit.getServer().getConsoleSender().sendMessage("[IceMusic] > "+ ChatColor.AQUA +"已开启网易云账号登录，正在登录 账号："+ChatColor.GOLD+Login.getWyPhone());
        }else {
            loginMessage.put("username", Login.getWyUserName());
            Bukkit.getServer().getConsoleSender().sendMessage("[IceMusic] > "+ ChatColor.AQUA +"已开启网易云账号登录，正在登录 邮箱："+ChatColor.GOLD+Login.getWyUserName());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Login.getWyUserName().isEmpty() && Login.getWyPhone().isEmpty()){
                    cancel();
                    return;
                }
                loginMessage.put("password", Login.getWyPassword());
                String result = PluginMessage.sendMsgToServer("NETEASE_LOGIN",loginMessage);
                if("502".equals(result)){
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY +"[IceMusic] > §e登录失败！网易云音乐账号或密码错误");
                    cancel();
                }
                if ("501".equals(result)){
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY +"[IceMusic] > §e登录失败！您输入的网易云账号邮箱格式错误或服务器通讯错误");
                    cancel();
                }
                if ("200".equals(result)){
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY +"[IceMusic] > "+ChatColor.GREEN+"网易云音乐登录成功");
                }
            }
        }.runTaskTimerAsynchronously(this,10,20*60*20);
    }


    /**
     * 插件注册
     */
    private void register(){
        //注册指令
        Bukkit.getPluginCommand("music").setExecutor(CommandHandler.instance);
        //注册通道
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL, new MessageListener());
        //监听器
        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
    }

    /**
     * 初始化配置文件
     */
    private void initFile() {
        writeConfig("config.yml");
        writeConfig("login.yml");
        writeConfig("soundlist.yml");
        writeConfig("worldBGM.yml");
        writeConfig("regionBGM.yml");
        writeConfig("language.yml");
        writeConfig("timing.yml");
        //GUI配置文件
        writeConfig("gui/musiclist.yml");
        writeConfig("gui/search.yml");
        writeConfig("gui/main.yml");
        writeConfig("gui/toplist.yml");
        writeConfig("gui/listdetail.yml");
        writeConfig("gui/musicresult.yml");
        writeConfig("gui/playlistresult.yml");

        Config.init();
        Login.init();
        Language.init();
        WorldBgm.init();
        Timing.init();
    }

    /**
     * 写出配置文件
     * @param fileName 文件名
     */
    private void writeConfig(String fileName) {
        File file = new File(IceMusic.getInstance().getDataFolder(), fileName);
        if (!file.exists()) {
            this.saveResource(fileName, false);
        }
    }

    /**
     * 检查config.yml文件版本与是否已同意EULA协议
     */
    private void checkConfigFile(){
        String configVersion = Config.getVersion();
        if (!configVersion.equals(version)){
            File configFile = new File(this.getDataFolder(),"config.yml");
            File oldConfigFile = new File(this.getDataFolder(),"config - old - "+configVersion+".yml");
            configFile.renameTo(oldConfigFile);
            writeConfig("config.yml");
            Bukkit.getConsoleSender().sendMessage("§a = §c 检测到您的§e config.yml§c 的版本为§e "+configVersion+" §c本次更新更新了此文件，已将您原本的文件重命名为 §e"+"config - old - "+configVersion+".yml"+"§c 请自行修改配置");
        }
        if (!Config.getEula()){
            sendError("请同意位于§e config.yml§c 中的EULA协议");
        }
    }

    /**
     * 检测插件依赖
     */
    private void checkDepend() {
        if (Bukkit.getPluginManager().isPluginEnabled("VexView")) {
            getServer().getConsoleSender().sendMessage("§a =  §6已检测到VexView v" + VexViewAPI.getVexView().getVersion());
        } else {
            sendError("未检测到VexView，VexView为必须前置");
            return;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            sendError("未检测到Vault，Vault为必须前置");
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("BossBarAPI")) {
            useBarApi = true;
            getServer().getConsoleSender().sendMessage(" §a= §d 检测到BossBarAPI");
        }
    }

    /**
     * 输出错误
     * @param errorMsg 错误信息
     */
    private static void sendError(String errorMsg) {
        Bukkit.getServer().getConsoleSender().sendMessage("§a =  §c错误信息:§l§e" + errorMsg);
        Bukkit.getServer().getConsoleSender().sendMessage("§a ==================================== ");
        Bukkit.getPluginManager().disablePlugin(IceMusic.getInstance());
    }

    /**
     * 初始化Vault
     */
    public void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static IceMusic getInstance() {
        return instance;
    }

    public static String getChannel() {
        return CHANNEL;
    }

}
