package net.kevin.com.healthmanager.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.fragment.MonthFragment;
import net.kevin.com.healthmanager.fragment.WeekFragment;

/**
 * 用户运动周和月历史
 */
public class StepHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);
        final TextView zhou = (TextView) findViewById(R.id.zhou);
        final TextView yue = (TextView) findViewById(R.id.yue);
        final WeekFragment blankFragment = new WeekFragment();
        final MonthFragment blankFragment2 = new MonthFragment();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager1.beginTransaction();
        fragmentTransaction.add(R.id.container4,blankFragment);
        fragmentTransaction.commit();
        zhou.setClickable(false);
        zhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction =fragmentManager.beginTransaction();
                transaction.replace(R.id.container4,blankFragment);
                transaction.commit();
                yue.setClickable(true);
                zhou.setClickable(false);
                zhou.setTextColor(getResources().getColor(R.color.orangered));
                yue.setTextColor(getResources().getColor(R.color.black));
            }
        });

        yue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction =fragmentManager.beginTransaction();
                transaction.replace(R.id.container4,blankFragment2);
                transaction.commit();
                yue.setClickable(false);
                zhou.setClickable(true);
                zhou.setTextColor(getResources().getColor(R.color.black));
                yue.setTextColor(getResources().getColor(R.color.orangered));
            }
        });

    }
}
