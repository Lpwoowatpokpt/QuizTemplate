package com.lpwoowatpokpt.quiztemplate.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Interface.RankingCallBack;
import com.lpwoowatpokpt.quiztemplate.Model.QuestionScore;
import com.lpwoowatpokpt.quiztemplate.Model.Ranking;
import com.lpwoowatpokpt.quiztemplate.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DoneActivity extends AppCompatActivity {

    TinyDB tinyDB;

    private static final String MATCH = "match : %d / %d";

    Button tryAgainBtn;
    TextView txtResult, getTxtResult;
    ProgressBar progressBar;

    DatabaseReference questionScore, rankingTbl;

    int sum = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(tinyDB.getString(Common.FONT))
                .setFontAttrId(R.attr.fontPath)
                .build());

        Utils.onActivityCreateSetTheme(this, tinyDB.getInt(Common.THEME_ID));
        setContentView(R.layout.activity_done);

        questionScore = Common.getDatabase().getReference(Common.QUESTION_SCORE);
        rankingTbl = Common.getDatabase().getReference(Common.RANKING);

        txtResult = findViewById(R.id.txtTotalScore);
        getTxtResult = findViewById(R.id.txtPassed);

        progressBar = findViewById(R.id.doneProgressBar);
        tryAgainBtn = findViewById(R.id.btnPlay);

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoneActivity.this , HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            final int score = extra.getInt("SCORE");
            String timestamp = extra.getString("TIMESTAMP");
            String mode = extra.getString("MODE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");

            txtResult.setText(String.format("score : %d", score));
            getTxtResult.setText(String.format(MATCH, correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);

            if (score != 0 && Common.currentUser!=null)
            {
                questionScore.child(String.format("%s_%s", Common.currentUser.getDisplayName(),
                       timestamp))
                        .setValue(new QuestionScore(String.format("%s_%s", Common.currentUser.getDisplayName(),
                                Common.categoryId),
                                Common.currentUser.getDisplayName(),
                                String.valueOf(score),
                                Common.categoryId,
                                Common.categoryName,
                                timestamp,
                                mode));
            }
        }

      if (Common.currentUser != null)
          UpdateScore(Common.currentUser.getDisplayName(), String.valueOf(Common.currentUser.getPhotoUrl()), new RankingCallBack<Ranking>() {
              @Override
              public void callback(Ranking ranking) {
                  rankingTbl.child(ranking.getUserName()).setValue(ranking);
              }
          });


    }

    private void UpdateScore(final String userName, final String photoUrl, final RankingCallBack<Ranking> callBack) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren())
                        {
                            QuestionScore ques = data.getValue(QuestionScore.class);
                            sum+=Integer.parseInt(ques.getScore());
                        }

                        Ranking ranking = new Ranking(userName, photoUrl, sum);
                        callBack.callback(ranking);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}
