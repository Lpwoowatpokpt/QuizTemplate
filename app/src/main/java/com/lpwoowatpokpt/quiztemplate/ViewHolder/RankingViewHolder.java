package com.lpwoowatpokpt.quiztemplate.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lpwoowatpokpt.quiztemplate.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by death on 10.01.18.
 */

public class RankingViewHolder extends RecyclerView.ViewHolder{

    public TextView txt_name, txt_score, txt_position;
    public CircleImageView userAvatar;

    public RankingViewHolder(View itemView) {
        super(itemView);
        txt_name = itemView.findViewById(R.id.name);
        txt_score = itemView.findViewById(R.id.score);
        txt_position = itemView.findViewById(R.id.number_ranking);

        userAvatar = itemView.findViewById(R.id.user_avatar);

    }

}
