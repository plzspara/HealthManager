package net.kevin.com.healthmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import org.json.JSONException;
import org.json.JSONObject;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button button_login;
    private EditText edit_account, edit_password;
    private TextView text_goRegister, text_QQLogin;
    private Tencent mTencent;

    private final String AppID = "101530906";
    private final String appKey = "57b639f8a7a4b768d7e7add7329bcf34";
    private final String bing_url = "http://guolin.tech/api/bing_pic";

    private String snsType = "qq",accessToken, expiresIn,userId;
    private String openidString,url,nickName,gender;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean loginStatus;
        SP = getSharedPreferences("User",0);
        loginStatus = SP.getBoolean("LoginStatus",false);
        /*if (loginStatus) {
            goMainActivity();
        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTencent = Tencent.createInstance(AppID,getApplicationContext());
        Bmob.initialize(this, appKey);

        button_login = (Button) findViewById(R.id.btn_submit);
        text_goRegister = (TextView) findViewById(R.id.txt_go_register);
        edit_account = (EditText) findViewById(R.id.edit_phone);
        edit_password = (EditText) findViewById(R.id.edit_password);
        text_QQLogin = (TextView) findViewById(R.id.text_QQLogin);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_account.getText().toString().equals("") || edit_password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "帐号密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String phone = edit_account.getText().toString();
                    String password = edit_password.getText().toString();
                    BmobUser.loginByAccount(phone, password, new LogInListener<User>() {

                        @Override
                        public void done(User user, BmobException e) {
                            if (user != null) {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                SP = getSharedPreferences("User",0);
                                SharedPreferences.Editor editor = SP.edit();
                                editor.putBoolean("LoginStatus",true);
                                editor.commit();
                                goMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "loginByAccount.done: "+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        text_goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        text_QQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTencent.login(LoginActivity.this,"all",new BaseUiListener());
            }
        });

    }


    /**
     * 跳转到主活动
     */
    private void goMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());

        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }

    }

    private class BaseUiListener implements IUiListener {
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            try {
                openidString = ((JSONObject) response).getString("openid");
                accessToken =  ((JSONObject) response).getString("access_token");
                expiresIn = ((JSONObject) response).getString("expires_in");
                Log.d(TAG, "onComplete: "  + snsType + "-" + accessToken + "-" + expiresIn + "-" + openidString);
                SP = getSharedPreferences("User",0);
                SharedPreferences.Editor editor = SP.edit();
                editor.putString("openid",openidString);
                editor.putString("accessToken",accessToken);
                editor.putString("expireIn",expiresIn);
                editor.commit();
                mTencent.setOpenId(openidString);
                mTencent.setAccessToken(openidString,expiresIn);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    try {
                        Log.d(TAG, "onComplete: " + o.toString());
                        url = ((JSONObject) o).getString("figureurl_qq");
                        nickName = ((JSONObject) o).getString("nickname");
                        gender = ((JSONObject) o).getString("gender");
                        SP = getSharedPreferences("User",0);
                        SharedPreferences.Editor editor = SP.edit();
                        editor.putString("url",url);
                        editor.putString("nickName",nickName);
                        editor.putString("gender",gender);
                        editor.commit();
                        Log.d(TAG, "onComplete: ");
                        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(snsType,accessToken, expiresIn,openidString);
                        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

                            @Override
                            public void done(JSONObject userAuth,BmobException e) {
                                Log.d(TAG, "done: ");
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                SP = getSharedPreferences("User",0);
                                SharedPreferences.Editor editor = SP.edit();
                                editor.putBoolean("LoginStatus",true);
                                editor.commit();
                                goMainActivity();
                            }
                        });

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, "onComplete: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.v("UserInfo","onError");
                }

                @Override
                public void onCancel() {
                    Log.v("UserInfo","onCancel");
                }
            });


        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
        }


    }


}
