package com.lpwoowatpokpt.quiztemplate.UI.Login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.HomeActivity;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity {

    TinyDB tinyDB;

    SpotsDialog dialog;

    LinearLayout root;

    TextInputEditText edt_email, edt_password;
    Button sign_in_btn;

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
        setContentView(R.layout.activity_sign_in);

        dialog = (SpotsDialog) new SpotsDialog.Builder().setContext(this).build();

        root = findViewById(R.id.root);

        edt_email = findViewById(R.id.edtEmail);
        edt_password = findViewById(R.id.edtPassword);

        sign_in_btn = findViewById(R.id.btn_sign_in);

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Common.isConnectedToInternet(getBaseContext())){
                    Common.ShowSnackbar(root, getString(R.string.no_internet));
                }
                else if (TextUtils.isEmpty(edt_email.getText())){
                    edt_email.setError(getString(R.string.email_required));
                }
                else if(TextUtils.isEmpty(edt_password.getText())){
                    edt_password.setError(getString(R.string.password_required));
                }
                else if(!Common.isEmailValid(edt_email.getText().toString())){
                    edt_email.setError(getString(R.string.email_not_valid));
                }
                else
                    login();

            }
        });
    }

    private void login() {

        dialog.show();

        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        Common.getAuth().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Common.currentUser = Common.getAuth().getCurrentUser();
                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    final String current_id = Common.currentUser.getUid();

                    Map<String, Object>tokenMap = new HashMap<>();
                    tokenMap.put(Common.TOKEN_ID, token_id);

                    Common.getFirestore().collection(Common.USERS).document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Common.ShowSnackbar(root, "Error :" + e.getMessage());
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
