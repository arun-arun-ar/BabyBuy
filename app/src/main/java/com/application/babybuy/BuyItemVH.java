package com.application.babybuy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BuyItemVH extends RecyclerView.ViewHolder {
    public TextView txt_title,txt_price;
    public ImageView opt_btn;

    public BuyItemVH(@NonNull View itemView) {
        super(itemView);

        txt_title = itemView.findViewById(R.id.txt_title);

        txt_price = itemView.findViewById(R.id.txt_price);

        opt_btn= itemView.findViewById(R.id.img_option);

    }
}
