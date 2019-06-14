package com.lpwoowatpokpt.quiztemplate.RecycleAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lpwoowatpokpt.quiztemplate.Model.User;
import com.lpwoowatpokpt.quiztemplate.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemAvatarAdapter extends RecyclerView.Adapter<ItemAvatarAdapter.ViewHolder>{


    private List<User> userList;

    public ItemAvatarAdapter(List<User> userList) {
        this.userList = userList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.useravatar_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Picasso.get().load(userList.get(position).getPhotoUrl()).into(viewHolder.randomAvatar);
        if (userList.get(position).getUserName()!=null&&userList.get(position).getUserName().contains(" ")){
            String[] name = userList.get(position).getUserName().split(" ");
            viewHolder.randomUserName.setText(name[0]);
        }


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView randomAvatar;
        TextView randomUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            randomAvatar = itemView.findViewById(R.id.user_avatar);
            randomUserName = itemView.findViewById(R.id.user_name);

        }
    }
}
