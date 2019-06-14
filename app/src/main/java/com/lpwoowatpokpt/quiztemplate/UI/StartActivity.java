package com.lpwoowatpokpt.quiztemplate.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.Question;
import com.lpwoowatpokpt.quiztemplate.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StartActivity extends AppCompatActivity {

    TinyDB tinyDB;

    Button btnPlay;

    SeekBar seekBar;
    TextView txtMode, category_name;

    DatabaseReference questions, categories;

    KenBurnsView categoryBG;

    String name;

    private static final String ALL_CATEGORIES = "00";


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
        setContentView(R.layout.activity_start);


        categories = Common.getDatabase().getReference(Common.CATEGORY);
        questions = Common.getDatabase().getReference(Common.QUESTIONS);

        categories.keepSynced(true);
        questions.keepSynced(true);

        LoadQuestions(Common.categoryId);

        categoryBG = findViewById(R.id.image_bg);
        Query category_image_path = categories.orderByChild("name").equalTo(Common.categoryName);
        category_image_path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot image_path: dataSnapshot.getChildren())
                {
                    name = (String)image_path.child("name").getValue();
                    String path = (String)image_path.child("image").getValue();
                    Picasso.get().load(path).into(categoryBG);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(1);

        txtMode = findViewById(R.id.txt_mode);
        txtMode.setText(Common.MEDIUM);

        category_name = findViewById(R.id.txt_header);


        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            String category = extra.getString("name");
            category_name.setText(category);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 0:
                        txtMode.setText(Common.EASY);
                        break;
                    case 1:
                        txtMode.setText(Common.MEDIUM);
                        break;
                    case 2:
                        txtMode.setText(Common.HARD);
                        break;
                    case 3:
                        txtMode.setText(Common.SUPERHARD);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, PlayingActivity.class);
                intent.putExtra("MODE", getPlayMode());
                intent.putExtra("NAME", name);
                startActivity(intent);
                finish();
            }
        });
    }


    private String getPlayMode() {
        if (seekBar.getProgress()==0)
            return Common.EASY;
        else if (seekBar.getProgress()==1)
            return Common.MEDIUM;
        else if (seekBar.getProgress()==2)
            return Common.HARD;
        else
            return Common.SUPERHARD;
    }

    private void LoadQuestions(String categoryId) {
        if(Common.questionsList.size()>0)
            Common.questionsList.clear();

        if (categoryId.equals(ALL_CATEGORIES))
            LoadAllQuestions();
        else {
            questions.orderByChild(Common.CATEGORY_ID).equalTo(categoryId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                            {
                                Question ques = postSnapshot.getValue(Question.class);
                                Common.questionsList.add(ques);
                                Collections.shuffle(Common.questionsList);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void LoadAllQuestions() {
        if(Common.questionsList.size()>0)
            Common.questionsList.clear();

        questions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Question ques = postSnapshot.getValue(Question.class);
                    Common.questionsList.add(ques);
                    Collections.shuffle(Common.questionsList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
