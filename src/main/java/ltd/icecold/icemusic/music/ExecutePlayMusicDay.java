package ltd.icecold.icemusic.music;

import ltd.icecold.icemusic.config.Timing;
import org.bukkit.Bukkit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutePlayMusicDay {
    public ExecutePlayMusicDay(){
        if((Boolean) Timing.get("timing.enable")){
            List<String> timingList = Timing.timingName();
            for (String timing:timingList){
                executeEightAtNightPerDay(Timing.getTime(timing),Timing.getMusicName(timing));
            }
        }
    }
    public void executeEightAtNightPerDay(String time,String musicName) {
        //"20:00:00"
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay  = getTimeMillis(time) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        executor.scheduleAtFixedRate(
                () -> {
                    MusicHandle.playServerMusic2Player(musicName,Bukkit.getConsoleSender());
                },
                initDelay,
                oneDay,
                TimeUnit.MILLISECONDS);
    }

    private static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
