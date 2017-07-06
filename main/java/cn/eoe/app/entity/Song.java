package cn.eoe.app.entity;

/**
 * Created by Administrator on 2017/4/8.
 */
public class Song {
    private  String path;
    private  String name;
    private String singer;
    private  int duration;
    private String album;
    public void setpatch(  String path){this.path=path;}

    public void setname(  String name){
        this.name=name;
    }

    public void setsinger(  String singer){
        this.singer=singer;
    }

    public void setduration(  int duration){
        this.duration=duration;
    }

    public String getPath(){return path;}

    public  int getDuration(){return duration;}

    public String getName(){return name;}

    public String getSinger(){
        return singer;
    }

    public void setalbum(String album){ this.album = album;}

    public String getAlbum(){ return album;
    }
}


