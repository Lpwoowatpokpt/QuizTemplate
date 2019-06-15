package com.lpwoowatpokpt.quiztemplate.UI.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.HomeActivity;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    DatabaseReference wallpapers;
    public static final String WALLPAPERS = "Wallpapers";

    //facebook permissions
    private static final String PUBLIC_PROFILE = "public_profile";

    //fix blury avatar
    public static final String FACEBOOK_PHOTO_500 = "?height=500";

    KenBurnsView background;

    FirebaseAuth auth;
    DatabaseReference users;

    RelativeLayout root;

    SpotsDialog dialog;

    Button anonimusButton;

    TinyDB tinyDB;

    //sign in with email and password
    Button sign_in_btn, sign_up_btn;


    //google sign in
    FloatingActionButton googleSignIn;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE = 9001;

    //facebook sign in
    FloatingActionButton facebookSignIn;
    private CallbackManager callbackManager;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
       SignInOldUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(this);

        if (IsFirstRun()){
            tinyDB.putBoolean(Common.IS_SOUND_ON, false);
            tinyDB.putBoolean(Common.IS_GAME_ON_TIME, false);
            tinyDB.putBoolean(Common.IS_DARK_MODE, false);
            tinyDB.putBoolean(Common.IS_IMAGE_QUESTION, false);
            tinyDB.putLong(Common.TIME, 30000);
            tinyDB.putInt(Common.PROGRESS, 30);
            tinyDB.putInt(Common.THEME_ID, 0);
            tinyDB.putString(Common.FONT, Common.FONT_DEFAULT);
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(tinyDB.getString(Common.FONT))
                .setFontAttrId(R.attr.fontPath)
                .build());
        FacebookSdk.sdkInitialize(getApplicationContext());
        Utils.onActivityCreateSetTheme(this, tinyDB.getInt(Common.THEME_ID));
        setContentView(R.layout.activity_login);

        sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_in_btn.setOnClickListener(this);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        users = Common.getDatabase().getReference(Common.USERS);

        wallpapers = Common.getDatabase().getReference(WALLPAPERS);
        background = findViewById(R.id.image_bg);

        if (Common.isConnectedToInternet(getApplicationContext()))
            LoadWallpaperFromFirebase();

        root = findViewById(R.id.root_layout);
        dialog = (SpotsDialog) new SpotsDialog.Builder().setContext(this).build();

        //buttons
        anonimusButton = findViewById(R.id.anonimus_button);
        googleSignIn = findViewById(R.id.google_btn);
        facebookSignIn = findViewById(R.id.facebook_btn);

        anonimusButton.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);
        facebookSignIn.setOnClickListener(this);


        //google
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_id))
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API,
                signInOptions).build();

        //facebook
        callbackManager = CallbackManager.Factory.create();
    }

    public boolean IsFirstRun(){
        return tinyDB.getAll().isEmpty();

    }



    private void LoadWallpaperFromFirebase() {
        wallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                Random random = new Random();
                int wallpaperIndex = random.nextInt(size);

                wallpapers.orderByKey()
                        .equalTo("0" + wallpaperIndex)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                                   String url = String.valueOf(postSnap.child("url").getValue());
                                   Picasso.get().load(url).into(background);
                               }
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean CheckIfUserIdExist(String id, DataSnapshot dataSnapshot){
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            if(Objects.equals(ds.getKey(), id))
                return true;
        }
        return false;
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private  void ShowSnackbar(String message){
        Snackbar snack = Snackbar.make(root, message, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.btn_sign_up:
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
            break;
        case R.id.anonimus_button:
            ContinueAnonimus();
            break;
        case R.id.google_btn:
            if (Common.isConnectedToInternet(this))
                SignInWithGoogle();
            else
                ShowSnackbar(getString(R.string.no_internet));
            break;
        case R.id.facebook_btn:
            if(Common.isConnectedToInternet(this))
            InitFacebookSdk();
            else
                ShowSnackbar(getString(R.string.no_internet));
            break;
}
    }

    private void InitFacebookSdk() {
        final LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.performClick();
        loginButton.setReadPermissions(Collections.singletonList(PUBLIC_PROFILE));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Common.isConnectedToInternet(getApplicationContext()))
                {
                    SignInWithFacebook(loginResult.getAccessToken());
                }else
                {
                    Snackbar.make(root, getString(R.string.no_internet), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void SignInWithFacebook(AccessToken accessToken) {
        dialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            dialog.dismiss();
                            ShowSnackbar(getString(R.string.failed));
                        }else
                        {
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(CheckIfUserIdExist(Objects.requireNonNull(auth.getCurrentUser()).getUid(), dataSnapshot))
                                        SignInOldUser();
                                    else
                                        SignInNewFBUser(task);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
    }


    private void SignInWithGoogle() {
        dialog.show();
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void ContinueAnonimus() {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            Common.currentUser = null;
            startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == REQUEST_CODE)
            {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess())
                {
                    GoogleSignInAccount account = result.getSignInAccount();
                    if (Common.isConnectedToInternet(getApplicationContext()))
                    {
                        assert account != null;
                        FirebaseAuthWithGoogle(account);
                    }else
                    ShowSnackbar(getString(R.string.no_internet));
                }
            }
    }

    private void FirebaseAuthWithGoogle(final GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(CheckIfUserIdExist(Objects.requireNonNull(auth.getCurrentUser()).getUid(), dataSnapshot))
                                        SignInOldUser();
                                    else
                                        SignInNewUser(account);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }


    private void SignInNewUser(GoogleSignInAccount account) {
        final User user = new User();

        if (account.getPhotoUrl() != null)
            user.setPhotoUrl(account.getPhotoUrl().toString());

        user.setUserName(account.getDisplayName());
        user.setOnline(Common.FALSE);
        user.setBalance(100);

        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), getString(R.string.success),
                                Toast.LENGTH_LONG).show();
                        Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                        Common.currentUser = auth.getCurrentUser();
                        startActivity(homeActivity);
                        finish();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        ShowSnackbar(getString(R.string.failed));
                    }
                });
    }

    private void SignInNewFBUser(Task<AuthResult> task) {

        String uid = task.getResult().getUser().getUid();

        String photoUrl = task.getResult().getUser().getPhotoUrl() + FACEBOOK_PHOTO_500;

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(photoUrl))
                .build();

        auth.getCurrentUser().updateProfile(profileChangeRequest);

        String name = task.getResult().getUser().getDisplayName();

        User user = new User(name, photoUrl, Common.FALSE, 100);

        users.child(uid).setValue(user);


        Toast.makeText(getBaseContext(), getString(R.string.success),
                Toast.LENGTH_LONG).show();

        Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        Common.currentUser = auth.getCurrentUser();
        startActivity(homeActivity);
        finish();

        dialog.dismiss();
    }

    private void SignInOldUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (Common.isConnectedToInternet(getBaseContext())) {
            if (currentUser != null) {
                Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                Common.currentUser = currentUser;
                startActivity(homeActivity);
                dialog.dismiss();
                finish();
            }
        } else
        {
            ShowSnackbar(getString(R.string.no_internet));
        }
    }
}
