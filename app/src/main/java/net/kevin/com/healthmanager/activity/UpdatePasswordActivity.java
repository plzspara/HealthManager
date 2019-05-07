package net.kevin.com.healthmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class UpdatePasswordActivity extends AppCompatActivity {

    EditText et_old,et_new,et_confirm;
    Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        et_old = (EditText) findViewById(R.id.et_oldpass);
        et_new = (EditText) findViewById(R.id.et_newpass);
        et_confirm = (EditText) findViewById(R.id.et_confirmpass);
        bt_submit = (Button) findViewById(R.id.submit);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPass = et_old.getText().toString();
                String newPass = et_new.getText().toString();
                String confirm = et_confirm.getText().toString();

                if (oldPass.equals("")) {
                    Toast.makeText(UpdatePasswordActivity.this,"原密码不能为空",Toast.LENGTH_SHORT).show();
                }
                if (newPass.equals("")) {
                    Toast.makeText(UpdatePasswordActivity.this,"新密码不能为空",Toast.LENGTH_SHORT).show();
                }

                if (confirm.equals("")) {
                    Toast.makeText(UpdatePasswordActivity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
                }

                if (!newPass.equals(confirm)) {
                    Toast.makeText(UpdatePasswordActivity.this,"两次密码不相同",Toast.LENGTH_SHORT).show();
                }

                if (!oldPass.equals("") && !newPass.equals("") && !confirm.equals("") && newPass.equals(confirm) ) {
                    BmobUser.updateCurrentUserPassword(oldPass, newPass, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(UpdatePasswordActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpdatePasswordActivity.this,"修改密码失败",Toast.LENGTH_SHORT).show();
                                Log.d("tag", "done: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }
}
