package com.lpwoowatpokpt.quiztemplate.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lpwoowatpokpt.quiztemplate.R;


/**
 * Created by Death on 25/10/2017.
 */

public class ScoreDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txtScore, txtCategory, txtTimestamp, txtMode;

    public ScoreDetailViewHolder(View itemView) {
        super(itemView);
        txtScore = itemView.findViewById(R.id.score);
        txtCategory = itemView.findViewById(R.id.category);
        txtTimestamp = itemView.findViewById(R.id.timestamp);
        txtMode = itemView.findViewById(R.id.mode);
    }
}
