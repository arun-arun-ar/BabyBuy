package com.application.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class itemViewActivity extends AppCompatActivity {

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_product_activity);

        BuyItem item = (BuyItem) getIntent().getSerializableExtra("VIEW");

        // get product title
        TextView title = findViewById(R.id.title);
        title.setText(title.getText()+": " + item.getTitle());


        // product image
        imageView = findViewById(R.id.imageView);


        // product price
        TextView price = findViewById(R.id.price);
        price.setText(price.getText()+": " + item.getPrice());

        // product description
        TextView description = findViewById(R.id.description);
        description.setText(description.getText()+": " + item.getDescription());


        // get purchase date
        TextView date = findViewById(R.id.date);
        date.setText(date.getText()+": " + item.getCreated_at());


        // product location
        TextView loc = findViewById(R.id.loc);
        loc.setText(loc.getText()+": lat"+item.getLat()+", lng "+ item.getLng());




        if (item.getPimage() != null) {
            Glide.with(this).load(item.getPimage()).into(imageView);
        }

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener( view -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        TextView gmap = findViewById(R.id.gmap);
        gmap.setOnClickListener(v -> {
            Intent intent = new Intent(itemViewActivity.this, MapsActivity.class);
            intent.putExtra("VIEW", item);
            itemViewActivity.this.startActivity(intent);
            finish();
        });


    }

}