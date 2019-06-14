package com.lpwoowatpokpt.quiztemplate.UI.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Interface.RankingCallBack;
import com.lpwoowatpokpt.quiztemplate.Model.QuestionScore;
import com.lpwoowatpokpt.quiztemplate.Model.Ranking;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.ViewHolder.RankingViewHolder;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {

    View myFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerOptions<Ranking>options;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    DatabaseReference questionScore, rankingTbl;
    Query topUsers;

    int sum = 0;

    public static RankingFragment newinstance()
    {
        return new RankingFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionScore = Common.getDatabase().getReference(Common.QUESTION_SCORE);
        questionScore.keepSynced(true);
        rankingTbl = Common.getDatabase().getReference(Common.RANKING);
        topUsers = rankingTbl.limitToLast(100);
        rankingTbl.keepSynced(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingList = myFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);

        if (Common.currentUser != null)
            UpdateScore(Common.currentUser.getDisplayName(),  String.valueOf(Common.currentUser.getPhotoUrl()), new RankingCallBack<Ranking>() {
                @Override
                public void callback(Ranking ranking) {
                    rankingTbl.child(ranking.getUserName())
                            .setValue(ranking);
                }
            });

        PopulateRankingList();

        return myFragment;
    }

    private void PopulateRankingList() {
        options = new FirebaseRecyclerOptions.Builder<Ranking>()
                .setQuery(topUsers.orderByChild("score"), Ranking.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RankingViewHolder viewHolder, int position, @NonNull Ranking model) {
                viewHolder.txt_position.setText(String.valueOf(-position + 100));
                viewHolder.txt_name.setText(model.getUserName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));
                Picasso.get().load(model.getPhotoUrl()).into(viewHolder.userAvatar);
            }

            @NonNull
            @Override
            public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ranking_item, parent,false);
                return new RankingViewHolder(itemView);
            }
        };
        rankingList.setAdapter(adapter);
        rankingList.getAdapter().notifyDataSetChanged();
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

    private void UpdateScore(final String userName, final String photoUrl, final RankingCallBack<Ranking> callBack) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren())
                        {
                            QuestionScore ques = data.getValue(QuestionScore.class);
                            assert ques != null;
                            sum+=Integer.parseInt(ques.getScore());
                        }

                        Ranking ranking = new Ranking(userName, photoUrl, sum);
                        callBack.callback(ranking);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
