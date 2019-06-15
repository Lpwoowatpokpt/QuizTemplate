package com.lpwoowatpokpt.quiztemplate.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lpwoowatpokpt.quiztemplate.Common.AppController;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.Fragments.CategoryFragment;
import com.lpwoowatpokpt.quiztemplate.UI.Fragments.RankingFragment;
import com.lpwoowatpokpt.quiztemplate.UI.Fragments.SettingsFragment;
import com.lpwoowatpokpt.quiztemplate.UI.Fragments.StatisticsFragment;
import com.lpwoowatpokpt.quiztemplate.UI.Login.LoginActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TinyDB tinyDB;

    SpotsDialog dialog;
    //ui
    FrameLayout userAvatarLayout;
    CircleImageView userAvatar;
    FloatingActionButton updateAvatar;
    TextView userName, userBalance;
    Button sign_in_btn, add_coins_btn;

    //update user avatar
    DatabaseReference users;
    CircleImageView userAvatarPreview;
    Button selectBtn, uploadBtn;
    private Uri filePath;
    private Bitmap bitmap;

    LinearLayout money_layout;

    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppController.getInstance().setOnVisibilityChangeListener(new AppController.ValueChangeListener() {
            @Override
            public void onChanged(Boolean isAppInBackground) {
                if (isAppInBackground && Common.currentUser!=null)
                    SwitchUserToOffline();
                else
                    if(Common.currentUser!=null)
                    SwitchUserToOnline();
            }
        });
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
        setContentView(R.layout.activity_home);

        users = Common.getDatabase().getReference(Common.USERS);

        dialog = (SpotsDialog) new SpotsDialog.Builder().setContext(this).build();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);

        userAvatarLayout = headerView.findViewById(R.id.userAvatarLayout);
        userAvatar = headerView.findViewById(R.id.user_avatar);
        updateAvatar = headerView.findViewById(R.id.updateAvatar);
        userName = headerView.findViewById(R.id.user_name);
        sign_in_btn = headerView.findViewById(R.id.sign_in_btn);

        userBalance = headerView.findViewById(R.id.balanceTxt);
        add_coins_btn = headerView.findViewById(R.id.add_200_coinsBtn);

        money_layout = headerView.findViewById(R.id.moneyLayout);

        if(Common.currentUser == null)
            AssignAnon();
        else{
            if (Common.isConnectedToInternet(this))
                AssignSigned();
            else
                Common.ShowToast(this, getString(R.string.no_internet));
        }

        SetDefaultFragment();
    }


    private void AssignAnon() {
        navigationView.inflateMenu(R.menu.activity_anon_home_drawer);

        sign_in_btn.setVisibility(View.VISIBLE);
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInIntent();
            }
        });

        money_layout.setVisibility(View.GONE);
        userAvatarLayout.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
    }

    private void AssignSigned() {
        navigationView.inflateMenu(R.menu.activity_home_drawer);

        sign_in_btn.setVisibility(View.GONE);

        money_layout.setVisibility(View.VISIBLE);
        userAvatarLayout.setVisibility(View.VISIBLE);
        userName.setVisibility(View.VISIBLE);

        SwitchUserToOnline();

        GetUserBalance();

        updateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserAvatar();
            }
        });

        add_coins_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCoins();
            }
        });

        Picasso.get().load(Common.currentUser.getPhotoUrl()).into(userAvatar);

        String [] nameSeparated = Common.currentUser.getDisplayName().split(" ");
        userName.setText(nameSeparated[0]);
    }


    private void SwitchUserToOnline() {
        users.orderByKey()
                .equalTo(Common.currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                            postSnap.getRef().child("online").setValue(Common.TRUE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SwitchUserToOffline() {
        users.orderByKey()
                .equalTo(Common.currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                            postSnap.getRef().child("online").setValue(Common.FALSE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                    userBalance.setText(String.valueOf(user.getBalance()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateUserAvatar() {
        final android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogLayout = inflater.inflate(R.layout.update_avatar_dialogue, null);

        userAvatarPreview = dialogLayout.findViewById(R.id.userAvatarPreview);

        Picasso.get().load(Common.currentUser.getPhotoUrl()).into(userAvatarPreview);

        selectBtn = dialogLayout.findViewById(R.id.buttonSelect);
        uploadBtn = dialogLayout.findViewById(R.id.buttonUpload);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFileChooser();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageToStorage();
            }
        });

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void UploadImageToStorage() {
        if (filePath != null)
        {
            dialog.show();
            StorageReference storageReference = Common.getStorage().getReference();

            final StorageReference reference = storageReference.child("images/avatars"+ Common.currentUser.getUid() + ".jpeg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[]data = baos.toByteArray();
            UploadTask uploadTask = reference.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();

                                    Common.currentUser.updateProfile(profileChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    users.child(Common.currentUser.getUid()).child("photoUrl")
                                                            .setValue(String.valueOf(uri));
                                                    Picasso.get().load(uri).into(userAvatar);
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage(getString(R.string.progress) + " " + ((int) + progress) + "%...");
                        }
                    });
        }
    }

    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void GetCoins() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userAvatarPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Common.currentUser!=null)
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void Logout() {
        SwitchUserToOffline();
        Intent signOut = new Intent(HomeActivity.this, LoginActivity.class);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        LoginManager.getInstance().logOut();
        auth.signOut();
        signOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signOut);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment selectedFragment = null;
        int id = item.getItemId();

        switch (id){
            case R.id.nav_game:
                selectedFragment = CategoryFragment.newinstance();
                toolbar.setTitle(getString(R.string.home));
                break;
            case R.id.nav_top:
                selectedFragment = RankingFragment.newinstance();
                toolbar.setTitle(getString(R.string.top_100));
                break;
            case R.id.nav_statistics:
                selectedFragment = StatisticsFragment.newinstance();
                toolbar.setTitle(getString(R.string.statistics));
                break;
            case R.id.nav_manage:
                selectedFragment = SettingsFragment.newinstance();
                toolbar.setTitle(getString(R.string.settings));
                break;
        }

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        assert selectedFragment != null;
        transaction.replace(R.id.container, selectedFragment);
        transaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SignInIntent() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    void SetDefaultFragment()
    {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, CategoryFragment.newinstance());
        transaction.commit();
    }

}
