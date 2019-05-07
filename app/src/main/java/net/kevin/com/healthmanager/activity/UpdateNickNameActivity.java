package net.kevin.com.healthmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户昵称和性别更新
 */
public class UpdateNickNameActivity extends AppCompatActivity {


    private RadioGroup radioGroup_gender;
    private String gender = "男";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick_name);

        final EditText editText = (EditText) findViewById(R.id.et_nickname);
        Button button = (Button) findViewById(R.id.update);
        radioGroup_gender = (RadioGroup) findViewById(R.id.radioGroup_gender);
        radioGroup_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton_checked= (RadioButton) group.findViewById(checkedId);
                switch (checkedId){
                    case R.id.radioButton_male:
                        gender = "男";
                        break;
                    case R.id.radioButton_female:
                        gender = "女";
                        break;
                    default:
                        break;
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = editText.getText().toString();
                if (nickName.equals("")) {
                    Toast.makeText(UpdateNickNameActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    String objectId = BmobUser.getCurrentUser(User.class).getObjectId();
                    User user = new User();
                    user.setUsername(nickName);
                    user.setGender(gender);
                    user.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                                 if (e == null) {
                                     Toast.makeText(UpdateNickNameActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(UpdateNickNameActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
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
