package com.example.user.androideatit.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.user.androideatit.Activity.Cart;
import com.example.user.androideatit.Database.Database;
import com.example.user.androideatit.Interface.ItemClickListener;
import com.example.user.androideatit.Model.Order;
import com.example.user.androideatit.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name,txt_price;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name){
        this.txt_cart_name = txt_cart_name;
    }


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_Price);
        btn_quantity = itemView.findViewById(R.id.btn_quantity);
        cart_image = itemView.findViewById(R.id.cart_image);

       // itemView.setOnCreateContextMenuListener(this);


    }

    @Override
    public void onClick(View v) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart){
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {

        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_image);


       holder.btn_quantity.setNumber(listData.get(position).getQuanlity());
       holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
           @Override
           public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
               Order order = listData.get(position);
               order.setQuanlity(String.valueOf(newValue));
               new Database(cart).updateCart(order);

               //Update text total
               //Calculate total price
               int total = 0;
               List<Order> orders = new Database(cart).getCarts();
               for(Order item:orders)
                   total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(item.getQuanlity()));
               Locale locale = new Locale("en","US");
               NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
               cart.txtTotalPrice.setText(fmt.format(total));
           }
       });

        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuanlity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {

        return listData.size();
    }
}
