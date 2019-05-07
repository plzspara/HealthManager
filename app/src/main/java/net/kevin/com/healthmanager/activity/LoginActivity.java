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
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 手机密码登录，QQ登录
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button button_login;
    private EditText edit_account, edit_password;
    private TextView text_goRegister, text_QQLogin;
    private Tencent mTencent;

    private final String AppID = "101552135";
    private final String appKey = "57b639f8a7a4b768d7e7add7329bcf34";

    private String snsType = "qq";
    private String openidString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mTencent = Tencent.createInstance(AppID, getApplicationContext());
        Bmob.initialize(this, appKey);
        if (BmobUser.isLogin()) {
            goMainActivity();
        }
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
                                goMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "loginByAccount.done: " + e.getMessage());
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
                mTencent.login(LoginActivity.this, "all", new BaseUiListener());
            }
        });

    }


    /**
     * 跳转到主活动
     */
    private void goMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());

        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }

    }

    private class BaseUiListener implements IUiListener {
        String userName,birthday,gender,headImage;
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();


            /*
             * 下面隐藏的是用户登录成功后 登录用户数据的获取的方法
             * 共分为两种  一种是简单的信息的获取,另一种是通过UserInfo类获取用户较为详细的信息
             *有需要看看
             * */
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------" + response.toString());
                openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);

                mTencent.setAccessToken(((JSONObject) response).getString("access_token"), ((JSONObject) response).getString("expires_in"));

                Log.v("TAG", "-------------" + openidString);
                //access_token= ((JSONObject) response).getString("access_token");              //expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像什么的，这个时候怎么办？
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？  */

            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
            Log.d(TAG, "onComplete: " + qqToken);
            //    info.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {

                    //用户信息获取到了
                    //Toast.makeText(getApplicationContext(), ((JSONObject) o).getString("nickname")+((JSONObject) o).getString("gender") , Toast.LENGTH_SHORT).show();
                    try {
                        userName = ((JSONObject) o).getString("nickname");
                        headImage = ((JSONObject) o).getString("figureurl_2");
                        birthday = ((JSONObject) o).getString("year");
                        gender =   ((JSONObject) o).getString("gender");
                        if (BmobUser.getCurrentUser(User.class).getHeadImage() == null && BmobUser.isLogin()) {
                            Log.d(TAG, "isfirst: true");
                            registerUserInfo(userName,gender,headImage,birthday);
                        } else if (BmobUser.isLogin()){
                            Log.d(TAG, "isfirst: false");

                            goMainActivity();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.v("UserInfo", "onError");
                }

                @Override
                public void onCancel() {
                    Log.v("UserInfo", "onCancel");
                }
            });


            BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(snsType, mTencent.getAccessToken(), mTencent.getExpiresIn()+"", openidString);
            BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
                @Override
                public void done(JSONObject user, BmobException e) {
                    if (e == null) {

                    } else {
                        Log.e("BMOB", e.toString());
                    }
                }
            });

        }

        public void registerUserInfo(String username,String gender,String headImage,String birthday){
            String objectId = BmobUser.getCurrentUser(User.class).getObjectId();
            User user = new User();
            Log.d(TAG, "registerUserInfo: " + username + "  " + gender + "   "+ headImage);
            user.setUsername(username);
            user.setGender(gender);
            user.setYear(birthday);
            user.setHeadImage(headImage);
            user.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.d(TAG, "done: 更新成功");
                        goMainActivity();
                    } else {
                        Log.d(TAG, "done: 更新失败" + e.getMessage());
                    }
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
