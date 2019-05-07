package net.kevin.com.healthmanager.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;
import net.kevin.com.healthmanager.step.accelerometer.StepCount;
import net.kevin.com.healthmanager.step.accelerometer.StepValuePassListener;
import net.kevin.com.healthmanager.step.bean.StepData;
import net.kevin.com.healthmanager.step.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 实时动态画运动轨迹，显示步数
 */
public class DynamicDemo extends Activity implements SensorEventListener {
    private static final String TAG = "DynamicDemo";
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;

    MapView mMapView;
    BaiduMap mBaiduMap;

    private TextView info;
    private RelativeLayout progressBarRl;

    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    float mCurrentZoom = 18f;//默认地图缩放比例值
    int thisStepCount = 0;//本次运动的步数

    private SensorManager mSensorManager;

    /**
     * 默认为30秒进行一次存储
     */
    private static int duration = 30 * 1000;
    /**
     * 当前的日期
     */
    private static String CURRENT_DATE = "";
    /**
     * 传感器管理对象
     */
    private SensorManager sensorManager;
    /**
     * 广播接受者
     */
    private BroadcastReceiver mBatInfoReceiver;
    /**
     * 保存记步计时器
     */
    private TimeCount time;
    /**
     * 今日所走的步数
     */
    private int CURRENT_STEP = 0;
    /**
     * 计步传感器类型  Sensor.TYPE_STEP_COUNTER或者Sensor.TYPE_STEP_DETECTOR
     */
    private static int stepSensorType = -1;
    /**
     * 每次第一次启动记步服务时是否从系统中获取了已有的步数记录
     */
    private boolean hasRecord = false;
    /**
     * 本次运动开始时传感器中的步数
     */
    private int hasStepCount = 0;
    /**
     * 上一秒的步数
     */
    private int previousStepCount = 0;

    /**
     * 加速度传感器中获取的步数
     */
    private StepCount mStepCount;


    private TextView textView;

    //起点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_startpoint);
    //终点图标
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_finishpoint);

    List<LatLng> points = new ArrayList<LatLng>();//位置点集合
    Polyline mPolyline;//运动轨迹图层
    LatLng last = new LatLng(0, 0);//上一个定位点
    MapStatus.Builder builder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dyn);

        initView();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        /**
         * 添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                mCurrentZoom = arg0.zoom;
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
                // TODO Auto-generated method stub

            }
        });

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//只用gps定位，需要在室外定位。
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);

        textView = (TextView) findViewById(R.id.text_step);
        textView.setText("0");
        initTodayData();
        initBroadcastReceiver();
        startTimeCount();

    }

    private void initView() {

        Button start = (Button) findViewById(R.id.buttonStart);
        Button finish = (Button) findViewById(R.id.buttonFinish);
        info = (TextView) findViewById(R.id.info);
        progressBarRl = (RelativeLayout) findViewById(R.id.progressBarRl);

        start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocClient != null && !mLocClient.isStarted()) {
                    mLocClient.start();
                    progressBarRl.setVisibility(View.VISIBLE);
                    info.setText("GPS信号搜索中，请稍后...");
                    mBaiduMap.clear();
                }
                startStepDetector();
            }
        });

        finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stepUpdate();
                SharedPreferences myPreference = getSharedPreferences("runStep", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPreference.edit();
                editor.putInt("step", CURRENT_STEP);
                editor.commit();
                if (mLocClient != null && mLocClient.isStarted()) {
                    mLocClient.stop();

                    progressBarRl.setVisibility(View.GONE);

                    if (isFirstLoc) {
                        points.clear();
                        last = new LatLng(0, 0);
                        return;
                    }

                    MarkerOptions oFinish = new MarkerOptions();// 地图标记覆盖物参数配置类
                    oFinish.position(points.get(points.size() - 1));
                    oFinish.icon(finishBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oFinish); // 在地图上添加此图层

                    //复位
                    points.clear();
                    last = new LatLng(0, 0);
                    isFirstLoc = true;

                }


            }
        });

    }

    double lastX;


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {

            if (location == null || mMapView == null) {
                return;
            }

            //注意这里只接受gps点，需要在室外定位。
            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                info.setText("GPS信号弱，请稍后...");

                if (isFirstLoc) {//首次定位
                    //第一个点很重要，决定了轨迹的效果，gps刚开始返回的一些点精度不高，尽量选一个精度相对较高的起始点
                    LatLng ll = null;

                    ll = getMostAccuracyLocation(location);
                    if (ll == null) {
                        return;
                    }
                    isFirstLoc = false;
                    points.add(ll);//加入集合
                    last = ll;

                    //显示当前定位点，缩放地图
                    locateAndZoom(location, ll);

                    //标记起点图层位置
                    MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
                    oStart.position(points.get(0));// 覆盖物位置点，第一个点为起点
                    oStart.icon(startBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oStart); // 在地图上添加此图层

                    progressBarRl.setVisibility(View.GONE);

                    return;//画轨迹最少得2个点，首地定位到这里就可以返回了
                }

                //从第二个点开始
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为5米才添加到集合中
                if (DistanceUtil.getDistance(last, ll) < 5) {
                    return;
                }

                points.add(ll);//如果要运动完成后画整个轨迹，位置点都在这个集合中

                last = ll;

                //显示当前定位点，缩放地图
                locateAndZoom(location, ll);

                //清除上一次轨迹，避免重叠绘画
                mMapView.getMap().clear();

                //起始点图层也会被清除，重新绘画
                MarkerOptions oStart = new MarkerOptions();
                oStart.position(points.get(0));
                oStart.icon(startBD);
                mBaiduMap.addOverlay(oStart);

                //将points集合中的点绘制轨迹线条图层，显示在地图上
                OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(points);
                mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
            }
        }

    }

    private void locateAndZoom(final BDLocation location, LatLng ll) {
        mCurrentLat = location.getLatitude();
        mCurrentLon = location.getLongitude();
        locData = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 首次定位很重要，选一个精度相对较高的起始点
     * 注意：如果一直显示gps信号弱，说明过滤的标准过高了，
     * 你可以将location.getRadius()>25中的过滤半径调大，比如>40，
     * 并且将连续5个点之间的距离DistanceUtil.getDistance(last, ll ) > 5也调大一点，比如>10，
     * 这里不是固定死的，你可以根据你的需求调整，如果你的轨迹刚开始效果不是很好，你可以将半径调小，两点之间距离也调小，
     * gps的精度半径一般是10-50米
     */
    private LatLng getMostAccuracyLocation(BDLocation location) {

        if (location.getRadius() > 40) {//gps位置精度大于40米的点直接弃用
            return null;
        }

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        if (DistanceUtil.getDistance(last, ll) > 10) {
            last = ll;
            points.clear();//有任意连续两点位置大于10，重新取点
            return null;
        }
        points.add(ll);
        last = ll;
        //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
        if (points.size() >= 5) {
            points.clear();
            return ll;
        }
        return null;
    }


    /**
     * 获取当天日期
     *
     * @return
     */
    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 初始化当天的步数
     */
    private void initTodayData() {
        CURRENT_DATE = getTodayDate();
        DbUtils.createDb(this, "DylanStepCount");
        DbUtils.getLiteOrm().setDebugged(false);
        //获取当天的数据，用于展示
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENT_DATE});
        if (list.size() == 0 || list.isEmpty()) {
            CURRENT_STEP = 0;
        } else if (list.size() == 1) {
            Log.v(TAG, "StepData=" + list.get(0).toString());
            CURRENT_STEP = Integer.parseInt(list.get(0).getStep());
        } else {
            Log.v(TAG, "出错了！");
        }
        if (mStepCount != null) {
            mStepCount.setSteps(CURRENT_STEP);
        }

        textView.setText(0 + "");
    }

    /**
     * 注册广播
     */
    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
//        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.d(TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.d(TAG, "screen off");
                    //改为60秒一存储
                    duration = 60000;
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.d(TAG, "screen unlock");
//                    save();
                    //改为30秒一存储
                    duration = 30000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.i(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                    //保存一次
                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    Log.i(TAG, " receive ACTION_SHUTDOWN");
                    save();
                } else if (Intent.ACTION_DATE_CHANGED.equals(action)) {//日期变化步数重置为0
//                    Logger.d("重置步数" + StepDcretor.CURRENT_STEP);
                    save();
                    isNewDay();
                } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {
                    //时间变化步数重置为0
                    isCall();
                    save();
                    isNewDay();
                } else if (Intent.ACTION_TIME_TICK.equals(action)) {//日期变化步数重置为0
                    isCall();
//                    Logger.d("重置步数" + StepDcretor.CURRENT_STEP);
                    save();
                    isNewDay();
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }


    /**
     * 监听晚上0点变化初始化数据
     */
    private void isNewDay() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())) || !CURRENT_DATE.equals(getTodayDate())) {
            initTodayData();
        }
    }


    /**
     * 监听时间变化提醒用户锻炼
     */
    private void isCall() {
        String time = this.getSharedPreferences("share_date", Context.MODE_MULTI_PROCESS).getString("achieveTime", "21:00");
        String plan = this.getSharedPreferences("share_date", Context.MODE_MULTI_PROCESS).getString("planWalk_QTY", "7000");
        String remind = this.getSharedPreferences("share_date", Context.MODE_MULTI_PROCESS).getString("remind", "1");
        if (("1".equals(remind)) &&
                (CURRENT_STEP < Integer.parseInt(plan)) &&
                (time.equals(new SimpleDateFormat("HH:mm").format(new Date())))
        ) {
            remindNotify();
        }

    }

    /**
     * 开始保存记步数据
     */
    private void startTimeCount() {
        if (time == null) {
            time = new TimeCount(duration, 1000);
        }
        time.start();
    }


    /**
     * 记步Notification的ID
     */
    int notifyId_Step = 100;
    /**
     * 提醒锻炼的Notification的ID
     */
    int notify_remind_id = 200;

    /**
     * 提醒锻炼通知栏
     */
    private void remindNotify() {

    }


    /**
     * 获取当前步数
     *
     * @return
     */
    public int getStepCount() {
        return CURRENT_STEP;
    }


    /**
     * 获取传感器实例
     */
    private void startStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addCountStepListener();
        } else {
            addBasePedometerListener();
        }
    }

    /**
     * 添加传感器监听
     * 1. TYPE_STEP_COUNTER API的解释说返回从开机被激活后统计的步数，当重启手机后该数据归零，
     * 该传感器是一个硬件传感器所以它是低功耗的。
     * 为了能持续的计步，请不要反注册事件，就算手机处于休眠状态它依然会计步。
     * 当激活的时候依然会上报步数。该sensor适合在长时间的计步需求。
     * <p>
     * 2.TYPE_STEP_DETECTOR翻译过来就是走路检测，
     * API文档也确实是这样说的，该sensor只用来监监测走步，每次返回数字1.0。
     * 如果需要长事件的计步请使用TYPE_STEP_COUNTER。
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_COUNTER;
            Log.v(TAG, "Sensor.TYPE_STEP_COUNTER");
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (detectorSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_DETECTOR;
            Log.v(TAG, "Sensor.TYPE_STEP_DETECTOR");
            sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.v(TAG, "Count sensor not available!");
            addBasePedometerListener();
        }
    }


    /**
     * 传感器监听回调
     * 记步的关键代码
     * 1. TYPE_STEP_COUNTER API的解释说返回从开机被激活后统计的步数，当重启手机后该数据归零，
     * 该传感器是一个硬件传感器所以它是低功耗的。
     * 为了能持续的计步，请不要反注册事件，就算手机处于休眠状态它依然会计步。
     * 当激活的时候依然会上报步数。该sensor适合在长时间的计步需求。
     * <p>
     * 2.TYPE_STEP_DETECTOR翻译过来就是走路检测，
     * API文档也确实是这样说的，该sensor只用来监监测走步，每次返回数字1.0。
     * 如果需要长事件的计步请使用TYPE_STEP_COUNTER。
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                //获取当前传感器返回的临时步数
                int tempStep = (int) event.values[0];
                //首次如果没有获取手机系统中已有的步数则获取一次系统中APP还未开始记步的步数

                if (!hasRecord) {
                    hasRecord = true;
                    hasStepCount = tempStep;
                } else {

                    //本次运动的步数，获取APP打开到现在的总步数=本次系统回调的总步数-APP打开之前已有的步数
                    thisStepCount = tempStep - hasStepCount;

                    //一秒内的步数，本次有效步数=（APP打开后所记录的总步数-上一次APP打开后所记录的总步数）
                    int thisStep = thisStepCount - previousStepCount;

                    //总步数=现有的步数+本次有效步数
                    CURRENT_STEP += (thisStep);
                    //记录最后一次APP打开到现在的总步数
                    previousStepCount = thisStepCount;
                }
                textView.setText(thisStepCount + "");
                break;

            case Sensor.TYPE_STEP_DETECTOR:
                if (event.values[0] == 1.0) {
                    CURRENT_STEP++;
                }
                break;

            case Sensor.TYPE_ORIENTATION:
                double x = event.values[SensorManager.DATA_X];

                if (Math.abs(x - lastX) > 1.0) {
                    mCurrentDirection = (int) x;

                    if (isFirstLoc) {
                        lastX = x;
                        return;
                    }

                    locData = new MyLocationData.Builder().accuracy(0)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
                    mBaiduMap.setMyLocationData(locData);
                }
                lastX = x;
                break;

            default:
                break;

        }


    }

    /**
     * 通过加速度传感器来记步
     */
    private void addBasePedometerListener() {
        mStepCount = new StepCount();
        mStepCount.setSteps(CURRENT_STEP);
        // 获得传感器的类型，这里获得的类型是加速度传感器
        // 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean isAvailable = sensorManager.registerListener(mStepCount.getStepDetector(), sensor,
                SensorManager.SENSOR_DELAY_UI);
        mStepCount.initListener(new StepValuePassListener() {
            @Override
            public void stepChanged(int steps) {
                CURRENT_STEP = steps;
                textView.setText(CURRENT_STEP + "");
            }
        });
        if (isAvailable) {
            Log.v(TAG, "加速度传感器可以使用");
        } else {
            Log.v(TAG, "加速度传感器无法使用");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * 保存记步数据
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            //save();
            startTimeCount();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

    }

    /**
     * 保存记步数据
     */
    private void save() {
        int tempStep = CURRENT_STEP;

        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENT_DATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setToday(CURRENT_DATE);
            data.setStep(tempStep + "");
            DbUtils.insert(data);
        } else if (list.size() == 1) {
            StepData data = list.get(0);
            data.setStep(tempStep + "");
            DbUtils.update(data);
        } else {
        }
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.unRegisterLocationListener(myListener);
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        startBD.recycle();
        finishBD.recycle();
        //更新服务器上的日期和步数
        unregisterReceiver(mBatInfoReceiver);
        super.onDestroy();
    }

    public void stepUpdate() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String time = sdf.format(date);
                    if (user.getStepDate() != null) {
                        if (user.getStepDate().get(user.getStepDate().size() - 1).equals(time)) {
                            update(user.getObjectId(), user.getStepDate().size() - 1);
                        } else {
                            newUpdate(user.getObjectId(), time);
                        }
                    } else {
                        User newUser = new User();

                        newUser.add("stepDate", time);
                        newUser.add("step", CURRENT_STEP + "");
                        newUser.update(BmobUser.getCurrentUser(User.class).getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.i("bmob", "更新成功");
                                } else {
                                    Log.i("bmob", "更新失败：" + e.getMessage());
                                }
                            }
                        });
                    }

                } else {
                    Log.d(TAG, "查询失败" + e.getMessage());
                }
            }
        });
    }

    public void update(String objectId, int position) {
        User user = new User();
        user.setValue("step." + position, CURRENT_STEP + "");
        user.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "成功");
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }
        });
    }

    public void newUpdate(String objectId, String time) {
        User user = new User();
        user.add("step", CURRENT_STEP + "");
        user.add("stepDate", time);
        user.update(objectId,new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "更新成功");
                } else {
                    Log.i("bmob", "更新失败：" + e.getMessage());
                }
            }
        });
    }



}
