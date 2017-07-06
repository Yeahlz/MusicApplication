package cn.eoe.app.cn.eoe.app.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

import cn.eoe.app.entity.Song;

/**
 * Created by Administrator on 2017/4/8.
 */

public class MusicScanf {
    public ArrayList<Song> quary(Context context)  {
        ArrayList<Song>musiclist=new ArrayList<Song>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,    //通过query方法搜寻媒体类数据库
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);                    //参数分别是访问歌曲信息的uri,查询列，查询条件，排序方式
        if (cursor != null) {
            Song song;
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {    // 移动光标
                song=new Song();       //新建类存储信息
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));    //  歌曲路径
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); //  歌曲名称
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  // 歌手名称
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));  // 播放时长
                song.setduration(duration);
                song.setname(name);
                song.setpatch(path);
                song.setsinger(singer);
                song.setalbum(album);
                musiclist.add(song);  //将信息装进集合
            }
            cursor.close();   //关闭光标
        }

        return musiclist;
    }

}

