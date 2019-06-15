package com.lpwoowatpokpt.quiztemplate.UI.Login;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.R;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    TinyDB tinyDB;

    //sign_in_dialogue
    CircleImageView addAvatarBtn;
    TextInputEditText edt_email, edt_userName, edt_password;
    ImageView btn_switch_pass_visibility;
    Button create_account_btn;

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
        setContentView(R.layout.activity_sign_up);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
