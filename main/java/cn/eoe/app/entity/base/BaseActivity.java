package cn.eoe.app.entity.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/5/9.
 */

public class BaseActivity extends Activity {   //activity继承该类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addAcitiity(this);   //收集子类activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);   //移除子类activity
    }
}
