package com.lpwoowatpokpt.quiztemplate.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lpwoowatpokpt.quiztemplate.R;

public class SuggestedViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout viewForeground;
    public TextView questionNumberTxt, quesionTxt, statusTxt;
    public ImageView questionImagePreview;


    public SuggestedViewHolder(@NonNull View itemView) {
        super(itemView);
        viewForeground = itemView.findViewById(R.id.view_foreground);
        questionNumberTxt = itemView.findViewById(R.id.questionNumber);
        quesionTxt = itemView.findViewById(R.id.questionText);
        statusTxt = itemView.findViewById(R.id.statusText);
        questionImagePreview = itemView.findViewById(R.id.imageQuestionBackground);
    }
}
