package com.lpwoowatpokpt.quiztemplate.UI.Login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.HomeActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    TinyDB tinyDB;

    SpotsDialog dialog;

    LinearLayout root;

    //firebase
    private StorageReference mStorage;

    //sign_in_dialogue
    CircleImageView addAvatarBtn;
    TextInputEditText edt_email, edt_userName, edt_password;
    Button create_account_btn;

    private Uri imageUri;

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

        imageUri = null;

        dialog = (SpotsDialog) new SpotsDialog.Builder().setContext(this).build();

        root = findViewById(R.id.root);

        mStorage = Common.getStorage().getReference().child(Common.AVATAR_REVERENCE);
;

        addAvatarBtn = findViewById(R.id.userAvatarPreview);
        addAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        edt_email = findViewById(R.id.edtEmail);
        edt_userName = findViewById(R.id.edtName);
        edt_password = findViewById(R.id.edtPassword);

        create_account_btn = findViewById(R.id.btn_sign_up);
        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Common.isConnectedToInternet(getBaseContext())){
                    Common.ShowSnackbar(root, getString(R.string.no_internet));
                }
                else if (imageUri==null){
                    Common.ShowSnackbar(root, getString(R.string.add_avatar));
                }
               else if(TextUtils.isEmpty(edt_email.getText())){
                   edt_email.setError(getString(R.string.email_required));
               }
               else if (TextUtils.isEmpty(edt_userName.getText())){
                   edt_userName.setError(getString(R.string.username_required));
               }
               else if (TextUtils.isEmpty(edt_password.getText())){
                 edt_password.setError(getString(R.string.password_required));
               }
               else if (!Common.isEmailValid(edt_email.getText().toString())){
                   edt_email.setError(getString(R.string.email_not_valid));
               }
               else
                   signInWithEmailAndPassword(edt_email.getText().toString(),edt_userName.getText().toString(),  edt_password.getText().toString());
            }
        });
    }

    private void signInWithEmailAndPassword(String email, final String name, String pass) {
        dialog.show();
        Common.getAuth().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final String userId = Common.getAuth().getUid();
                    StorageReference user_profile = mStorage.child(userId+".jpg");

                    user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if (taskSnapshot.getMetadata()!=null){
                                        if(taskSnapshot.getMetadata().getReference()!=null){
                                            Task<Uri>result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    final String imageUrl = uri.toString();

                                                    String tokenId = FirebaseInstanceId.getInstance().getToken();

                                                    final Map<String,Object>userMap = new HashMap<>();
                                                    userMap.put(Common.NAME, name);
                                                    userMap.put(Common.IMAGE, imageUrl);
                                                    assert tokenId!=null;
                                                    userMap.put(Common.TOKEN_ID, tokenId);
                                                    userMap.put(Common.ONLINE, Common.FALSE);

                                                    Common.getFirestore().collection(Common.USERS).document(userId).set(userMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    dialog.dismiss();
                                                                    login();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Common.ShowSnackbar(root, "Error : " + e.getMessage());
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void login() {
        Common.currentUser = Common.getAuth().getCurrentUser();
        Intent mainIntent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE){
           if (data!=null){
                imageUri = data.getData();
                addAvatarBtn.setImageURI(imageUri);
            }


        }
    }
}
