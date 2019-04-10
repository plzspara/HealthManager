package net.kevin.com.healthmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateBirthdayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_birthday);
        final EditText editText = (EditText) findViewById(R.id.et_birthday);
        Button button = (Button) findViewById(R.id.bt_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birthday = editText.getText().toString();
                if (birthday.equals("")) {
                    Toast.makeText(UpdateBirthdayActivity.this,"生日不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    String objectId = BmobUser.getCurrentUser(User.class).getObjectId();
                    User user = new User();
                    user.setYear(birthday);
                    user.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(UpdateBirthdayActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpdateBirthdayActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                                Log.d("tag", "done: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
