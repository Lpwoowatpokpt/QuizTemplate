package com.lpwoowatpokpt.quiztemplate.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lpwoowatpokpt.quiztemplate.Adapters.PagerAdapter;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateModeActivity extends AppCompatActivity  {

    TinyDB tinyDB;

    ViewPager viewPager;
    TabLayout tabLayout;

    FloatingActionButton addQuestion;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(tinyDB.getString(Common.FONT))
                .setFontAttrId(R.attr.fontPath)
                .build());

        Utils.onActivityCreateSetTheme(this, tinyDB.getInt(Common.THEME_ID));
        setContentView(R.layout.activity_create_mode);

        viewPager = findViewById(R.id.viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        addQuestion = findViewById(R.id.addQuestionBtn);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestQuestion();
            }
        });
    }

    private void SuggestQuestion() {
        List <String> newQuestion = new ArrayList<>();

        if (tinyDB.getBoolean(Common.IS_IMAGE_QUESTION, true)){
            newQuestion.add(tinyDB.getString(Common.TEXT_IMAGE_QUESTION));
            newQuestion.add(tinyDB.getString(Common.IMAGE_PATH));
        }else
            newQuestion.add(tinyDB.getString(Common.TEXT_QUESTION));

        newQuestion.add(tinyDB.getString(Common.ANSWER_1));
        newQuestion.add(tinyDB.getString(Common.ANSWER_2));
        newQuestion.add(tinyDB.getString(Common.ANSWER_3));
        newQuestion.add(tinyDB.getString(Common.ANSWER_4));
        newQuestion.add(tinyDB.getString(Common.HINT));


       if (IsQuestionFilled(newQuestion)){
           Common.ShowToast(this,getString(R.string.success_question_add));
           Intent intent = new Intent(this, ConfirmQuetionActivity.class);
           startActivity(intent);
       }else{
           Common.ShowToast(this,getString(R.string.not_filled));
           Log.e("slut", String.valueOf(newQuestion.toString()));
       }


    }

    public boolean IsQuestionFilled(List<String> newQuestion){

        for (int i = 0; i < newQuestion.size(); i++){
            if (newQuestion.get(i).matches(""))
                return false;
        }
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.BackToMainMenu(this, FirebaseAuth.getInstance());
    }
}
