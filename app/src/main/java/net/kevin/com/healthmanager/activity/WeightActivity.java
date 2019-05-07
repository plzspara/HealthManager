package net.kevin.com.healthmanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;
import net.kevin.com.healthmanager.javaBean.User;

import java.math.BigDecimal;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 体重管理
 */
public class WeightActivity extends Activity implements View.OnClickListener {

    private EditText edit_weight,edit_height;
    private Button determine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        edit_weight = (EditText) findViewById(R.id.edit_weight);
        edit_height = (EditText) findViewById(R.id.edit_height);
        determine = (Button) findViewById(R.id.btn_determine);

        determine.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_determine:
                double userWeight = 0;
                String weight = edit_weight.getText().toString();
                String height = edit_height.getText().toString();
                if (BmobUser.getCurrentUser(User.class).getWeight()!=null) {
                    userWeight = Double.parseDouble(BmobUser.getCurrentUser(User.class).getWeight());
                }

                String info = "";
                if (!weight.isEmpty() && !height.isEmpty()) {
                    double d_weight = Double.parseDouble(weight);
                    double d_height = Double.parseDouble(height)/100;
                    if (userWeight > 0) {
                        if (userWeight > d_weight) {
                            info = ",与上次测量相比减少了" + (userWeight - d_weight) + "kg";
                        } else if (userWeight < d_weight) {
                            info = ",与上次测量相比增加了" + (d_weight - userWeight) + "kg";
                        } else {
                            info = ",与上次测量相比体重没有变化";
                        }
                    }
                    final double result = d_weight / d_height /d_height;
                    BigDecimal bigDecimal = new BigDecimal(result);
                    AlertDialog.Builder builder = new AlertDialog.Builder(WeightActivity.this);
                    builder.setTitle("成年人正常体重指数为18.5-22.9");
                    builder.setMessage("你的体重指数为" + bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + info);
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (result<22.9 && result>18.5){
                                Toast.makeText(WeightActivity.this,"体重正常，请保持",Toast.LENGTH_SHORT).show();
                            } else if (result>22.9){
                                Toast.makeText(WeightActivity.this,"体重偏重，请减肥",Toast.LENGTH_SHORT).show();
                            } else if (result<18.5) {
                                Toast.makeText(WeightActivity.this,"体重过轻，请增加饮食",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();

                    User user = new User();
                    user.setWeight(d_weight+"");
                    user.update(BmobUser.getCurrentUser(User.class).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {

                            } else {
                                Log.d("tag", "done: " + e.getMessage());
                            }
                        }
                    });
                    break;
                } else {
                    Toast.makeText(WeightActivity.this,"请输入",Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
