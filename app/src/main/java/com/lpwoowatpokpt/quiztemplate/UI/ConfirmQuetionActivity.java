package com.lpwoowatpokpt.quiztemplate.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.QuestionSuggest;
import com.lpwoowatpokpt.quiztemplate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConfirmQuetionActivity extends AppCompatActivity implements View.OnClickListener {

    TinyDB tinyDB;

    int newQuestionId = 1;

    DatabaseReference category, questions, suggested_question;

    final List<String> category_names = new ArrayList<>();
    Spinner cateories_spinner;
    ArrayAdapter<String> adapter;

    //ui
    Button answerA, answerB, answerC, answerD;
    TextView imageDescriptionTxt, questionTxt;
    ImageView question_image;
    ImageView scaleBtn;

    Button hintBtn, addQuestionBtn;


    //scaled
    ImageView scaledImg;

    //hint
    TextView hintTxt;
    Button unlockHintBtn, get_200Btn;

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
        setContentView(R.layout.activity_confirm_question);

        category = Common.getDatabase().getReference(Common.CATEGORY);
        questions = Common.getDatabase().getReference(Common.QUESTIONS);
        suggested_question = Common.getDatabase().getReference(Common.QUESTION_SUGGEST);


        GetLastQuestionIndex();

        cateories_spinner = findViewById(R.id.spinner);

        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnap: dataSnapshot.getChildren())
                {
                    String _category_names = categorySnap.child("name").getValue(String.class);
                    category_names.add(_category_names);
                    PopulateSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        answerA = findViewById(R.id.answerABtn);
        answerB = findViewById(R.id.answerBBtn);
        answerC = findViewById(R.id.answerCBtn);
        answerD = findViewById(R.id.answerDBtn);

        imageDescriptionTxt = findViewById(R.id.image_description);
        questionTxt = findViewById(R.id.question_text);

        question_image = findViewById(R.id.question_image);
        scaleBtn = findViewById(R.id.scale_imageBtn);

        hintBtn = findViewById(R.id.hintBtn);
        addQuestionBtn = findViewById(R.id.confirmBtn);

        scaleBtn.setOnClickListener(this);
        hintBtn.setOnClickListener(this);
        addQuestionBtn.setOnClickListener(this);

        if (tinyDB.getBoolean(Common.IS_IMAGE_QUESTION, true)){
            questionTxt.setVisibility(View.GONE);
            imageDescriptionTxt.setText(tinyDB.getString(Common.TEXT_IMAGE_QUESTION));
            Picasso.get().load(tinyDB.getString(Common.IMAGE_PATH)).into(question_image);
        }
        else {
            imageDescriptionTxt.setVisibility(View.GONE);
            question_image.setVisibility(View.GONE);
            scaleBtn.setVisibility(View.GONE);

            questionTxt.setText(tinyDB.getString(Common.TEXT_QUESTION));
        }

        answerA.setText(tinyDB.getString(Common.ANSWER_1));
        answerB.setText(tinyDB.getString(Common.ANSWER_2));
        answerC.setText(tinyDB.getString(Common.ANSWER_3));
        answerD.setText(tinyDB.getString(Common.ANSWER_4));


        HighlightCorrectAnswer();
    }

    private void GetLastQuestionIndex() {
        questions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int childrens = (int)dataSnapshot.getChildrenCount();
                newQuestionId += childrens;

                suggested_question.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int suggested = (int)dataSnapshot.getChildrenCount();
                        newQuestionId += suggested;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void HighlightCorrectAnswer() {
        int index = 0;
      List<String>answers = new ArrayList<>();

      answers.add(tinyDB.getString(Common.ANSWER_1));
      answers.add(tinyDB.getString(Common.ANSWER_2));
      answers.add(tinyDB.getString(Common.ANSWER_3));
      answers.add(tinyDB.getString(Common.ANSWER_4));

      for (int i = 0; i < answers.size(); i++){
          if (answers.get(i).equals(tinyDB.getString(Common.CORRECT_ANSWER))){
              index = i;
          }
      }

      if (index == 0)
          answerA.setBackgroundResource(R.drawable.correct_answer);
      else if(index == 1)
          answerB.setBackgroundResource(R.drawable.correct_answer);
      else if(index == 2)
          answerC.setBackgroundResource(R.drawable.correct_answer);
      else
          answerD.setBackgroundResource(R.drawable.correct_answer);

    }

    private void PopulateSpinner() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, category_names);
        cateories_spinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CreateModeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hintBtn:
                ShowHintDialogue();
                break;
            case R.id.scale_imageBtn:
                ShowImageDialogue();
                break;
            case R.id.confirmBtn:
                AddQuestion();
                break;

        }
    }

    private void AddQuestion() {
        String answerA = tinyDB.getString(Common.ANSWER_1);
        String answerB = tinyDB.getString(Common.ANSWER_2);
        String answerC = tinyDB.getString(Common.ANSWER_3);
        String answerD = tinyDB.getString(Common.ANSWER_4);
        String correctAnswer = tinyDB.getString(Common.CORRECT_ANSWER);
        String imageUrl = tinyDB.getString(Common.IMAGE_PATH);


        String question;
        String isImageQuestion;

        if (tinyDB.getBoolean(Common.IS_IMAGE_QUESTION,true)){
             question = tinyDB.getString(Common.TEXT_IMAGE_QUESTION);
             isImageQuestion = "true";
        }else{
            question = tinyDB.getString(Common.TEXT_QUESTION);
            isImageQuestion = "false";
        }

        String hint = tinyDB.getString(Common.HINT);


        QuestionSuggest questionSuggest = new QuestionSuggest(answerA, answerB, answerC, answerD,
                correctAnswer, imageUrl,
                isImageQuestion, question,
                GetCategoryId(),
                hint,
                String.valueOf(newQuestionId),
                Common.currentUser.getDisplayName(),
                Common.IN_PROGRESS);

       suggested_question.child(Common.currentUser.getUid()+"_"+newQuestionId)
               .setValue(questionSuggest)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       DeleteQuestionPreview();
                       Intent intent = new Intent(ConfirmQuetionActivity.this, SuggestedListActivity.class);
                       startActivity(intent);
                       Common.ShowToast(getApplicationContext(), getString(R.string.question_in_quee));
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Common.ShowToast(getApplicationContext(), e.toString());
           }
       });
    }

    private void DeleteQuestionPreview() {
        String empty = "";
        tinyDB.putString(Common.ANSWER_1, empty);
        tinyDB.putString(Common.ANSWER_2, empty);
        tinyDB.putString(Common.ANSWER_3, empty);
        tinyDB.putString(Common.ANSWER_4, empty);
        tinyDB.putString(Common.CORRECT_ANSWER, empty);
        tinyDB.putString(Common.TEXT_QUESTION, empty);
        tinyDB.putString(Common.TEXT_IMAGE_QUESTION, empty);
        tinyDB.putString(Common.IMAGE_PATH, empty);
        tinyDB.putString(Common.HINT, empty);
        tinyDB.putBoolean(Common.IS_IMAGE_QUESTION, false);
    }


    private String GetCategoryId() {
        if (cateories_spinner.getSelectedItemPosition()<10)
            return "0" + cateories_spinner.getSelectedItemPosition();
        else
            return String.valueOf(cateories_spinner.getSelectedItemPosition());

    }

    private void ShowImageDialogue() {
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
        scaledImg = dialogLayout.findViewById(R.id.scaledImage);

        Picasso.get()
                .load(tinyDB.getString(Common.IMAGE_PATH))
                .into(scaledImg);

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void ShowHintDialogue() {
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

        get_200Btn = dialogLayout.findViewById(R.id.button_rewards);
        get_200Btn.setVisibility(View.GONE);
        unlockHintBtn = dialogLayout.findViewById(R.id.button_unlock_hint);
        unlockHintBtn.setVisibility(View.GONE);

        hintTxt.setText(tinyDB.getString(Common.HINT));

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}
