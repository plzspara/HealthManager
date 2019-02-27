package net.kevin.com.healthmanager.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.step.StepArcView;
import net.kevin.com.healthmanager.step.UpdateUiCallBack;
import net.kevin.com.healthmanager.step.service.StepService;
import net.kevin.com.healthmanager.step.utils.SharedPreferencesUtils;

import java.text.DecimalFormat;

public class StepActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "StepActivity";
    private TextView tv_data;
    private StepArcView cc;
    private TextView tv_set;
    private TextView tv_isSupport;
    private TextView tv_runLength, tv_calorie;
    private SharedPreferencesUtils sp;
    private int currentStep, firstStep;

    private void assignViews() {
        tv_data = (TextView) findViewById(R.id.tv_data);
        cc = (StepArcView) findViewById(R.id.cc);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_isSupport = (TextView) findViewById(R.id.tv_isSupport);
        tv_calorie = (TextView) findViewById(R.id.text_calorie);
        tv_runLength = (TextView) findViewById(R.id.text_runLength);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        assignViews();
        initData();
        addListener();
    }


    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认10000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "10000");
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        setupService();
    }


    private boolean isBind = false;

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }


    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "10000");
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());
            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    updateUI(stepCount);
                    currentStep = stepCount;
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 更新运动数据
     *
     * @param stepCount 当前运动步数
     */
    private void updateUI(int stepCount) {
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "10000");
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
        DecimalFormat df = new DecimalFormat("0.00");
        tv_runLength.setText("距离：" + df.format(stepCount * 0.6 / 1000) + "公里");
        tv_calorie.setText("热量：" + df.format(stepCount * 0.6 * 0.02) + "千卡");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, SetPlanActivity.class));
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        firstStep = (int) sp.getParam("currentStep", 0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sp.setParam("currentStep", currentStep);
        sp.setParam("runStep",currentStep - firstStep);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
        //DbUtils.closeDb();
    }
}
