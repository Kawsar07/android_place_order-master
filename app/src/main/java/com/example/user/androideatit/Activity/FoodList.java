package com.example.user.androideatit.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.androideatit.Common.Common;
import com.example.user.androideatit.Database.Database;
import com.example.user.androideatit.Interface.ItemClickListener;
import com.example.user.androideatit.Model.Food;
import com.example.user.androideatit.Model.Order;
import com.example.user.androideatit.R;
import com.example.user.androideatit.ViewHolder.FoodViewHolder;
import com.example.user.androideatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference foodList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search Functionality
    FirebaseRecyclerAdapter<Food,FoodViewHolder>searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        //local DB
        localDB = new Database(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Get Intent here
                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null){
                    if (Common.isConnectToInternet(getBaseContext())){
                        loadListFood(categoryId);
                    }
                    else {
                        Toast.makeText(FoodList.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get Intent here
                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null){
                    if (Common.isConnectToInternet(getBaseContext())){
                        loadListFood(categoryId);
                    }
                    else {
                        Toast.makeText(FoodList.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            }
        });



        //Search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
      //  materialSearchBar.setSpeechMode(false);

        loadSuggest();//Write function to load Suggest from Firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user type their text , we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList)//Loop in suggestion list
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()));
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When Search Bar is close
                //Restore original  adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //Show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter !=null){
            adapter.startListening();
        }
    }

    private void startSearch(CharSequence text) {

        Query searchByName = foodList.orderByChild("Name").equalTo(text.toString());

        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();

         searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
             @Override
             protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {

                 viewHolder.food_name.setText(model.getName());
                 Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
               /* Picasso.get().load(model.getImage())
                        .into(viewHolder.food_image);*/

                 final Food local = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                         //start new activity
                         Intent foodDetail =  new Intent(FoodList.this, FoodDetail.class);
                         foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                         startActivity(foodDetail);
                         //   Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                     }
                 });
             }

             @NonNull
             @Override
             public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                 View itemView = LayoutInflater.from(viewGroup.getContext())
                         .inflate(R.layout.food_item,viewGroup,false);
                 return new FoodViewHolder(itemView);
             }
         };
         searchAdapter.startListening();
         recyclerView.setAdapter(searchAdapter);//Set adapter for recycler view is search result
    }

    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshrot:dataSnapshot.getChildren()){
                            Food item = postSnapshrot.getValue(Food.class);
                            suggestList.add(item.getName()); //Add name food to suggestion list
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId){


        Query searchByName = foodList.orderByChild("menuId").equalTo(categoryId);

        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("$ %s",model.getPrice().toString()));

                //image load to viewHolder
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.food_image);

                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                "1",
                                model.getPrice(),
                                model.getDiscount(),
                                model.getImage()
                        ));
                        Toast.makeText(FoodList.this, "Add to cart", Toast.LENGTH_LONG).show();
                        Log.d("bipul", "btnCart");
                    }
                });

                //Add Favorites
                if (localDB.isFavorites(adapter.getRef(position).getKey())){
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                //Click to change state of Favorites
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!localDB.isFavorites(adapter.getRef(position).getKey())){

                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+ " was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+ " was remove favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent foodDetail =  new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                        //   Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.food_item,viewGroup,false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
       // searchAdapter.stopListening();
    }

    public void backPressed(View view) {
          startActivity(new Intent(this, Home.class));
        finish();
    }
}
