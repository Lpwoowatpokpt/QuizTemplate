package com.lpwoowatpokpt.quiztemplate.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.MultiplayerGame;
import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MultiplayerPlayingActivity extends AppCompatActivity {

    TinyDB tinyDB;

    DatabaseReference users, multiplayer;

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
        setContentView(R.layout.activity_multiplayer_playing);

        users = Common.getDatabase().getReference(Common.USERS);
        multiplayer = Common.getDatabase().getReference(Common.MULTIPLAYER);
        

    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SwitchUserToOffline();
        Common.BackToMainMenu(this, FirebaseAuth.getInstance());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwitchUserToOffline();
    }
}
