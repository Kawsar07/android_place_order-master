package com.example.user.androideatit.Model;

/**
 * Created by 123456 on 2018/11/19.
 */

public class Order {
    private int Id;
    private String ProductId;
    private String ProductName;
    private String Quanlity;
    private String Price;
    private String Discount;
    private String Image;

    public Order() {
    }

    public Order(String productId, String productName, String quanlity, String price, String discount,String image) {
        ProductId = productId;
        ProductName = productName;
        Quanlity = quanlity;
        Price = price;
        Discount = discount;
        Image = image;
    }


    public Order(int id, String productId, String productName, String quanlity, String price, String discount,String image) {
        Id = id;
        ProductId = productId;
        ProductName = productName;
        Quanlity = quanlity;
        Price = price;
        Discount = discount;
        Image = image;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuanlity() {
        return Quanlity;
    }

    public void setQuanlity(String quanlity) {
        Quanlity = quanlity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
