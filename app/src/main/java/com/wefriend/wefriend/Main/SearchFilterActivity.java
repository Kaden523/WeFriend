package com.wefriend.wefriend.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wefriend.wefriend.R;

public class SearchFilterActivity extends AppCompatActivity {
    RadioGroup settingRadioGroup;
    RadioButton button1, button2,button3,button4,button5;
    Button changeBtn;
    ImageButton backBtn;
    private String preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);
        preference = "";
        settingRadioGroup = (RadioGroup) findViewById(R.id.search_setting_radiogroup);
        button1 = (RadioButton) findViewById(R.id.check_1);
        button2 = (RadioButton) findViewById(R.id.check_2);
        button3 = (RadioButton) findViewById(R.id.check_3);
        button4 = (RadioButton) findViewById(R.id.check_4);
        button5 = (RadioButton) findViewById(R.id.check_5);
        changeBtn = (Button) findViewById(R.id.change_search_setting);
        backBtn = (ImageButton) findViewById(R.id.back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        settingRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (button1.isChecked()){
                    preference = "0";
                }
                else if (button2.isChecked()){
                    preference = "1";
                }
                else if (button3.isChecked()){
                    preference = "2";
                }
                else if (button4.isChecked()){
                    preference = "3";
                }
                else if (button5.isChecked()){
                    preference = "4";
                }
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchFilterActivity.this, BufferActivity.class);
                BufferActivity.preference = getPreference();
                startActivity(intent);
            }
        });
    }
    public String getPreference(){
        return preference;
    }
}