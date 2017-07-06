package cn.eoe.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import cn.eoe.app.utils.LrcGetContent;
/**
 * Created by Administrator on 2017/4/18.
 */

public class WordView extends TextView{
    private List<String> words = new ArrayList<>();
    private float wordPosition_X;
    private float wordPosition_Y;
    private int wordSize = 30;
    private int INTEVAL = 45;
    int Index;
    float height;
    String Lrc;

    public WordView(Context context){
        super(context);
    }
    public WordView(Context context ,AttributeSet attrs){
        super(context,attrs);
    }
    public WordView(Context context,AttributeSet attrs,int defSytle){ super( context, attrs, defSytle);} // 第一个参数上下文环境 第二个为接收xml中该控件的属性 第三个为默认的theme

    protected void onDraw(Canvas canvas){   // 更新视图就会自动调用方法
        if(Index>= words .size()){return;}
        Paint paintL = new Paint();
        paintL.setAntiAlias(true);    // 抗锯齿效果
        paintL.setTypeface(Typeface.SERIF);  // 设置字体样式
        paintL.setColor(Color.YELLOW);
        paintL.setTextSize(wordSize+15);
        paintL.setTextAlign(Paint.Align.CENTER); //字体居中

        Paint paint=new Paint();
        paintL.setAntiAlias(true);
        paintL.setTypeface(Typeface.SERIF);
        paintL.setColor(Color.BLACK);
        paint.setTextSize(wordSize);
        paint.setAlpha(100);   //设置透明度
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText( words.get(Index),wordPosition_X,wordPosition_Y,paintL);  //当前显示歌词
        if(Index!=0){
            for(int i = Index-1,j = 1;i>= 0;i--,j++){
                String temp = words.get(i);
                if((wordPosition_Y-(wordSize+INTEVAL)*j)<height){
                    break;
                }
                canvas.drawText(temp,wordPosition_X,wordPosition_Y-(wordSize+INTEVAL)*j,paint);  //当前歌词的前几句
            }
         }
        for( int i = Index+1,j = 1;i< words.size();i++,j++){
            String temp = words .get(i);
            if((wordPosition_Y+(wordSize+INTEVAL)*j)>900){
                break;
            }
            canvas.drawText(temp,wordPosition_X,wordPosition_Y+(wordSize+INTEVAL)*j,paint); //当前歌词的后几句
        }
    }

    protected void onSizeChanged(int w,int h,int ow,int oh){     // 当屏幕大小改变是系统自动调用的函数  这里是为了获取一些跟屏幕大小想适应的数据
        wordPosition_X = w*0.5f;
        super.onSizeChanged(w,h,ow,oh);
        wordPosition_Y = h*0.4f;
        height = h*0.15f;
    }

    public void init(){                                          // 获取歌词列表
        LrcGetContent lrcGetContent=new LrcGetContent();
        words = lrcGetContent.getWords(lrcGetContent.readLRC(Lrc));
    }

    public void getIndex(int Index1){                         //获取歌词下标来显示歌词
        Index=Index1;
    }   //获取到歌词下标更换歌词

    public void getLrc(String lrc1) { Lrc = lrc1;}          // 获取当前lrc文件路径
}
