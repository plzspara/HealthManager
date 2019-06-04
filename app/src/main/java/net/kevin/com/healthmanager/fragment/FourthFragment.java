package net.kevin.com.healthmanager.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.activity.HealthReportActivity;
import net.kevin.com.healthmanager.activity.LoginActivity;
import net.kevin.com.healthmanager.activity.PersonInfoActivity;
import net.kevin.com.healthmanager.activity.SetPlanActivity;
import net.kevin.com.healthmanager.activity.StepHistoryActivity;
import net.kevin.com.healthmanager.javaBean.User;

import java.io.IOException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 个人
 */
public class FourthFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "FourthFragment";
    private int SUCCESS = 1;
    private Handler handler;
    private CircleImageView circleImageView;

    private TextView personInfo, stepPlan, stepHistory, logout, healthReport,nickName;
    private TextView tv_gender;

    public FourthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        final String objectId = BmobUser.getCurrentUser(User.class).getObjectId();
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(objectId, new QueryListener<User>() {
            @Override
            public void done(User object,BmobException e) {
                if(e==null){
                    if (object.getUsername() != null) {
                        updateNickName(object.getUsername(),object.getGender(),object.getYear());
                    }
                    if (object.getHeadImage() != null)  {
                        getImage(object.getHeadImage());
                    }
                }else{
                    Log.d(TAG, "done: " + e.getMessage());
                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        byte[] Picture = (byte[]) msg.obj;
                        //使用BitmapFactory工厂，把字节数组转化为bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                        //通过imageview，设置图片
                        circleImageView.setImageBitmap(bitmap);
                        break;
                    default:
                        break;

                }
            }
        };

    }

    private void updateNickName(String name,String gender,String birth) {
        nickName.setText(name);
        if (gender != null) {
            if (gender.equals("男")) {
                tv_gender.setText("♂");
                tv_gender.setTextColor(getResources().getColor(R.color.blue_a700));
            } else {
                tv_gender.setText("♀");
                tv_gender.setTextColor(getResources().getColor(R.color.pink_a200));
            }
        }
    }

    private void getImage(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] Picture_bt = response.body().bytes();
                Log.d(TAG, "onResponse: " + Picture_bt);
                //通过handler更新UI
                Message message = handler.obtainMessage();
                message.obj = Picture_bt;
                message.what = SUCCESS;
                handler.sendMessage(message);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fourth, container, false);
        circleImageView = (CircleImageView) view.findViewById(R.id.head_image);
        personInfo = (TextView) view.findViewById(R.id.personinfo);
        stepPlan = (TextView) view.findViewById(R.id.step_plan);
        stepHistory = (TextView) view.findViewById(R.id.history);
        logout = (TextView) view.findViewById(R.id.logout);
        healthReport = (TextView) view.findViewById(R.id.health_report);
        nickName = (TextView) view.findViewById(R.id.nickname);
        tv_gender = (TextView) view.findViewById(R.id.tv_gender);

        personInfo.setOnClickListener(this);
        stepHistory.setOnClickListener(this);
        stepPlan.setOnClickListener(this);
        logout.setOnClickListener(this);
        healthReport.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personinfo:
                Intent intent1 = new Intent(getActivity(), PersonInfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.step_plan:
                Intent intent3 = new Intent(getActivity(), SetPlanActivity.class);
                startActivity(intent3);
                break;
            case R.id.history:
                BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
                    @Override
                    public void done(BmobUser user, BmobException e) {
                        if (e == null) {

                        } else {
                            Log.e("error",e.getMessage());
                        }
                    }
                });
                User user = BmobUser.getCurrentUser(User.class);
                if (user.getStep() != null) {
                    Intent intent4 = new Intent(getActivity(), StepHistoryActivity.class);
                    startActivity(intent4);
                } else {
                    Toast.makeText(getActivity(),"没有运动记录",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.logout:
                BmobUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.health_report:
                Intent intent2 = new Intent(getActivity(), HealthReportActivity.class);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }
}
