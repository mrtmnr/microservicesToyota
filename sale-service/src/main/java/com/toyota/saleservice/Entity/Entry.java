package com.toyota.saleservice.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "entries")
public class Entry {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_price")
    private float totalPrice;

    @Column(name = "campaign_active")
    private boolean campaignActive;

    public Entry() {
    }

    public Entry(int quantity, float totalPrice, boolean campaignActive) {
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.campaignActive = campaignActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isCampaignActive() {
        return campaignActive;
    }

    public void setCampaignActive(boolean campaignActive) {
        this.campaignActive = campaignActive;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", campaignActive=" + campaignActive +
                '}';
    }
}
