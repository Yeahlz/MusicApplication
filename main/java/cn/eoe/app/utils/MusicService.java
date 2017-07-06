package cn.eoe.app.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.eoe.app.cn.eoe.app.db.MusicScanf;
import cn.eoe.app.cn.eoe.app.ui.MusicPlayActivity;
import cn.eoe.app.entity.Song;

import static cn.eoe.app.cn.eoe.app.ui.MusicPlayActivity.NOTIFICATION_PLAY_MODE;

/**
 * Created by Administrator on 2017/4/9.
 */

public class MusicService extends Service {
    public static final String AAAAA = "AAAAA";
    private MediaPlayer mediaPlayer;
    private Timer timer;
    int position;
    MusicScanf musicScanf = new MusicScanf();
    private List<Song> musiclist;
    public static final int ORDER_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    int play_mode =ORDER_PLAY;
    String path1;
    private Random random = new Random();
    public IBinder onBind(Intent intent) {      // 通过onbind 返回ibinder 对象
        position = intent.getIntExtra("position", 0);
        return new MyBinder();
    }

    public class MyBinder extends Binder  {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer(); //创建音乐播放器对象
        OnClickReceiver onClickReceiver = new OnClickReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AAAAA);
        registerReceiver(onClickReceiver, intentFilter);
    }

    public class OnClickReceiver extends BroadcastReceiver {          // 接收广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String notification_msg = intent.getAction();
            if (notification_msg.equals(AAAAA)) {                     // 检索接收对象
                position = intent.getIntExtra("position", 0);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        }
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        if(timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        super.onDestroy();
    }

    public void play(String path) {
        try {
            path1 = path;
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();   // 重置
            mediaPlayer.setDataSource(path);  // 加载多媒体文件
            mediaPlayer.prepare();  //  准备播放音乐
            mediaPlayer.start();      //  播放音乐
            addTimer();   // 添加计时器
        } catch (IOException e) {
            e.printStackTrace();
        }
        musiclist = musicScanf.quary(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {   // 当歌曲播放完自动调用函数
                switch (play_mode) {
                    case ORDER_PLAY:
                        nextPlay();
                        play_mode = ORDER_PLAY;
                        break;
                    case RANDOM_PLAY:
                        position = random.nextInt(musiclist.size());
                        play(musiclist.get(position).getPath());
                        play_mode = RANDOM_PLAY;
                        break;
                    case SINGLE_PLAY:
                        play(musiclist.get(position).getPath());
                        play_mode = SINGLE_PLAY;
                        break;
                    default:
                        break;
                }
                Intent intent = new Intent(NOTIFICATION_PLAY_MODE);    // 发送广播 service 通知activity 更新 ui
                intent.putExtra("position",position);
                sendBroadcast(intent);
            }
        });
    }
        private void saveData(Context context, int position ,int currentPosition){     //保存数据
        SharedPreferences sp = context.getSharedPreferences("config",  MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("position",position);
        editor.putInt("CURRENTPOSITION",currentPosition);
        editor.commit();}
    public void nextPlay() {            // 下一首播放
        if (position >= musiclist.size() - 1) {
            position = 0;
        } else {
            ++position;
        }
        play(musiclist.get(position).getPath());
    }

    public void lastPlay() {       //上一首播放
        if (position - 1 < 0) {
            position = musiclist.size() - 1;
        } else {
            ++position;
        }
        play(musiclist.get(position).getPath());
    }

    public int getPosition() {
        return position;
    }

    public int getPlayMode() {
        return play_mode;
    }

    public void pausePlay() {
        mediaPlayer.pause();
    }

    public void continuePlay() {
        mediaPlayer.start();
    }

    public void changeMode(int mode) {
        play_mode = mode;
    }     // 获取播放模式的改变

    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }   //歌曲指定位置播放方法

    public void addTimer() {         //开启子线程更新数据 子线程无法更新ui
        if(mediaPlayer!=null){
        if (timer == null) {
            timer = new Timer();
            timer.schedule(timerTask, 0, 500);
        }}
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            int duration = mediaPlayer.getDuration();
            int currentPosition = mediaPlayer.getCurrentPosition();
            saveData(MusicService.this, position, mediaPlayer.getCurrentPosition());   // 在线程中保存数据
            Message msg = MusicPlayActivity.handler.obtainMessage();       //建立消息
            Bundle bundle = new Bundle();                //利用bundle封装数据
            bundle.putInt("duration", duration);
            bundle.putInt("currentPosition", currentPosition);
            msg.setData(bundle);
            MusicPlayActivity.handler.sendMessage(msg);
        }
    };
}