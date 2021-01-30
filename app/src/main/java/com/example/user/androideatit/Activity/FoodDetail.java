package com.example.user.androideatit.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.user.androideatit.Common.Common;
import com.example.user.androideatit.Database.Database;
import com.example.user.androideatit.Model.Food;
import com.example.user.androideatit.Model.Order;
import com.example.user.androideatit.Model.Rating;
import com.example.user.androideatit.R;
import com.example.user.androideatit.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import info.hoang8f.widget.FButton;


public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton  btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    FirebaseDatabase database;
    DatabaseReference ratingTb1;
    DatabaseReference foods;
    Food currentFood;

    FButton btnShowComment;

    RatingBar ratingBar;

    String foodId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTb1 = database.getReference("Rating");

        //Init View
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (CounterFab) findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton) findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);

        btnShowComment = findViewById(R.id.btnShowComment);
        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodDetail.this, ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                ));
                Toast.makeText(FoodDetail.this, "Add to cart", Toast.LENGTH_LONG).show();
                Log.d("bipul", "btnCart");

            }
        });

        btnCart.setCount(new Database(this).getCountCart());

        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_image = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get food ID from Intent
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            if (Common.isConnectToInternet(getBaseContext())){
                getDetailFood(foodId);
                getRatingFood(foodId);
            }

            else {
                Toast.makeText(FoodDetail.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getRatingFood(String foodId) {
        com.google.firebase.database.Query foodRating = ratingTb1.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {

            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count !=0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {


        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.rating_hint_text)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.rating_backround)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                //Set Image
                //Picasso.get().load(currentFood.getImage()).into(food_image);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {

        //Get Rating and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId, String.valueOf(value), comments);

        ratingTb1.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "Thank you for submit rating !!!", Toast.LENGTH_SHORT).show();
                    }
                });

      /*  ratingTb1.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {

                    //Remove old value (you can delete or let it be - useless function)
                    ratingTb1.child(Common.currentUser.getPhone()).removeValue();
                    //Update new Value
                    ratingTb1.child(Common.currentUser.getPhone()).setValue(rating);
                } else {
                    //Update new Value
                    ratingTb1.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this, "Thank you for submit rating !!!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
    }

    public void backPressed(View view) {
        startActivity(new Intent(FoodDetail.this,FoodList.class));
        //finish();
    }
}
