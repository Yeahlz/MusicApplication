package cn.eoe.app.cn.eoe.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.musicapplication.R;

import java.util.List;

import cn.eoe.app.entity.base.ActivityCollector;
import cn.eoe.app.adapter.MusicListAdapter;
import cn.eoe.app.cn.eoe.app.db.MusicScanf;
import cn.eoe.app.entity.Song;
import cn.eoe.app.entity.base.BaseActivity;

import static cn.eoe.app.utils.MusicService.AAAAA;




public class MusicListActivity extends BaseActivity{
    private ListView musicListview;
    MusicScanf musicScanf = new MusicScanf();
    private MusicListAdapter musicListAdapter;
    private List<Song> musicList;    //  声明特定对象的集合
    String path;
    String lrc;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);
        ActivityCollector.addAcitiity(this);
        Log.d("XXXX","onCreate1");
        musicList = musicScanf.quary(this);  // 获取音乐信息
        musicListview = (ListView) findViewById(R.id.lv_musiclist);
        musicListAdapter = new MusicListAdapter(this, musicList);  //  适配器信息跟音乐信息结合
        musicListview.setAdapter(musicListAdapter);  // 生成界面
        musicListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {       // 音乐列表设置点击事件并传送相关信息
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intetnt = new Intent(AAAAA );
                intetnt.putExtra("position",position);
                sendBroadcast(intetnt);
                Intent intent2 = new Intent(MusicListActivity.this, MusicPlayActivity.class);
                path = musicList.get(position).getPath();
                name=musicList.get(position).getName().replace(" - ","-");
                name=name.replace("mp3","");                        //歌曲名跟lrc文件名相似，进行修改
                lrc = "/storage/emulated/0/Musiclrc/"+name+"lrc";  // 指定手机lrc文件路径
                intent2.putExtra("path",path);
                intent2.putExtra("position",position);       // 传递数据
                intent2.putExtra("lrc",lrc);
                startActivity(intent2);
            }
        });
    }

   @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(MusicListActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }
}






