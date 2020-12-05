package ltd.icecold.icemusic.music;

import com.google.gson.*;
import ltd.icecold.icemature.NeteaseMusic;
import ltd.icecold.icemusic.bean.ReturnMessage;
import ltd.icecold.server.bean.message.*;
import ltd.icecold.server.bean.web.NewPlayListBean;
import ltd.icecold.server.handlers.LoginHandler;
import ltd.icecold.server.util.http.HttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Future;

public class MusicInfo {

    public static ReturnMessage musicPlay(String musicName) throws Exception {
        Gson gson = new Gson();
        NeteaseMusic neteaseMusic = new NeteaseMusic();
        String search = neteaseMusic.search(musicName, 1);

        Integer code = new JsonParser().parse(search).getAsJsonObject().get("code").getAsInt();
        ReturnMessage returnMessage = new ReturnMessage();
        if (code != 200) {
            returnMessage.setType("SEARCH_ERROR");
            returnMessage.setCode("404");
            return returnMessage;
        }
        Integer songCount = new JsonParser().parse(search).getAsJsonObject().get("result").getAsJsonObject().get("songCount").getAsInt();
        if (songCount == 0) {
            returnMessage.setType("SEARCH_0");
            returnMessage.setCode("404");
            return returnMessage;
        }
        JsonArray songs = new JsonParser().parse(search).getAsJsonObject().get("result").getAsJsonObject().get("songs").getAsJsonArray();
        long musicId = songs.get(0).getAsJsonObject().get("id").getAsLong();
        musicName = songs.get(0).getAsJsonObject().get("name").getAsString();
        JsonArray artists = songs.get(0).getAsJsonObject().get("artists").getAsJsonArray();
        StringBuffer stringBuffer = new StringBuffer();
        for (JsonElement art : artists) {
            stringBuffer.append(art.getAsJsonObject().get("name").getAsString()).append(" ");
        }

        String resultInfo = neteaseMusic.songDetail(musicId + "");
        String picUrl = new JsonParser().parse(resultInfo).getAsJsonObject()
                .get("songs").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("al").getAsJsonObject()
                .get("picUrl").getAsString();


        Future<String> getUrl = new HttpClient().sendGet("http://localhost:3000/song/url?id=" + musicId + "&br=320000", FlushNeteaseCookie.getCookie(remoteAddress));
        while (getUrl.isDone()) ;
        result = getUrl.get();
        code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
        if (code != 200) {
            returnMessage.put("type", "URL_ERROR");
            returnMessage.put("code", "404");
            return gson.toJson(returnMessage);
        }
        if (new JsonParser().parse(result).getAsJsonObject().get("data").getAsJsonArray().size() == 0) {
            returnMessage.put("type", "URL_ERROR");
            returnMessage.put("code", "404");
            return gson.toJson(returnMessage);
        }
        String url = new JsonParser().parse(result).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
        MusicMessageBean musicMessageBean = new MusicMessageBean();
        MusicMessageBean.DataBean dataBean = new MusicMessageBean.DataBean();
        dataBean.setMusicID(String.valueOf(musicId));
        dataBean.setMusicName(musicName);
        dataBean.setMusicURL(url);
        dataBean.setMusicArtists(stringBuffer.toString());
        dataBean.setMusicPic(picUrl);
        musicMessageBean.setData(dataBean);
        musicMessageBean.setCode("200");
        return gson.toJson(musicMessageBean);
    }

    public static String musicIdPlay(String msg, String remoteAddress) throws Exception {
        /**
         * {
         *     "musicName": "xxx",
         *     "userName": "xxx",
         *     "useCode": "xxx"
         * }
         */
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String musicId = new JsonParser().parse(msg).getAsJsonObject().get("musicId").getAsString();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> getInfo = new HttpClient().sendGet("http://localhost:3000/song/detail?ids=" + musicId, FlushNeteaseCookie.getCookie(remoteAddress));
            while (getInfo.isDone()) ;
            String resultInfo = getInfo.get();
            String picUrl = new JsonParser().parse(resultInfo).getAsJsonObject()
                    .get("songs").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("al").getAsJsonObject()
                    .get("picUrl").getAsString();

            Future<String> getUrl = new HttpClient().sendGet("http://localhost:3000/song/url?id=" + musicId + "&br=320000", FlushNeteaseCookie.getCookie(remoteAddress));
            while (getUrl.isDone()) ;
            String result = getUrl.get();
            int code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
            if (code != 200) {
                returnMessage.put("type", "URL_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            String url = new JsonParser().parse(result).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            MusicMessageBean musicMessageBean = new MusicMessageBean();
            MusicMessageBean.DataBean dataBean = new MusicMessageBean.DataBean();
            dataBean.setMusicID(String.valueOf(musicId));
            dataBean.setMusicName("");
            dataBean.setMusicURL(url);
            dataBean.setMusicPic(picUrl);
            musicMessageBean.setData(dataBean);
            musicMessageBean.setCode("200");
            return gson.toJson(musicMessageBean);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String musicLyric(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String musicId = new JsonParser().parse(msg).getAsJsonObject().get("musicId").getAsString();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> getUrl = new HttpClient().sendGet("http://localhost:3000/lyric?id=" + musicId, FlushNeteaseCookie.getCookie(remoteAddress));
            while (getUrl.isDone()) ;
            String result = getUrl.get();
            if (new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt() != 200) {
                returnMessage.put("type", "Lyric_Error");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            if (new JsonParser().parse(result).getAsJsonObject().has("lrc")) {
                if (new JsonParser().parse(result).getAsJsonObject().has("lrc")) {
                    if (new JsonParser().parse(result).getAsJsonObject().get("lrc").getAsJsonObject().has("lyric")) {
                        returnMessage.put("needDesc", "false");
                        returnMessage.put("lyric", new JsonParser().parse(result).getAsJsonObject().get("lrc").getAsJsonObject().get("lyric").getAsString());
                        returnMessage.put("havetlyric", "false");
                        returnMessage.put("code", "200");
                    }
                }

                //歌词翻译 —— 移除（2020年8月18日22:42:58）

                /**
                 if (new JsonParser().parse(result).getAsJsonObject().has("tlyric")) {
                 if (new JsonParser().parse(result).getAsJsonObject().get("tlyric").getAsJsonObject().has("lyric")) {
                 if (!new JsonParser().parse(result).getAsJsonObject().get("tlyric").getAsJsonObject().get("lyric").isJsonNull()){
                 returnMessage.put("tlyric",new JsonParser().parse(result).getAsJsonObject().get("tlyric").getAsJsonObject().get("lyric").getAsString());
                 returnMessage.put("havetlyric","true");
                 }
                 }
                 }
                 */

                return gson.toJson(returnMessage);
            }

            returnMessage.put("needDesc", "true");
            returnMessage.put("desc", "无歌词");
            returnMessage.put("code", "404");
            return gson.toJson(returnMessage);

        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String getNewPlayList(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        String num = new JsonParser().parse(msg).getAsJsonObject().get("num").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> newSong = new HttpClient().sendGet("http://localhost:3000/personalized/", new HashMap<>());
            while (newSong.isDone()) ;
            String result = newSong.get();
            NewPlayListBean newPlayListBean = gson.fromJson(result, NewPlayListBean.class);
            if (newPlayListBean.getCode() != 200) {
                returnMessage.put("type", "SEARCH_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            NewPlayListMessageBewan newPlayListMessageBewan = new NewPlayListMessageBewan();
            newPlayListMessageBewan.setCode("200");
            List<NewPlayListMessageBewan.DataBean> dataBeanList = new ArrayList<>();
            List<NewPlayListBean.ResultBean> result1 = newPlayListBean.getResult();
            int[] randoms = randoms(newPlayListBean.getResult().size() - 1, Integer.parseInt(num));
            for (int i = 0; i < Integer.parseInt(num); i++) {
                NewPlayListBean.ResultBean resultBean = result1.get(randoms[i]);
                NewPlayListMessageBewan.DataBean dataBean = new NewPlayListMessageBewan.DataBean();
                dataBean.setId(resultBean.getId());
                dataBean.setName(resultBean.getName());
                dataBean.setPicUrl(resultBean.getPicUrl());
                dataBean.setCopywriter(resultBean.getCopywriter());
                dataBeanList.add(dataBean);
            }
            newPlayListMessageBewan.setData(dataBeanList);
            return gson.toJson(newPlayListMessageBewan);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String getNewMusic(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> playList = new HttpClient().sendGet("http://localhost:3000/personalized/newsong?data=new", FlushNeteaseCookie.getCookie(remoteAddress));
            while (playList.isDone()) ;
            String result = playList.get();
            Integer code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
            if (code != 200) {
                returnMessage.put("type", "SEARCH_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            if (!new JsonParser().parse(result).getAsJsonObject().has("result")) {
                returnMessage.put("type", "SEARCH_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            JsonArray resultArray = new JsonParser().parse(result).getAsJsonObject().get("result").getAsJsonArray();
            NewMusicMessageBean newMusicMessageBean = new NewMusicMessageBean();
            newMusicMessageBean.setCode("200");
            newMusicMessageBean.setCount(resultArray.size());
            List<NewMusicMessageBean.DataBean> dataBeanList = new ArrayList<>();
            for (JsonElement json : resultArray) {
                NewMusicMessageBean.DataBean dataBean = new NewMusicMessageBean.DataBean();
                dataBean.setId(json.getAsJsonObject().get("id").getAsInt());
                dataBean.setName(json.getAsJsonObject().get("name").getAsString());
                dataBean.setArtists(json.getAsJsonObject().get("song").getAsJsonObject().get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
                dataBeanList.add(dataBean);
            }
            newMusicMessageBean.setData(dataBeanList);
            return gson.toJson(newMusicMessageBean);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String getTopList(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String id = new JsonParser().parse(msg).getAsJsonObject().get("id").getAsString();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> getUrl = new HttpClient().sendGet("http://127.0.0.1:3000/top/list?idx=" + id, FlushNeteaseCookie.getCookie(remoteAddress));
            while (getUrl.isDone()) ;
            String result = getUrl.get();
            int code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
            if (code != 200) {
                returnMessage.put("type", "URL_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }

            TopListMessageBean topListMessageBean = new TopListMessageBean();
            topListMessageBean.setCode("200");
            List<TopListMessageBean.DataBean> dataBeanList = new ArrayList<>();
            for (JsonElement json : new JsonParser().parse(result).getAsJsonObject().get("playlist").getAsJsonObject().get("tracks").getAsJsonArray()) {
                TopListMessageBean.DataBean dataBean = new TopListMessageBean.DataBean();
                JsonObject asJsonObject = json.getAsJsonObject();
                dataBean.setMusicName(asJsonObject.get("name").getAsString());
                dataBean.setMusicID(asJsonObject.get("id").getAsLong());
                dataBeanList.add(dataBean);
            }
            topListMessageBean.setData(dataBeanList);
            return gson.toJson(topListMessageBean);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String getMusicList(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String id = new JsonParser().parse(msg).getAsJsonObject().get("id").getAsString();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> getUrl = new HttpClient().sendGet("http://localhost:3000/playlist/detail?id=" + id, FlushNeteaseCookie.getCookie(remoteAddress));
            while (getUrl.isDone()) ;
            String result = getUrl.get();
            int code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
            if (code != 200) {
                returnMessage.put("type", "URL_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }

            TopListMessageBean topListMessageBean = new TopListMessageBean();
            topListMessageBean.setCode("200");
            List<TopListMessageBean.DataBean> dataBeanList = new ArrayList<>();
            for (JsonElement json : new JsonParser().parse(result).getAsJsonObject().get("playlist").getAsJsonObject().get("tracks").getAsJsonArray()) {
                TopListMessageBean.DataBean dataBean = new TopListMessageBean.DataBean();
                JsonObject asJsonObject = json.getAsJsonObject();
                dataBean.setMusicName(asJsonObject.get("name").getAsString());
                dataBean.setMusicID(asJsonObject.get("id").getAsLong());
                dataBeanList.add(dataBean);
            }
            topListMessageBean.setData(dataBeanList);
            return gson.toJson(topListMessageBean);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String searchMusic(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String name = new JsonParser().parse(msg).getAsJsonObject().get("name").getAsString();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> getUrl = new HttpClient().sendGet("http://localhost:3000/search?keywords=" + name, FlushNeteaseCookie.getCookie(remoteAddress));
            while (getUrl.isDone()) ;
            String result = getUrl.get();
            int code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
            if (code != 200) {
                returnMessage.put("type", "URL_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            if (!new JsonParser().parse(result).getAsJsonObject().has("result")) {
                returnMessage.put("type", "SEARCH_CLEAN");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            JsonObject result1 = new JsonParser().parse(result).getAsJsonObject().get("result").getAsJsonObject();
            if (result1.get("songCount").getAsInt() == 0) {
                returnMessage.put("type", "SEARCH_CLEAN");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }

            SearchMusicMessageBean searchMusicMessageBean = new SearchMusicMessageBean();
            searchMusicMessageBean.setCode("200");
            List<SearchMusicMessageBean.DataBean> dataBeanList = new ArrayList<>();
            for (JsonElement json : result1.get("songs").getAsJsonArray()) {
                SearchMusicMessageBean.DataBean dataBean = new SearchMusicMessageBean.DataBean();
                dataBean.setName(json.getAsJsonObject().get("name").getAsString());
                dataBean.setId(json.getAsJsonObject().get("id").getAsLong());
                StringBuilder stringBuilder = new StringBuilder();
                for (JsonElement jsonArtists : json.getAsJsonObject().get("artists").getAsJsonArray()) {
                    stringBuilder.append(jsonArtists.getAsJsonObject().get("name").getAsString()).append(" ");
                }
                dataBean.setArtists(stringBuilder.toString().trim());
                dataBeanList.add(dataBean);
            }
            searchMusicMessageBean.setData(dataBeanList);
            return gson.toJson(searchMusicMessageBean);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    public static String searchMusicList(String msg, String remoteAddress) throws Exception {
        Gson gson = new Gson();
        Map<String, String> returnMessage = new HashMap<>();
        String name = new JsonParser().parse(msg).getAsJsonObject().get("name").getAsString();
        String userName = new JsonParser().parse(msg).getAsJsonObject().get("userName").getAsString();
        String useCode = new JsonParser().parse(msg).getAsJsonObject().get("useCode").getAsString();
        if (LoginHandler.checkOnlineState(userName, useCode)) {
            Future<String> getUrl = new HttpClient().sendGet("http://localhost:3000/search?type=1000&keywords=" + name, FlushNeteaseCookie.getCookie(remoteAddress));
            while (getUrl.isDone()) ;
            String result = getUrl.get();
            int code = new JsonParser().parse(result).getAsJsonObject().get("code").getAsInt();
            if (code != 200) {
                returnMessage.put("type", "URL_ERROR");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            if (!new JsonParser().parse(result).getAsJsonObject().has("result")) {
                returnMessage.put("type", "SEARCH_CLEAN");
                returnMessage.put("code", "404");
                return gson.toJson(returnMessage);
            }
            JsonObject result1 = new JsonParser().parse(result).getAsJsonObject().get("result").getAsJsonObject();

            SearchMusicListMessageBean searchMusicListMessageBean = new SearchMusicListMessageBean();
            searchMusicListMessageBean.setCode("200");
            List<SearchMusicListMessageBean.DataBean> dataBeanList = new ArrayList<>();
            for (JsonElement json : result1.get("playlists").getAsJsonArray()) {
                SearchMusicListMessageBean.DataBean dataBean = new SearchMusicListMessageBean.DataBean();
                dataBean.setName(json.getAsJsonObject().get("name").getAsString());
                dataBean.setId(json.getAsJsonObject().get("id").getAsLong());
                dataBean.setNickname(json.getAsJsonObject().get("creator").getAsJsonObject().get("nickname").getAsString());
                dataBean.setTrackCount(json.getAsJsonObject().get("trackCount").getAsInt());
                dataBean.setCoverImgUrl(json.getAsJsonObject().get("coverImgUrl").getAsString());
                dataBeanList.add(dataBean);
            }
            searchMusicListMessageBean.setData(dataBeanList);
            return gson.toJson(searchMusicListMessageBean);
        } else {
            returnMessage.put("type", "USE_CODE_ERROR");
            returnMessage.put("code", "502");
            return gson.toJson(returnMessage);
        }
    }

    private static int[] randoms(int number, int size) {
        Random rand = new Random();
        int nu[] = new int[size];
        boolean[] bool = new boolean[number + 1];
        int randint = 0;
        for (int i = 0; i < size; i++) {
            do {
                randint = rand.nextInt(number) + 1;
            } while (bool[randint]);
            bool[randint] = true;
            nu[i] = randint;
        }
        return nu;
    }

}
