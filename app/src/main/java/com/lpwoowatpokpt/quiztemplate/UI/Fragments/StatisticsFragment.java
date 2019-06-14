package com.lpwoowatpokpt.quiztemplate.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Model.QuestionScore;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.SuggestedListActivity;
import com.lpwoowatpokpt.quiztemplate.ViewHolder.ScoreDetailViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

    View myFragment;

    DatabaseReference questions_list;

    Query games;

    RecyclerView score_list;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<QuestionScore> options;
    FirebaseRecyclerAdapter<QuestionScore, ScoreDetailViewHolder> adapter;

    TextView emptyTxt;

    CardView suggestedCard;

    public static StatisticsFragment newinstance()
    {
        return new StatisticsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questions_list = Common.getDatabase().getReference(Common.QUESTION_SCORE);

        games = questions_list.limitToLast(15);
        questions_list.keepSynced(true);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragment = inflater.inflate(R.layout.fragment_statistics, container, false);

        emptyTxt = myFragment.findViewById(R.id.empty_recyclerTxt);

        score_list = myFragment.findViewById(R.id.recycler_statistics);
        score_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        score_list.setLayoutManager(layoutManager);

        LoadUserResults(Common.currentUser.getDisplayName());

        suggestedCard = myFragment.findViewById(R.id.suggestedQuestionCard);

        suggestedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SuggestedListActivity.class);
                startActivity(intent);
            }
        });

        return myFragment;
    }

    private void LoadUserResults(String displayName) {
        options = new FirebaseRecyclerOptions.Builder<QuestionScore>()
                .setQuery(games.orderByChild("user").equalTo(displayName), QuestionScore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<QuestionScore, ScoreDetailViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ScoreDetailViewHolder viewHolder, int position, @NonNull QuestionScore model) {
                viewHolder.txtScore.setText(model.getScore());
                viewHolder.txtCategory.setText(model.getCategoryName());
                viewHolder.txtTimestamp.setText(model.getTimeStamp());
                viewHolder.txtMode.setText(model.getMode());
            }

            @NonNull
            @Override
            public ScoreDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.score_detail_item, parent,false);
                return new ScoreDetailViewHolder(itemView);
            }

            @Override
            public int getItemCount() {
                if (super.getItemCount()>0)
                    emptyTxt.setVisibility(View.GONE);
                return super.getItemCount();
            }
        };
        score_list.setAdapter(adapter);

    }

}
