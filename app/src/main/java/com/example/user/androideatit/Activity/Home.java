package com.example.user.androideatit.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.user.androideatit.Common.Common;
import com.example.user.androideatit.Database.Database;
import com.example.user.androideatit.Interface.ItemClickListener;
import com.example.user.androideatit.Model.Banner;
import com.example.user.androideatit.Model.Category;
import com.example.user.androideatit.Model.Rating;
import com.example.user.androideatit.Model.Token;
import com.example.user.androideatit.R;
import com.example.user.androideatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;

    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    CounterFab fab;

    //Slider
    HashMap<String, String> image_list;
    SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //view
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectToInternet(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(getBaseContext(), "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if (Common.isConnectToInternet(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(getBaseContext(), "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");


        //make sure you move this function after database is getInstance
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {

                holder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.imageView);
                // Picasso.get().load(model.getImage()).into(viewHolder.imageView);

                final Category clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //  Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_SHORT).show();

                        //Get CategoryId and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        //Because CategoryId is key, so we just get the key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item, viewGroup, false);

                return new MenuViewHolder(itemView);
            }
        };



        //init paper
        Paper.init(this);


        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        //Load menu
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));

        updateToken(FirebaseInstanceId.getInstance().getToken());



        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fall_down);

        recycler_menu.setLayoutAnimation(controller);

        //setup slider
        //Need call this function after you init database firebase
        setupSlider();

    }

    private void setupSlider() {
        mSlider = findViewById(R.id.slider);
        image_list = new HashMap<>();

        final DatabaseReference banners = database.getReference("Banner");
         banners.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                     Banner banner = postSnapShot.getValue(Banner.class);
                     image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());

                 }
                 for (String key:image_list.keySet()){

                     String[] keySplit  = key.split("@@@");
                     String nameOfFood = keySplit[0];
                     String idOfFood = keySplit[1];



                     //Create slider
                     final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                     textSliderView
                             .description(nameOfFood)
                             .image(image_list.get(key))
                             .setScaleType(BaseSliderView.ScaleType.Fit)
                             .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                 @Override
                                 public void onSliderClick(BaseSliderView slider) {
                                     Intent  intent = new Intent(Home.this,FoodDetail.class);
                                     //We will send food id to FoodDetail
                                     intent.putExtras(textSliderView.getBundle());
                                     startActivity(intent);
                                 }
                             });

                     //Add extra bundle
                     textSliderView.bundle(new Bundle());
                     textSliderView.getBundle().putString("FoodId",idOfFood);

                     mSlider.addSlider(textSliderView);

                     //remove event after finish
                     banners.removeEventListener(this);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

         mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
         mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
         mSlider.setCustomAnimation(new DescriptionAnimation());
         mSlider.setDuration(4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
        if (adapter != null) {
            adapter.startListening();
        }
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);//false because this token send client app
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());

    }*/

    private void loadMenu() {
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        mSlider.stopAutoCycle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            loadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, Cart.class));

        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this, OrderStatus.class));

        } else if (id == R.id.nav_logout) {
            //Delete Remember user and password
            Paper.book().destroy();

            //Logout
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        } else if (id == R.id.nav_change_pwd) {
            showChangePassword();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePassword() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);
        alertDialog.setView(layout_pwd);

        final MaterialEditText edtPassword = layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = layout_pwd.findViewById(R.id.edtRepeatPassword);


        //Button
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //change password here

                //For use SpotsDialog, use AlertDialog from android.app and library version- 0.7aar
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                //check old password
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())) {

                    //check new password and repeat password
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {

                        Map<String, Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password", edtNewPassword.getText().toString());

                        //Make update

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, "Password was update", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

}
