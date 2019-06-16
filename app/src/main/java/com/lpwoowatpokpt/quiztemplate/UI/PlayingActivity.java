package com.lpwoowatpokpt.quiztemplate.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.Report;
import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.R;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String REPORT = "Report";
    private static final String DATE = "dd-MM-yyyy hh:mm:ss";

    TinyDB tinyDB;

    DatabaseReference reportList;

    final static long INTERVAL = 1000;
    static long TIMEOUT;

    int progressValue = 0;

    CountDownTimer countDownTimer;


    int index=0, score=0, lives=3, thisQuestion=0, totalQuestion, correctAnswer;

    ProgressBar progressBar;

    String mode = "";
    String name = "";

    int balance = 0;

    int balance_to_add = 0;

    String date;

    ImageView question_image, scaleBtn, scaledImage;
    ImageView live0, live1, live2;

    Button btnA, btnB, btnC, btnD, btnHint;
    TextView txtScore, txtUserBalance, txtQuestionNum, txtQuestionText, txtImageQuestionDescription, txtQuestionCenter;

    MediaPlayer correctSound, wrongSound;

    //hint
    Button unlockHintBtn, get_200Btn;
    TextView hintTxt;

    //bug report
    ImageButton bug_report;
    EditText reportMsg;
    Button sendReportBtn;


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
        setContentView(R.layout.activity_playing);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        reportList = Common.getDatabase().getReference(REPORT);

        date = (DateFormat.format(DATE, new java.util.Date()).toString());

        Bundle extra = getIntent().getExtras();
        if (extra != null){
            mode = extra.getString("MODE");
            name = extra.getString("NAME");
        }
        SelectMode();


        TIMEOUT = tinyDB.getLong(Common.TIME,0);

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);


        txtQuestionText = findViewById(R.id.question_text);
        txtImageQuestionDescription = findViewById(R.id.image_description);
        txtQuestionCenter = findViewById(R.id.questionCenterTxt);

        txtQuestionNum = findViewById(R.id.txtCountDown);
        txtScore = findViewById(R.id.txtScore);
        txtUserBalance = findViewById(R.id.balanceTxt);

        live0 = findViewById(R.id.live0);
        live1 = findViewById(R.id.live1);
        live2 = findViewById(R.id.live2);

        scaleBtn = findViewById(R.id.scale_imageBtn);

        btnA = findViewById(R.id.answerABtn);
        btnB = findViewById(R.id.answerBBtn);
        btnC = findViewById(R.id.answerCBtn);
        btnD = findViewById(R.id.answerDBtn);
        btnHint = findViewById(R.id.hintBtn);

        bug_report = findViewById(R.id.bug_report);
        bug_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBugReport();
            }
        });

        //check if user exists
        if (Common.currentUser!=null)
            GetUserBalance();
        else {
            btnHint.setVisibility(View.INVISIBLE);
            txtUserBalance.setVisibility(View.INVISIBLE);
            bug_report.setVisibility(View.INVISIBLE);
        }

        question_image = findViewById(R.id.question_image);

        progressBar = findViewById(R.id.progressBar);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
        
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext()))
                OpenHintDialogue();
            }
        });

        if (tinyDB.getBoolean(Common.IS_GAME_ON_TIME, true)){
            progressBar.setVisibility(View.VISIBLE);
            int progressValue = (int) TIMEOUT / 1000;
            progressBar.setMax(progressValue);
            bug_report.setVisibility(View.INVISIBLE);
        }else
            progressBar.setVisibility(View.GONE);

    }


    private void GetUserBalance() {
        Query query = Common.getDatabase().getReference(Common.USERS)
                .orderByKey()
                .equalTo(Common.currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                    User user = postSnap.getValue(User.class);
                    assert user != null;
                    txtUserBalance.setText(String.valueOf(user.getBalance()));
                    balance = Integer.parseInt(String.valueOf(user.getBalance()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateFirebaseBalance(final int balanceToAdd){
        final Query query = Common.getDatabase().getReference(Common.USERS)
                .orderByKey()
                .equalTo(Common.currentUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren()) {
                    int newBalance = balance + balanceToAdd;
                    postSnap.getRef().child(Common.BALANCE).setValue(newBalance);
                    balance = newBalance;
                    txtUserBalance.setText(String.valueOf(newBalance));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OpenHintDialogue() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogLayout = inflater.inflate(R.layout.hint_dialog, null);
        hintTxt = dialogLayout.findViewById(R.id.hint_txt);
        hintTxt.setVisibility(View.GONE);
        get_200Btn = dialogLayout.findViewById(R.id.button_rewards);
        get_200Btn.setVisibility(View.GONE);
        unlockHintBtn = dialogLayout.findViewById(R.id.button_unlock_hint);
        unlockHintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintTxt.setVisibility(View.VISIBLE);
                hintTxt.setText(Common.questionsList.get(index).getHint());
                unlockHintBtn.setVisibility(View.GONE);

            }
        });


        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View view) {
        if (index < totalQuestion)
        {
            final Button clickedButton = (Button) view;
            if (clickedButton.getText().toString().equals(Common.questionsList.get(index).getCorrectAnswer()))
            {
                if (tinyDB.getBoolean(Common.IS_GAME_ON_TIME, true))
                    countDownTimer.cancel();

                if (tinyDB.getBoolean(Common.IS_SOUND_ON, true))
                    correctSound.start();

                score +=1;
                correctAnswer++;

                if (Common.currentUser!=null)
                UpdateFirebaseBalance(balance_to_add);

                clickedButton.setBackgroundResource(R.drawable.correct_answer);
                btnA.setEnabled(false);
                btnB.setEnabled(false);
                btnC.setEnabled(false);
                btnD.setEnabled(false);
                new CountDownTimer(1500, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        btnA.setEnabled(true);
                        btnB.setEnabled(true);
                        btnC.setEnabled(true);
                        btnD.setEnabled(true);
                        btnA.setBackgroundResource(R.drawable.answer);
                        btnB.setBackgroundResource(R.drawable.answer);
                        btnC.setBackgroundResource(R.drawable.answer);
                        btnD.setBackgroundResource(R.drawable.answer);
                        ShowQuestion(++index);
                    }
                }.start();
            }else
            {
                --lives;
                ChangeLives();

                if (tinyDB.getBoolean(Common.IS_SOUND_ON, false))
                    wrongSound.start();

                if (lives == 2)
                {
                    clickedButton.setEnabled(false);
                    clickedButton.setBackgroundResource(R.drawable.wrong_answer);
                }
                if (lives == 1)
                {
                    clickedButton.setEnabled(false);
                    clickedButton.setBackgroundResource(R.drawable.wrong_answer);
                }
                if (lives <= 0)
                {
                    btnA.setEnabled(false);
                    btnB.setEnabled(false);
                    btnC.setEnabled(false);
                    btnD.setEnabled(false);
                    if (btnA.getText().toString().equals(Common.questionsList.get(index).getCorrectAnswer()))
                        btnA.setBackgroundResource(R.drawable.correct_answer);
                    if (btnB.getText().toString().equals(Common.questionsList.get(index).getCorrectAnswer()))
                        btnB.setBackgroundResource(R.drawable.correct_answer);
                    if (btnC.getText().toString().equals(Common.questionsList.get(index).getCorrectAnswer()))
                        btnC.setBackgroundResource(R.drawable.correct_answer);
                    if (btnD.getText().toString().equals(Common.questionsList.get(index).getCorrectAnswer()))
                        btnD.setBackgroundResource(R.drawable.correct_answer);
                    clickedButton.setBackgroundResource(R.drawable.wrong_answer);
                    new CountDownTimer(3000, 500){
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            btnA.setEnabled(true);
                            btnB.setEnabled(true);
                            btnC.setEnabled(true);
                            btnD.setEnabled(true);
                            btnA.setBackgroundResource(R.drawable.answer);
                            btnB.setBackgroundResource(R.drawable.answer);
                            btnC.setBackgroundResource(R.drawable.answer);
                            btnD.setBackgroundResource(R.drawable.answer);

                            Intent intent = new Intent(PlayingActivity.this, DoneActivity.class);

                                Bundle dataSend = new Bundle();
                                dataSend.putInt("SCORE", score);
                                dataSend.putInt("TOTAL", totalQuestion);
                                dataSend.putInt("CORRECT", correctAnswer);
                                dataSend.putString("TIMESTAMP", date);
                                dataSend.putString("MODE", mode);

                                intent.putExtras(dataSend);

                            startActivity(intent);
                            finish();
                        }
                    }.start();
                }
            }
            txtScore.setText(String.format("%d", score));
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (tinyDB.getBoolean(Common.IS_GAME_ON_TIME, true)){
            countDownTimer = new CountDownTimer(TIMEOUT, INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progressBar.setProgress(progressValue);
                    progressValue++;
                }

                @Override
                public void onFinish() {
                    --lives;
                    ChangeLives();
                    ShowQuestion(++index);
                }
            };
            ShowQuestion(index);
        }else
            ShowQuestion(index = 1);
    }


    private void ChangeLives(){
        switch (lives){
            case 2:
                live2.setColorFilter(Color.GRAY);
                break;
            case 1:
                live1.setColorFilter(Color.GRAY);
                break;
            case 0:
                live0.setColorFilter(Color.GRAY);
                break;
        }
    }

    private void SelectMode() {
        switch (mode) {
            case Common.EASY:
                totalQuestion = Common.questionsList.size() / 4;
                balance_to_add = 1;
                break;
            case Common.MEDIUM:
                totalQuestion = Common.questionsList.size() / 3;
                balance_to_add = 3;
                break;
            case Common.HARD:
                totalQuestion = Common.questionsList.size() / 2;
                balance_to_add = 5;
                break;
            default:
                totalQuestion = Common.questionsList.size();
                balance_to_add = 10;
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    private void ShowQuestion(int index)
    {
        if (index < totalQuestion && lives != 0) {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            if (Common.questionsList.get(index).getIsImageQuestion().equals("true")) {
                Picasso.get()
                        .load(Common.questionsList.get(index).getImageUrl())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);

                txtImageQuestionDescription.setText(Common.questionsList.get(index).getQuestion());
                txtImageQuestionDescription.setVisibility(View.VISIBLE);

                txtQuestionText.setVisibility(View.GONE);
                txtQuestionCenter.setVisibility(View.GONE);

                scaleBtn.setVisibility(View.VISIBLE);
                scaleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowImageDialog();
                    }
                });
            } else {
                txtQuestionText.setText(Common.questionsList.get(index).getQuestion());
                txtQuestionCenter.setText(Common.questionsList.get(index).getImageUrl());

                txtQuestionText.setVisibility(View.VISIBLE);
                txtQuestionCenter.setVisibility(View.VISIBLE);

                question_image.setVisibility(View.GONE);
                txtImageQuestionDescription.setVisibility(View.GONE);
                scaleBtn.setVisibility(View.GONE);
            }
            btnA.setText(Common.questionsList.get(index).getAnswerA());
            btnB.setText(Common.questionsList.get(index).getAnswerB());
            btnC.setText(Common.questionsList.get(index).getAnswerC());
            btnD.setText(Common.questionsList.get(index).getAnswerD());

            if (tinyDB.getBoolean(Common.IS_GAME_ON_TIME, true))
                countDownTimer.start();
        }else
        {
            Intent intent = new Intent(this, DoneActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            dataSend.putString("TIMESTAMP", date);
            dataSend.putString("MODE", mode);

            intent.putExtras(dataSend);
            startActivity(intent);

            finish();
        }
    }

    private void ShowImageDialog()
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.scaled_image, null);
        scaledImage = dialogLayout.findViewById(R.id.scaledImage);

        Picasso.get()
                .load(Common.questionsList.get(index).getImageUrl())
                .into(scaledImage);

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void ShowBugReport() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.bug_report_item, null);

        reportMsg = dialogLayout.findViewById(R.id.edtMsg);
        sendReportBtn = dialogLayout.findViewById(R.id.sendReportBtn);


        sendReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(reportMsg.getText().toString())){
                    reportMsg.setError(getString(R.string.empty_message));
                }else{
                    SendReport(reportMsg.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void SendReport(String msg) {
        Report report = new Report();

        report.setQuestionID(Common.questionsList.get(index).getQuestionID());
        report.setMessage(msg);

        reportList.child(Common.currentUser.getUid()+"_"+Common.questionsList.get(index).getQuestionID())
                .setValue(report)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Common.ShowToast(getBaseContext(),getString(R.string.message_sent));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Common.ShowToast(getBaseContext(),String.valueOf(e));
            }
        });
    }


}
