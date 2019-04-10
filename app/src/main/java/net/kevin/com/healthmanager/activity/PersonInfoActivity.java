package net.kevin.com.healthmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.kevin.com.healthmanager.R;

public class PersonInfoActivity extends AppCompatActivity {

    private TextView head,nick,password,birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        initView();
    }

    private void initView() {
    head = (TextView) findViewById(R.id.tv_head);
    nick = (TextView) findViewById(R.id.tv_nick);
    password = (TextView) findViewById(R.id.tv_password);
    birth = (TextView) findViewById(R.id.birth);


    birth.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PersonInfoActivity.this,UpdateBirthdayActivity.class);
            startActivity(intent);
        }
    });

    nick.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PersonInfoActivity.this,UpdateNickNameActivity.class);
            startActivity(intent);
        }
    });

    password.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PersonInfoActivity.this,UpdatePasswordActivity.class);
            startActivity(intent);
        }
    });

    head.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PersonInfoActivity.this,ChooseActivity.class);
            startActivity(intent);
        }
    });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
