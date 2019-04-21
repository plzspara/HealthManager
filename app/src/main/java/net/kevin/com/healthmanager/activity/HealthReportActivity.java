package net.kevin.com.healthmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import java.text.DecimalFormat;

import cn.bmob.v3.BmobUser;

public class HealthReportActivity extends AppCompatActivity {

    private int stepCount = 0;
    private TextView show_length,show_cal,show_step,show_weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_report);

        show_cal = (TextView) findViewById(R.id.show_cal);
        show_weight = (TextView) findViewById(R.id.show_weight);
        show_step = (TextView) findViewById(R.id.show_step);
        show_length = (TextView) findViewById(R.id.show_length);

        User user = BmobUser.getCurrentUser(User.class);
        String weight = user.getWeight() + "";
        if (user.getStep() != null) {
            for (int i = 0 ; i< user.getStep().size(); i++ ) {
                stepCount+=Integer.parseInt(user.getStep().get(i));
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        show_step.setText("步数："+stepCount);
        show_length.setText("距离：" + df.format(stepCount * 0.6 / 1000) + "公里");
        show_cal.setText("热量：" + df.format(stepCount * 0.6 * 0.02) + "千卡");
        show_weight.setText("体重：" + weight + "kg");
    }
}
