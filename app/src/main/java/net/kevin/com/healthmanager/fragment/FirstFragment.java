package net.kevin.com.healthmanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.tauth.Tencent;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.activity.DynamicDemo;
import net.kevin.com.healthmanager.activity.HistoryActivity;
import net.kevin.com.healthmanager.activity.LoginActivity;
import net.kevin.com.healthmanager.activity.StepActivity;
import net.kevin.com.healthmanager.activity.WeightActivity;
import net.kevin.com.healthmanager.step.StepArcView;
import net.kevin.com.healthmanager.step.utils.SharedPreferencesUtils;

import java.io.IOException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {


    private StepArcView stepArcView;
    private SharedPreferencesUtils sp;
    private Button btn_start,btn_weight,btn_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        stepArcView = (StepArcView) view.findViewById(R.id.step);
        btn_start = (Button) view.findViewById(R.id.start_running);
        btn_weight = (Button) view.findViewById(R.id.weight);
        btn_logout = (Button) view.findViewById(R.id.logout);

        stepArcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DynamicDemo.class);
                startActivity(intent);
            }
        });

        btn_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WeightActivity.class);
                startActivity(intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        sp = new SharedPreferencesUtils(getContext());
        int step = (int) sp.getParam("runStep", 0);
        stepArcView.setCurrentCount(10000, step);
    }

}
