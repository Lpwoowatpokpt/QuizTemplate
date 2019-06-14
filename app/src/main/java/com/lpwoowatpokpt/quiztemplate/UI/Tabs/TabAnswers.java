package com.lpwoowatpokpt.quiztemplate.UI.Tabs;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.Fragments.CategoryFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabAnswers extends Fragment {

    TinyDB tinyDB;

    EditText edtAnswerA, edtAnswerB, edtAnswerC, edtAnswerD;

    RadioGroup radioGroup;
    RadioButton rbA, rbB, rbC, rbD;

    public static TabAnswers getInstance()
    {
        return new TabAnswers();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View myView = inflater.inflate(R.layout.fragment_tab_answers, container, false);

        edtAnswerA = myView.findViewById(R.id.edtAnswerA);
        edtAnswerB = myView.findViewById(R.id.edtAnswerB);
        edtAnswerC = myView.findViewById(R.id.edtAnswerC);
        edtAnswerD = myView.findViewById(R.id.edtAnswerD);

        Common.SaveEditText(tinyDB, Common.ANSWER_1, edtAnswerA);
        Common.SaveEditText(tinyDB, Common.ANSWER_2, edtAnswerB);
        Common.SaveEditText(tinyDB, Common.ANSWER_3, edtAnswerC);
        Common.SaveEditText(tinyDB, Common.ANSWER_4, edtAnswerD);

        radioGroup = myView.findViewById(R.id.radioGroup);

        rbA = myView.findViewById(R.id.radioAnswerA);
        rbB = myView.findViewById(R.id.radioAnswerB);
        rbC = myView.findViewById(R.id.radioAnswerC);
        rbD = myView.findViewById(R.id.radioAnswerD);

        if (!tinyDB.getString(Common.CORRECT_ANSWER).equals(""))
            SetCheckedRadioButton();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioAnswerA:
                        tinyDB.putString(Common.CORRECT_ANSWER, edtAnswerA.getText().toString());
                        break;
                    case R.id.radioAnswerB:
                        tinyDB.putString(Common.CORRECT_ANSWER, edtAnswerB.getText().toString());
                        break;
                    case R.id.radioAnswerC:
                        tinyDB.putString(Common.CORRECT_ANSWER, edtAnswerC.getText().toString());
                        break;
                    case R.id.radioAnswerD:
                        tinyDB.putString(Common.CORRECT_ANSWER, edtAnswerD.getText().toString());
                        break;
                }
            }
        });

        LoadPreviousAnswersIfExists();


        return myView;
    }

    private void SetCheckedRadioButton() {
        if (tinyDB.getString(Common.ANSWER_1).equals(tinyDB.getString(Common.CORRECT_ANSWER)))
            rbA.setChecked(true);
        else if (tinyDB.getString(Common.ANSWER_2).equals(tinyDB.getString(Common.CORRECT_ANSWER)))
            rbB.setChecked(true);
        else if(tinyDB.getString(Common.ANSWER_3).equals(tinyDB.getString(Common.CORRECT_ANSWER)))
            rbC.setChecked(true);
        else
            rbD.setChecked(true);
    }

    private void LoadPreviousAnswersIfExists() {
        if (!tinyDB.getString(Common.ANSWER_1).equals(""))
            edtAnswerA.setText(tinyDB.getString(Common.ANSWER_1));

        if (!tinyDB.getString(Common.ANSWER_2).equals(""))
            edtAnswerB.setText(tinyDB.getString(Common.ANSWER_2));

        if (!tinyDB.getString(Common.ANSWER_3).equals(""))
            edtAnswerC.setText(tinyDB.getString(Common.ANSWER_3));

        if (!tinyDB.getString(Common.ANSWER_4).equals(""))
            edtAnswerD.setText(tinyDB.getString(Common.ANSWER_4));
    }
}
