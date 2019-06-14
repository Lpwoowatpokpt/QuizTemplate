package com.lpwoowatpokpt.quiztemplate.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Common.TinyDB;
import com.lpwoowatpokpt.quiztemplate.Common.Utils;
import com.lpwoowatpokpt.quiztemplate.Helper.RecyclerItemTouchHelper;
import com.lpwoowatpokpt.quiztemplate.Model.QuestionSuggest;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.ViewHolder.SuggestedViewHolder;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SuggestedListActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    TinyDB tinyDB;

    DatabaseReference question_suggested;

    RecyclerView suggest_list;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<QuestionSuggest> options;
    FirebaseRecyclerAdapter<QuestionSuggest, SuggestedViewHolder> adapter;

    TextView emptyText;
    FloatingActionButton addQuestion;
    RelativeLayout root;

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
        setContentView(R.layout.activity_suggested_list);

        root = findViewById(R.id.root);

        emptyText = findViewById(R.id.empty_recyclerTxt);
        addQuestion = findViewById(R.id.addQuestionBtn);

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuggestedListActivity.this, CreateModeActivity.class);
                startActivity(intent);
            }
        });

        question_suggested = Common.getDatabase().getReference(Common.QUESTION_SUGGEST);

        LoadUserQuestions(Common.currentUser.getDisplayName());

        suggest_list = findViewById(R.id.recycler_suggested);
        suggest_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        suggest_list.setLayoutManager(layoutManager);
        suggest_list.setItemAnimator(new DefaultItemAnimator());
        suggest_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        suggest_list.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(suggest_list);
    }


    private void LoadUserQuestions(String displayName) {
        options = new FirebaseRecyclerOptions.Builder<QuestionSuggest>()
                .setQuery(question_suggested.orderByChild("userName").equalTo(displayName), QuestionSuggest.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<QuestionSuggest, SuggestedViewHolder>(options) {
            @SuppressLint("PrivateResource")
            @Override
            protected void onBindViewHolder(@NonNull SuggestedViewHolder viewHolder, int position, @NonNull QuestionSuggest model) {
                if (tinyDB.getInt(Common.THEME_ID)==0)
                    viewHolder.viewForeground.setBackgroundColor(Color.WHITE);
                else
                    viewHolder.viewForeground.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));


                if (model.getIsImageQuestion().equals("true")){
                    viewHolder.questionImagePreview.setVisibility(View.VISIBLE);
                    Picasso.get().load(model.getImageUrl()).into(viewHolder.questionImagePreview);
                    viewHolder.quesionTxt.setTextColor(Color.WHITE);
                    viewHolder.quesionTxt.setBackgroundColor(getResources().getColor(R.color.questionTextBackground));
                }else
                    viewHolder.questionImagePreview.setVisibility(View.GONE);

                viewHolder.questionNumberTxt.setText(String.valueOf(position + 1));
                viewHolder.quesionTxt.setText(model.getQuestion());
                viewHolder.statusTxt.setText(model.getStatus());

                if (model.getStatus().equals(Common.IN_PROGRESS))
                    viewHolder.statusTxt.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                else
                    viewHolder.statusTxt.setBackgroundColor(getResources().getColor(R.color.correctAns));
            }

            @NonNull
            @Override
            public SuggestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggested_item, parent, false);
                return new SuggestedViewHolder(itemView);
            }

            @Override
            public int getItemCount() {
                if (super.getItemCount()>0)
                    emptyText.setVisibility(View.GONE);
                return super.getItemCount();
            }

        };
    }



    @Override
    public void onStop() {
        if(adapter!=null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.BackToMainMenu(this, FirebaseAuth.getInstance());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        adapter.getRef(position).removeValue();
        adapter.notifyDataSetChanged();
    }
}

