package com.lpwoowatpokpt.quiztemplate.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lpwoowatpokpt.quiztemplate.Interface.ItemClickListener;
import com.lpwoowatpokpt.quiztemplate.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView categotyTxt;
    public ImageView backgroundImg;

    private ItemClickListener itemClickListener;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categotyTxt = itemView.findViewById(R.id.category_name_txt);
        backgroundImg = itemView.findViewById(R.id.background_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
