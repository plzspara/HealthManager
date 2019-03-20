package net.kevin.com.healthmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText edit_phone, edit_verifyCode, edit_password;
    private Button btn_submit, btn_get;
    private TextView text_goLogin;

    private String phone;

    //注册状态码 注册成功后为true
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bmob.initialize(this, "57b639f8a7a4b768d7e7add7329bcf34");

        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_verifyCode = (EditText) findViewById(R.id.edit_vertify_code);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_get = (Button) findViewById(R.id.btn_get);
        text_goLogin = (TextView) findViewById(R.id.txt_go_login);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = edit_phone.getText().toString();
                String code = edit_verifyCode.getText().toString();
                String password = edit_password.getText().toString();
                if (code.equals("") || password.equals("")) {
                    Toast.makeText(RegisterActivity.this, "验证码密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    code = edit_verifyCode.getText().toString();
                    verifySmsCode(code, password);
                }

            }
        });

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = edit_phone.getText().toString();
                if (phone.equals("") || phone.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                } else {
                    timer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long l) {
                            btn_get.setClickable(false);
                            btn_get.setText(l / 1000 + "秒");
                        }

                        @Override
                        public void onFinish() {
                            btn_get.setText("获取");
                            btn_get.setClickable(true);
                        }
                    };
                    timer.start();
                    BmobSMS.requestSMSCode(phone, "健康管家", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer smsId, BmobException e) {
                            if (e == null) {
                                Toast.makeText(RegisterActivity.this, "发送短信成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "发送短信失败", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "btnget.done: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        text_goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 验证 验证码是否正确并注册 如果正确把注册状态码设为true
     *
     * @param code 短信验证码
     */
    private void verifySmsCode(String code, final String password) {

        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = new User();
                    user.setMobilePhoneNumber(phone);
                    user.setMobilePhoneNumberVerified(true);
                    user.setPassword(password);
                    user.setUsername("default");
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e == null) {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "user.update.done: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "verifySmsCode.done: " + e.getMessage() + e.getErrorCode());
                }
            }
        });

    }


}
