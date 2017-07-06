package cn.eoe.app.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.eoe.app.entity.MyLrc;

/**
 * Created by Administrator on 2017/4/18.
 */

public class LrcGetContent  {
    public List<MyLrc> lrcContent=new ArrayList<>();
    public List<String> Words = new ArrayList<>();
    public List<Integer> Time = new ArrayList<>();
    String title1;
    private List<String> title = new ArrayList<>(); //处理歌词文件
    public List<MyLrc> readLRC(String lrcPath) {
        File file = new File(lrcPath);  //打开文件
        try {
            FileInputStream fileInputStream = new FileInputStream(file);      //从文件中获取输入字节
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");  //将字节流换为字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  //使用缓存区
            String content = "";
            while ((content = bufferedReader.readLine()) != null) {
                MyLrc myLrc =new MyLrc();
                Matcher matcher = Pattern.compile("\\[\\d{1,2}:\\d{1,2}(\\.\\d{1,2})?\\]").matcher(content); //匹配正则表达式
                if (matcher.find()) {
                    String content2 = matcher.group();
                      myLrc.setTime(new LrcGetContent().getTime(content2.substring(1, content2.length() - 1)));   //加入lrc文件转换后的时间
                }
                if ((content.indexOf("[ar") != -1) || (content.indexOf("[ti") != -1) || (content.indexOf("[al") != -1) || (content.indexOf("[by") != -1)){
                    title1 = content.substring(content.indexOf(":") + 1, content.indexOf("]"));
                    title.add(title1);                                 //去掉开头没有时间对应的信息
                 } else {
                    String content1 = content.substring(content.indexOf(""), content.indexOf("]") + 1);    //加入lrc文件转换后的歌词
                    content = content.replace(content1, "");
                    myLrc.setLrcWord(content);
                    lrcContent.add(myLrc);                         //形成特定类型的集合
                 }
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcContent;
    }
    private int getTime(String time) {
        time = time.replace(".", ":");         // 替换符号
        String timeData[] = time.split(":");         // 分割数据
        int minute = Integer.parseInt(timeData[0]);       // 将字符型数据转换为整数型数据
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        int currenttime = ((minute * 60) + second) * 1000 + millisecond * 10;
        return currenttime;
    }
    public  List<String>getWords( List<MyLrc> list){      // 根据对应关系获取歌词集合
        List<String> Words = new ArrayList<>();
        for(int i=0;i<list.size();i++){
           Words.add(list.get(i).getLrcWord());
        }
        return Words;
    }
    public List<Integer>getTime( List<MyLrc> list){     // 根据对应关系获取歌曲时间点集合
        List<Integer> Time = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            Time.add(list.get(i).getTime());
        }
        return Time;
    }
}

