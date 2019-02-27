package net.kevin.com.healthmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class PasswordActivity extends AppCompatActivity {

    EditText edit_inputpass,edit_confirmpass;
    Button button_finalregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Bmob.initialize(this, "57b639f8a7a4b768d7e7add7329bcf34");
        edit_inputpass = (EditText) findViewById(R.id.edit_inputpass);
        edit_confirmpass = (EditText) findViewById(R.id.edit_confirmpass);
        button_finalregister = (Button) findViewById(R.id.button_finalregister);
        button_finalregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = edit_inputpass.getText().toString();
                String confirmpass = edit_confirmpass.getText().toString();
                if (password.equals(confirmpass)) {
                    User user = BmobUser.getCurrentUser(User.class);
                    user.setPassword(password);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),"密码出错"+ e.getErrorCode() + "-" +e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
