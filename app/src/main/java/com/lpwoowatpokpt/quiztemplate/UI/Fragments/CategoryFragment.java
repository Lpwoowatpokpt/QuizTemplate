package com.lpwoowatpokpt.quiztemplate.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lpwoowatpokpt.quiztemplate.Common.Common;
import com.lpwoowatpokpt.quiztemplate.Interface.ItemClickListener;
import com.lpwoowatpokpt.quiztemplate.Model.Category;
import com.lpwoowatpokpt.quiztemplate.R;
import com.lpwoowatpokpt.quiztemplate.UI.CreateModeActivity;
import com.lpwoowatpokpt.quiztemplate.UI.MultiplayerActivity;
import com.lpwoowatpokpt.quiztemplate.UI.StartActivity;
import com.lpwoowatpokpt.quiztemplate.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

public class CategoryFragment extends Fragment implements View.OnClickListener {

    View myFragment;

    DatabaseReference category, users;
    private static final String CATEGORY = "Category";

    LinearLayout additionalActivities;
    CardView cardMultiplayer, cardCreateMode;

    RecyclerView category_list;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerOptions<Category> options;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder>adapter;

    public static CategoryFragment newinstance()
    {
        return new CategoryFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = Common.getDatabase().getReference(Common.USERS);
        category = Common.getDatabase().getReference(CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_category, container, false);

        additionalActivities = myFragment.findViewById(R.id.layoutAdditionalActivities);

        if (Common.currentUser==null)
            additionalActivities.setVisibility(View.GONE);
        else
            additionalActivities.setVisibility(View.VISIBLE);

        cardMultiplayer = myFragment.findViewById(R.id.cardMultiplayer);
        cardCreateMode = myFragment.findViewById(R.id.cardCreateMode);

        cardMultiplayer.setOnClickListener(this);
        cardCreateMode.setOnClickListener(this);

        category_list = myFragment.findViewById(R.id.recycler_category);
        category_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        category_list.setLayoutManager(layoutManager);

        PopulateCategoryList();

        adapter.startListening();

        return myFragment;
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

    private void PopulateCategoryList() {
        options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull final Category model) {
                holder.categotyTxt.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.backgroundImg);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent startGame = new Intent(getContext(), StartActivity.class);
                        Common.categoryId = adapter.getRef(position).getKey();
                        Common.categoryName = model.getName();
                        Bundle dataSend = new Bundle();
                        dataSend.putString("name", model.getName());
                        startGame.putExtras(dataSend);
                        startActivity(startGame);
                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
               View itemView = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.category_item, parent,false);
                return new CategoryViewHolder(itemView);
            }
        };
        category_list.setAdapter(adapter);
        category_list.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardMultiplayer:
                if (Common.isConnectedToInternet(getContext())){
                    Intent i = new Intent(this.getActivity(), MultiplayerActivity.class);
                    startActivity(i);
                }else
                    Common.ShowToast(getContext(), getString(R.string.no_internet));

                break;
            case R.id.cardCreateMode:
                Intent intent = new Intent(this.getActivity(), CreateModeActivity.class);
                startActivity(intent);
                break;
        }
    }


}
