package net.kevin.com.healthmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarEntry;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;

/**
 * 健康报告
 */
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
            int count = 0;
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentTime = sdf.format(date);
            String day = currentTime.substring(8,10);

            if (day.substring(0,1).equals("0")) {
                count = Integer.parseInt(day.substring(1,2));
            } else {
                count = Integer.parseInt(day);
            }

            float start = 1f;

            for (int i = (int) start; i < start + count; i++) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - count + i);// 让日期减去5
                Date endDate = cal.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String d = simpleDateFormat.format(endDate);
                int k = user.getStepDate().size();
                for (int j = 0; j < count;j++) {
                    if ( k-1-j>=0 && user.getStepDate().get(k-1-j).equals(d)) {
                        int y = Integer.parseInt(user.getStep().get(k-1-j));
                        stepCount += y;
                        break;
                    }
                }
            }
        }



        DecimalFormat df = new DecimalFormat("0.00");
        show_step.setText("步数："+stepCount);
        show_length.setText("距离：" + df.format(stepCount * 0.75 / 1000) + "公里");
        show_cal.setText("热量：" + df.format(stepCount * 0.056) + "千卡");
        show_weight.setText("体重：" + weight + "kg");
    }
}
