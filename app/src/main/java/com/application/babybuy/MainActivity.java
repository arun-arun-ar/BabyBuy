package com.application.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    BuyItemDAO dao;
    private SharedPreferences sharedPreferences;
    FirebaseRecyclerAdapter adapt;
    private ImageView vert_btn;
    private TextView textView;
    private androidx.appcompat.widget.AppCompatButton add_item_btn;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // buy product activity
        add_item_btn = findViewById(R.id.add_item_btn);
        add_item_btn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BuyItemActivity.class));
        });

        // Top menu and options
        vert_btn = findViewById(R.id.vert_btn);
        vert_btn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, vert_btn);
            popupMenu.inflate(R.menu.menue_profile);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.account:
                        Intent intent = new Intent(this, UserAccount.class);
                        startActivity(intent);
                        break;

                    case R.id.exit:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("user_login", false).apply();

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, loginActivity.class));
                        this.finish();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        // firebase database authentication
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("user_login", false).apply();
            startActivity(new Intent(this, SplashActivity.class));
        }
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        textView = findViewById(R.id.textView);
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserActivity userActivityProfile = snapshot.getValue(UserActivity.class);
                if (userActivityProfile != null) {
                    String userName = userActivityProfile.userName;
                    textView.setText(userName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error in loading user", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        dao = new BuyItemDAO();
        FirebaseRecyclerOptions<BuyItem> option =
                new FirebaseRecyclerOptions.Builder<BuyItem>()
                        .setQuery(dao.get().orderByChild("userId").equalTo(userId), new SnapshotParser<BuyItem>() {
                            @NonNull
                            @Override
                            public BuyItem parseSnapshot(@NonNull DataSnapshot snapshot) {
                                BuyItem buyItem = snapshot.getValue(BuyItem.class);
                                buyItem.setKey(snapshot.getKey());
                                return buyItem;
                            }
                        }).build();


        //recycler view
        setRecyclerView(option);
        recyclerView.setAdapter(adapt);

    }

    private void setRecyclerView(FirebaseRecyclerOptions<BuyItem> option) {
        adapt = new FirebaseRecyclerAdapter(option) {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_product, parent, false);
                return new BuyItemVH(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i, @NonNull Object o) {
                BuyItemVH vh = (BuyItemVH) holder;
                BuyItem buyItem = (BuyItem) o;

                vh.txt_title.setText(buyItem.getTitle());
                vh.txt_price.setText(buyItem.getPrice());

                vh.opt_btn.setOnClickListener(v -> {

                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, vh.opt_btn);
                    popupMenu.inflate(R.menu.menue_product);

                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent intent = new Intent(MainActivity.this, BuyItemActivity.class);
                                intent.putExtra("EDIT", buyItem);
                                MainActivity.this.startActivity(intent);
                                break;

                            case R.id.menu_remove:
                                BuyItemDAO dao = new BuyItemDAO();
                                dao.remove(buyItem.getKey()).addOnSuccessListener(suc -> {
                                    Toast.makeText(MainActivity.this, "Product removed successfully. ", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(er -> {
                                    Toast.makeText(MainActivity.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                                break;

                            case R.id.menu_share:
                                String ShareText = "Name: " + buyItem.getTitle() + "\nPrice: " + buyItem.getPrice();

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, ShareText);
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                                break;

                        }
                        return false;
                    });
                    popupMenu.show();
                });



                vh.txt_title.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, itemViewActivity.class);
                    intent.putExtra("VIEW", buyItem);
                    MainActivity.this.startActivity(intent);
                    finish();
                });

            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapt.startListening();
    }



}