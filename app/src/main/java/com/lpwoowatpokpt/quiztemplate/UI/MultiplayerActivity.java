package com.lpwoowatpokpt.quiztemplate.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.RecycleAdapters.ItemAvatarAdapter;

import java.util.Collections;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MultiplayerActivity extends AppCompatActivity {

    TinyDB tinyDB;

    DatabaseReference users, multiplayer;

    ItemAvatarAdapter avatarAdapter;
    RecyclerView recyclerUserList;

    Button randomGameBtn, inviteFriendBtn;


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
        setContentView(R.layout.activity_multiplayer);

        users = Common.getDatabase().getReference(Common.USERS);

        PopulateRandomUsersList();

        multiplayer = Common.getDatabase().getReference(Common.MULTIPLAYER);

        recyclerUserList = findViewById(R.id.recyclerAvatars);
        recyclerUserList.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerUserList.setItemAnimator(new DefaultItemAnimator());

        randomGameBtn = findViewById(R.id.randomGameBtn);
        inviteFriendBtn = findViewById(R.id.inviteFriendButton);

        randomGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    private void PopulateRandomUsersList() {
        Log.e("slut1", String.valueOf(Common.onlineList.size()));

        Query query = users.orderByChild("online").equalTo(Common.TRUE);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.onlineList.clear();

                for (DataSnapshot posSnap: dataSnapshot.getChildren()){
                    User user = posSnap.getValue(User.class);
                        Common.onlineList.add(user);
                        Collections.shuffle(Common.onlineList);
                        avatarAdapter = new ItemAvatarAdapter(Common.onlineList);
                        recyclerUserList.setAdapter(avatarAdapter);
                        avatarAdapter.notifyDataSetChanged();


                    Log.e("slut2", String.valueOf(Common.onlineList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.BackToMainMenu(this, FirebaseAuth.getInstance());
    }
}

