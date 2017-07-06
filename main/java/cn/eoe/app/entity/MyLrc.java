package cn.eoe.app.entity;

/**
 * Created by Administrator on 2017/4/19.
 */

public class MyLrc {
    private int time;
    private String lrcWord;
    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
    }

    public String getLrcWord(){return lrcWord;}

    public void setLrcWord(String lrcWord){
        this.lrcWord=lrcWord;
    }

}
