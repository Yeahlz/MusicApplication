package cn.eoe.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.musicapplication.R;

import java.util.List;

import cn.eoe.app.entity.Song;

/**
 * Created by Administrator on 2017/4/8.
 */
public class MusicListAdapter extends BaseAdapter {  //重写设配器
    private List<Song> musicList;
    private Context context;
    String name;
    String singer;
    public MusicListAdapter(Context context, List<Song> musiclist) {  //构造函数
        this.context = context;
        this.musicList = musiclist;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {  // 视图优化
        ViewHolder holder=null ;
        if (convertView == null) {    //convertView不存在时
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.music_list_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_musiclist_song);      // viewholder搜寻布局文件中的id
            holder.singer = (TextView) convertView.findViewById(R.id.tv_musiclist_singer);
            holder.duration = (TextView) convertView.findViewById(R.id.tv_musiclist_time);
            holder.position = (TextView) convertView.findViewById(R.id.tv_musiclist_postion);
            convertView.setTag(holder); //item布局的View对象并赋给convertView
        } else {
            holder = (ViewHolder) convertView.getTag();  //存在时取出缓存的view,不用重新创建
        }
          name = musicList.get(position).getName();
        if(name!=null) {
            singer = name.substring(0, name.indexOf("-"));  //重写歌手
            name = name.substring(name.indexOf("-") + 1, name.indexOf("."));  //重写歌名
        }
          holder.name.setText(name);     //输入控件内容
          holder.singer.setText(singer);
          int duration = musicList.get(position).getDuration();
          String time = MusicListAdapter.this.changeTime(duration);
          holder.duration.setText(time);
          holder.position.setText(position + 1 + "");
          return convertView;
    }

    public class ViewHolder {
        TextView name;
        TextView singer;
        TextView duration;
        TextView position;
    }

    public String changeTime(int time) {   // 转换时间
        if (time / 1000 %60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}

