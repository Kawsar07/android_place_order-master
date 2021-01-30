package com.example.user.androideatit.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.androideatit.Common.Common;
import com.example.user.androideatit.Model.Rating;
import com.example.user.androideatit.R;
import com.example.user.androideatit.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference ratingTb1;
    SwipeRefreshLayout mSwipeRefreshLayout;

    String foodId = "";

    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().build());
        setContentView(R.layout.activity_show_comment);

        //Firebase
         database = FirebaseDatabase.getInstance();
         ratingTb1 = database.getReference("Rating");
         recyclerView = findViewById(R.id.recyclerComment);
         layoutManager = new LinearLayoutManager(this);
         recyclerView.setLayoutManager(layoutManager);

         //Swipe Lyout
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);

                    if (!foodId.isEmpty() && foodId != null){
                        // Create request query
                        Query query = ratingTb1.orderByChild("foodId").equalTo(foodId);

                        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();

                        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                               holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                               holder.txtComment.setText(model.getComment());
                               holder.txtUserPhone.setText(model.getUserPhone());
                            }

                            @NonNull
                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(viewGroup.getContext())
                                        .inflate(R.layout.show_comment_layout,viewGroup,false);
                                return new ShowCommentViewHolder(view);
                            }
                        };

                        loadComment(foodId);
                    }

            }
        });

        //thead to load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if (getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);

                if (!foodId.isEmpty() && foodId != null){
                    // Create request query
                    Query query = ratingTb1.orderByChild("foodId").equalTo(foodId);

                    FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            holder.txtComment.setText(model.getComment());
                            holder.txtUserPhone.setText(model.getUserPhone());
                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view = LayoutInflater.from(viewGroup.getContext())
                                    .inflate(R.layout.show_comment_layout,viewGroup,false);
                            return new ShowCommentViewHolder(view);
                        }
                    };

                    loadComment(foodId);
                }
            }
        });
    }

    private void loadComment(String foodId) {
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
