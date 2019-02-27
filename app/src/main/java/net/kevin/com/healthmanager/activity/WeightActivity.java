package net.kevin.com.healthmanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.kevin.com.healthmanager.R;

import java.math.BigDecimal;

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
                String weight = edit_weight.getText().toString();
                String height = edit_height.getText().toString();
                if (!weight.isEmpty() && !height.isEmpty()) {
                    double d_weight = Double.parseDouble(weight);
                    double d_height = Double.parseDouble(height);
                    final double result = d_weight / d_height /d_height;
                    BigDecimal bigDecimal = new BigDecimal(result);
                    AlertDialog.Builder builder = new AlertDialog.Builder(WeightActivity.this);
                    builder.setTitle("成人正常体脂率为18.5-22.9");
                    builder.setMessage("" + bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
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
