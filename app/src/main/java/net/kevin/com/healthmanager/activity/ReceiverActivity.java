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

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 收货人信息修改
 */
public class ReceiverActivity extends AppCompatActivity {


    EditText edit_name,edit_phone,edit_address;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        edit_name = (EditText) findViewById(R.id.edit_receiver_name);
        edit_phone = (EditText) findViewById(R.id.edit_receiver_phone);
        edit_address = (EditText) findViewById(R.id.edit_receiver_address);
        btn_save = (Button) findViewById(R.id.btn_receiver_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString();
                String phone = edit_phone.getText().toString();
                String address = edit_address.getText().toString();
                if (name.equals("") && phone.equals("") && address.equals("")) {
                    Toast.makeText(ReceiverActivity.this,"输入的收货人信息不能都为空",Toast.LENGTH_SHORT).show();
                }else {
                    User user = BmobUser.getCurrentUser(User.class);
                    if (user.getAddress() == null && (name.equals("") || phone.equals("") || address.equals(""))) {
                        Toast.makeText(ReceiverActivity.this,"初次输入收货人信息，请全部输入",Toast.LENGTH_SHORT).show();
                    } else {
                        if (!phone.equals("") && phone.length()!=11) {
                            Toast.makeText(ReceiverActivity.this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                        } else {
                            update(name,address,phone);
                        }
                    }
                }



            }
        });
    }

    private void update(String name, String address,String phone) {
        User user = new User();
        if (!name.equals("")) {
            user.setName(name);
        }

        if (!phone.equals("")) {
            user.setMobilePhoneNumber(phone);
        }

        if (!address.equals("")) {
            user.setAddress(address);
        }

        user.update(BmobUser.getCurrentUser(User.class).getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(ReceiverActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ERROR", "done: " + e.getMessage() + e.getErrorCode() );
                    if (e.getErrorCode() == 209) {
                        Toast.makeText(ReceiverActivity.this,"此手机号已被注册",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
