package cn.eoe.app.cn.eoe.app.ui;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.musicapplication.R;

import java.util.ArrayList;
import java.util.List;

import cn.eoe.app.entity.base.ActivityCollector;
import cn.eoe.app.cn.eoe.app.db.MusicScanf;
import cn.eoe.app.entity.Song;
import cn.eoe.app.utils.LrcGetContent;
import cn.eoe.app.utils.MusicService;
import cn.eoe.app.entity.base.BaseActivity;
import cn.eoe.app.widget.WordView;


/**
 * Created by Administrator on 2017/4/9.
 */

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener {
    private final String NOTIFICATION_PRVIOUS_MUSIC = "ACTION_PRV";
    private final String NOTIFICATION_NEXT_MUSIC = "ACTION_NEXT";
    private final String NOTIFICATION_PLAY_MUSIC = "ACTION_PLAY";
    private final String NOTIFICATION_EXIT_MUSIC = "ACTION_EXIT";
    public static final String NOTIFICATION_PLAY_MODE = "ACTION_PLAYMODE";
    public static final int ORDER_PLAY = 1;
    public static final int RANDOM_PLAY = 2;
    public static final int SINGLE_PLAY = 3;
    int play_mode = ORDER_PLAY;

    public static List<Integer> Time = new ArrayList<>();
    private static WordView wordView;
    private static SeekBar sb_play;
    private static TextView tv_progress;
    private static TextView tv_total;
    private static TextView tv_Song;
    private static TextView tv_Singer;
    Notification notification;
     int position;
    static int mIndex;
    static int duration;
    static int currentPosition;
    boolean isPlaying;
    private NotificationManager notificationManager;
    private RemoteViews remoteViews;
    MusicScanf musicScanf = new MusicScanf();
    private List<Song> musiclist;
    String name;
    String PATH;
    String lrcPath;
    MusicService service;
    MyServiceConn conn;
    ImageView iv_pause;
    ImageView iv_playmode;
    ImageView iv_nextplay;
    ImageView iv_back;
    ImageView iv_lastplay;
    int currentPosition1;
    OnClickReceiver onClickReceiver;
    Receiver receiver;
   public class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {   //当activity 与service 绑定成功调用此方法
            service = ((MusicService.MyBinder)binder).getService();             //bindService的调用是异步的，到service的连接不是在bindService被调用后就马上完成
            service.play(PATH);         // 则播放音乐应该在调用这个方法时才使用
            isPlaying = false;
            initMyNotification();
            play_mode = service.getPlayMode();
            if (play_mode == ORDER_PLAY) {
                iv_playmode.setImageResource(R.drawable.nextplay1);
            } else if (play_mode == RANDOM_PLAY) {
                iv_playmode.setImageResource(R.drawable.shuffle);
            } else if (play_mode == SINGLE_PLAY) {
                iv_playmode.setImageResource(R.drawable.recycle);
            }
            mHandler.postDelayed(mRunable,50);  //调用更新方法
            service.seekTo(currentPosition1 );
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play_content);
        ActivityCollector.addAcitiity(this);

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_Song = (TextView) findViewById(R.id.tv_Song);
        tv_Singer = (TextView) findViewById(R.id.tv_Singer);
        wordView = (WordView) findViewById(R.id.tv_songContent);
        iv_pause = (ImageView) findViewById(R.id.iv_pause);
        iv_playmode = (ImageView) findViewById(R.id.iv_playmode);
        iv_nextplay = (ImageView) findViewById(R.id.iv_nextplay);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_lastplay = (ImageView) findViewById(R.id.iv_lastplay);
        sb_play = (SeekBar) findViewById(R.id.sb_play);
        iv_pause.setOnClickListener(this);
        iv_playmode.setOnClickListener(this);
        iv_nextplay.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_lastplay.setOnClickListener(this);

        musiclist = musicScanf.quary(this);
        Intent intent = getIntent();
        PATH = intent.getStringExtra("path");    //获取第一个页面传递的数据
        position = intent.getIntExtra("position", 0);
        lrcPath = intent.getStringExtra("lrc");
        remoteViews =new RemoteViews(getPackageName(),R.layout.notification_content);
         String name1 = musiclist.get(position).getName();
         String singer = name1.substring(0,name1.indexOf("-"));     // 重写歌手
         name1 = name1.substring(name1.indexOf("-")+1,name1.indexOf("."));     // 重写歌名
         remoteViews.setTextViewText(R.id.tv_notification_music_artist,singer);
         remoteViews.setTextViewText(R.id.tv_notification_music_title,name1);

        if (lrcPath == null) {     //界面交互时点击返回界面调用oncreate重新传入数据
            loadData(this);
            PATH = musiclist.get(position).getPath();
            getTime(setLrcPath(position));  // 获取歌词文件中的时间
            setName_Singer(position);
            wordView.getLrc(lrcPath);   // 在该方法中获取歌词文件途径
            wordView.init();
            String Minute = null;
            String Second = null;
            sb_play.setProgress(currentPosition1);
            Log.d("XXXXX",currentPosition1+"");
            int minute = currentPosition1 / 1000 / 60;      // 转换时间格式
            int second = currentPosition1 / 1000 % 60;
            if (minute < 10) {
                Minute = "0" + minute;
            } else {
                Minute = minute + "";
            }
            if (second < 10) {
                Second = "0" + second;
            } else {
                Second = second + "";
            }
            tv_progress.setText(Minute + ":" + Second);
            int size = Time.size() - 1;
            for (int i = 0; i < Time.size() - 1; i++) {

                if (currentPosition1 < Time.get(0)) {
                    mIndex = 0;
                    break;
                } else if (currentPosition1 > Time.get(i)
                        && currentPosition1 < Time.get(i + 1)) {
                    mIndex = i;      // 获取下标
                    break;
                }
            }
            wordView.getIndex(mIndex);       //显示当前歌词
            if (currentPosition1 >= Time.get(size)) {
                wordView.getIndex(size);
            }
            wordView.getIndex(mIndex);
          }
           else{
            getTime(lrcPath);  // 获取歌词文件中的时间
            setName_Singer(position);
            wordView.getLrc(lrcPath);   // 在该方法中获取歌词文件途径
            wordView.init();   }//获取歌词
        Intent intent1 = new Intent(MusicPlayActivity.this, MusicService.class);
        intent1.putExtra("position", position);
        conn = new MyServiceConn();
        bindService(intent1, conn, BIND_AUTO_CREATE);   // 绑定服务
        startService(intent1);
        sb_play.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {    //seekbar 拖动进度条调用方法
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    String Minute = null;
                    String Second = null;
                    int  minute =  progress / 1000 / 60;      // 转换时间格式
                    int second =  progress / 1000 % 60;
                    if (minute < 10) {
                        Minute = "0" + minute;
                    } else {
                        Minute = minute + "";
                    }
                    if (second < 10) {
                        Second = "0" + second;
                    } else {
                        Second = second + "";
                    }
                    tv_progress.setText(Minute + ":" + Second);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {      //当进度条停止调用此方法
                int progress = seekBar.getProgress();       // 获取当前进度
                service.seekTo(progress);
                int size = Time.size()-1;                   //进度条改变歌词随之改变
                for (int i = 0; i <Time.size()-1; i++) {
                    if (progress < Time.get(0)) {
                        mIndex = 0;
                        break;
                    } else if (progress > Time.get(i)
                            && progress < Time.get(i + 1)) {
                        mIndex = i;
                        break;
                    }
                    wordView.getIndex(mIndex);        // 获取下标
                }
                if(progress>=Time.get(size)){
                    wordView.getIndex(size);    //显示当前歌词
                }
            }
        });

        onClickReceiver = new OnClickReceiver();
        IntentFilter intentPrvFilter = new IntentFilter();          //   注册广播
        intentPrvFilter.addAction(NOTIFICATION_PLAY_MUSIC);     //  意图过滤
        registerReceiver(onClickReceiver, intentPrvFilter);        //  接收到对应的广播

        IntentFilter intentExitFilter = new IntentFilter();
        intentExitFilter.addAction(NOTIFICATION_NEXT_MUSIC);
        registerReceiver(onClickReceiver, intentExitFilter);

        IntentFilter intentPlayFilter = new IntentFilter();
        intentPlayFilter.addAction(NOTIFICATION_PRVIOUS_MUSIC);
        registerReceiver(onClickReceiver, intentPlayFilter);

        IntentFilter intentPauseFilter = new IntentFilter();
        intentPauseFilter.addAction(NOTIFICATION_EXIT_MUSIC);
        registerReceiver(onClickReceiver, intentPauseFilter);

         receiver = new Receiver();
        IntentFilter intentPlayModeFilter = new IntentFilter();     // 注册广播
        intentPlayModeFilter.addAction(NOTIFICATION_PLAY_MODE );     //  意图过滤
        registerReceiver(receiver, intentPlayModeFilter); //  接收到对应的广播

    }

    private void loadData(Context context) {     //取出数据
     SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        position= sp.getInt("position",0);
        currentPosition1 = sp.getInt("CURRENTPOSITION",5);
   }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onClickReceiver);
        unregisterReceiver(receiver);
        ActivityCollector.removeActivity(MusicPlayActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }  //按back键返回到后台运行


    public void getTime(String lrc) {     //获取歌词时间的方法
        LrcGetContent lrcGetContent = new LrcGetContent();
        Time = lrcGetContent.getTime(lrcGetContent.readLRC(lrc));
    }

    public static Handler handler = new Handler() {        //主线程接收数据
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            duration = bundle.getInt("duration");        //  获得service中的数据
            currentPosition = bundle.getInt("currentPosition");
            sb_play.setMax(duration);           //   设置进度条的长度
            sb_play.setProgress(currentPosition);    //  设置当前进度
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String Minute = null;
            String Second = null;
            if (minute < 10) {
                Minute = "0" + minute;
            } else {
                Minute = minute + "";
            }
            if (second < 10) {
                Second = "0" + second;
            } else {
                Second = second + "";
            }
            tv_total.setText(Minute + ":" + Second);
            minute = currentPosition / 1000 / 60;      // 转换时间格式
            second = currentPosition / 1000 % 60;
            if (minute < 10) {
                Minute = "0" + minute;
            } else {
                Minute = minute + "";
            }
            if (second < 10) {
                Second = "0" + second;
            } else {
                Second = second + "";
            }
            tv_progress.setText(Minute + ":" + Second);
            int size = Time.size()-1;
            for (int i = 0; i <Time.size()-1; i++) {
                if (currentPosition < Time.get(0)) {
                    mIndex = 0;
                    break;
                } else if (currentPosition > Time.get(i)
                        && currentPosition < Time.get(i + 1)) {
                    mIndex = i;      // 获取下标
                    break;
                }
            }
            wordView.getIndex(mIndex);       //显示当前歌词
            if(currentPosition>=Time.get(size))
            {wordView.getIndex(size);}
        }
    };

    public void  setName_Singer(int position) {     // 在页面中显示歌曲与歌手信息
        String name = musiclist.get(position).getName();
        String singer=name.substring(0,name.indexOf("-"));  // 重写歌手
        name=name.substring(name.indexOf("-")+1,name.indexOf("."));  // 重写歌名
        tv_Singer.setText(singer);
        tv_Song.setText(name);
    }

    public String setLrcPath(int position) {             //获取lrc途径的方法
        name = musiclist.get(position).getName().replace(" - ", "-");
        name = name.replace("mp3", "");
        Log.d("TAG", name);
        lrcPath = "/storage/emulated/0/Musiclrc/" + name + "lrc";
        return lrcPath;
    }

    public void wordsConnected(String lrcPath) {  //进行歌曲切换时重新获取歌曲中的时间及歌词
        getTime(lrcPath);
        wordView.getLrc(lrcPath);
        wordView.init();
    }
    public  void initMyNotification() {      // 设置通知栏
        Intent intent = new Intent(this, MusicPlayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // 只有当通知栏被点击才触发pendingintent
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        notification =builder.setContentIntent(pendingIntent).setTicker("本地音乐").setContent(remoteViews).setSmallIcon(R.drawable.music4).build(); // 设置通知栏参数
        remoteViews =new RemoteViews(this.getPackageName(),R.layout.notification_content);                                         // 远程view 跟通知栏联系形成自定义通知栏
        Intent play_pouse1 = new Intent(NOTIFICATION_PLAY_MUSIC);
        PendingIntent int_play_pouse = PendingIntent.getBroadcast(this, 2, play_pouse1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_play, int_play_pouse);          //点击触发发送广播

        Intent prv = new Intent(NOTIFICATION_PRVIOUS_MUSIC);
        PendingIntent int_prv = PendingIntent.getBroadcast(this, 1, prv, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_privous, int_prv);     //点击触发发送广播

        Intent next = new Intent(NOTIFICATION_NEXT_MUSIC);
        PendingIntent int_next = PendingIntent.getBroadcast(this,4,next,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next,int_next);       //点击触发发送广播

        Intent exit =new Intent(NOTIFICATION_EXIT_MUSIC);
        PendingIntent int_exit = PendingIntent.getBroadcast(this,6,exit,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_exit,int_exit);              //点击触发发送广播
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);     // NotificationManager用来管理notification的显示和消失
        notificationManager.notify(5,notification);      //显示通知栏
    }

    public class OnClickReceiver extends BroadcastReceiver {          // 接收广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String notification_msg = intent.getAction();
            remoteViews = new RemoteViews(getPackageName(), R.layout.notification_content);
            if (notification_msg.equals(NOTIFICATION_PRVIOUS_MUSIC)){                     // 检索接收对象
                service.lastPlay();                                             //执行相应方法
                position = service.getPosition();
                setName_Singer(position);
                wordsConnected((setLrcPath(position)));
            }

            if (notification_msg.equals(NOTIFICATION_PLAY_MUSIC)){
                if (!isPlaying) {
                    service.pausePlay();
                    isPlaying = true;
                    iv_pause.setImageResource(R.drawable.play);
                    remoteViews.setImageViewResource(R.id.iv_notification_play,R.drawable.play1);
                } else {
                    service.continuePlay();
                    isPlaying = false;
                    iv_pause.setImageResource(R.drawable.pause);
                    remoteViews.setImageViewResource(R.id.iv_notification_play,R.drawable.pause1);
                }
            }

            if (notification_msg.equals(NOTIFICATION_NEXT_MUSIC)){
                service.nextPlay();
                position = service.getPosition();
                setName_Singer(position);
                wordsConnected(setLrcPath(position));
            }
            if (notification_msg.equals(NOTIFICATION_EXIT_MUSIC)){
                    notificationManager.cancel(5);
                    unbindService(conn);
                    service.onDestroy();
                    ActivityCollector.finishAll();
            }
        }
    }
    public class Receiver extends BroadcastReceiver{   //接受广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String notification_msg = intent.getAction();
            if (notification_msg.equals(NOTIFICATION_PLAY_MODE)){             // 检索接收对象
                int position = intent.getIntExtra("position",0);                  //执行相应方法
                service.play(musiclist.get(position).getPath());
                setName_Singer(position);
                wordsConnected(setLrcPath(position));
            }
        }
    }
    public void onClick(View V) {             //点击按钮实现相应方法
        switch (V.getId()) {
            case R.id.iv_pause: {
                if (!isPlaying) {
                    service.pausePlay();
                    isPlaying = true;
                    iv_pause.setImageResource(R.drawable.play);
                } else {
                    service.continuePlay();
                    isPlaying = false;
                    iv_pause.setImageResource(R.drawable.pause);
                }
            }
            break;

            case R.id.iv_nextplay: {
                service.nextPlay();
                position =service.getPosition();
                setName_Singer(position);
                wordsConnected(setLrcPath(position));
            }
            break;

            case R.id.iv_lastplay: {
                service.lastPlay();
                position = service.getPosition();
                setName_Singer(position);
                wordsConnected(setLrcPath(position));
            }
            break;

            case R.id.iv_playmode: {
                if (play_mode == ORDER_PLAY) {
                    play_mode = RANDOM_PLAY;
                    iv_playmode.setImageResource(R.drawable.shuffle);
                    Toast.makeText(MusicPlayActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                } else if (play_mode == RANDOM_PLAY) {
                    play_mode = SINGLE_PLAY;
                    iv_playmode.setImageResource(R.drawable.recycle);
                    Toast.makeText(MusicPlayActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (play_mode == SINGLE_PLAY) {
                    play_mode = ORDER_PLAY;
                    iv_playmode.setImageResource(R.drawable.nextplay1);
                    Toast.makeText(MusicPlayActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                }
                service.changeMode(play_mode);    //发送播放模式
            }
            break;
            case R.id.iv_back:
            {Intent intent =new Intent(this,MusicListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);}
            default:
                break;
        }
    }

    Handler mHandler = new Handler();   // 创建handler对象
    Runnable mRunable = new Runnable() {
        @Override
        public void run() {         //开启线程
            wordView.invalidate();//更新视图
                initMyNotification();//更新通知栏
                if (isPlaying) {
                    remoteViews.setImageViewResource(R.id.iv_notification_play, R.drawable.play1);
                } else {
                    remoteViews.setImageViewResource(R.id.iv_notification_play, R.drawable.pause1);
                }
                position =service.getPosition();
                String name = musiclist.get(position).getName();
                Log.d("ccccc",position+"");
                String singer = name.substring(0, name.indexOf("-"));     // 重写歌手
                name = name.substring(name.indexOf("-") + 1, name.indexOf("."));     // 重写歌名
                remoteViews.setTextViewText(R.id.tv_notification_music_artist, singer);
                remoteViews.setTextViewText(R.id.tv_notification_music_title, name);
            mHandler.postDelayed(this, 50);     //进行定时更新
        }
    };
}



