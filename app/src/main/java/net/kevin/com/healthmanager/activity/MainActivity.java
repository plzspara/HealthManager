package net.kevin.com.healthmanager.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.fragment.FirstFragment;
import net.kevin.com.healthmanager.fragment.FourthFragment;
import net.kevin.com.healthmanager.fragment.SecondFragment;
import net.kevin.com.healthmanager.adapter.ViewPagerAdapter;
import net.kevin.com.healthmanager.fragment.ThirdFragment;
import net.kevin.com.healthmanager.javaBean.User;



import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

/**
 * 主页面，完成用户数据同步，请求权限处理，显示四个fragment
 */
public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;

    private SDKReceiver mReceiver;

    private final String appKey = "57b639f8a7a4b768d7e7add7329bcf34";

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                //Toast.makeText(MainActivity.this,"apikey验证失败，地图功能无法正常使用",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                //Toast.makeText(MainActivity.this,"apikey验证成功",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
               // Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        Bmob.initialize(this, appKey);
        BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {

                } else {
                    Log.e("error",e.getMessage());
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("runStep", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("objectId","asdf");
        String objectId = BmobUser.getCurrentUser(User.class).getObjectId();
        if (!id.equals(objectId)) {
            SharedPreferences myPreference = getSharedPreferences("runStep", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myPreference.edit();
            editor.putString("objectId", objectId);
            editor.putInt("step",0);
            editor.putInt("plan",10000);
            editor.commit();
        }
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        FirstFragment firstFragment = new FirstFragment();
        SecondFragment secondFragment = new SecondFragment();
        ThirdFragment thirdFragment = new ThirdFragment();
        FourthFragment fourthFragment = new FourthFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(firstFragment);
        list.add(secondFragment);
        list.add(thirdFragment);
        list.add(fourthFragment);
        viewPagerAdapter.setList(list);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            menuItem = item;
            switch (item.getItemId()) {
                case R.id.action_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.action_found:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.action_shop:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.action_person:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("tag", "onRequestPermissionsResult: ");
        switch (requestCode) {
            case 1:
                Log.d("tag", "onRequestPermissionsResult: switch");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, DynamicDemo.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,"没有定位权限！",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
