package com.toyota.productservice.Entity;


import jakarta.persistence.*;

@Entity
@Table(name = "campaigns")
public class Campaign {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title",length = 100)
    private String title;

    @Column(name = "discount_percentage")
    private float discountPercentage;

    @Column(name = "is_percentage_active")
    private boolean isPercentageActive=Boolean.FALSE;

    @Column(name ="is_one_free_active")
    private boolean isOneFreeActive=Boolean.FALSE;

    public Campaign() {
    }

    public Campaign(String title, float discountPercentage, boolean isPercentageActive, boolean isOneFreeActive) {
        this.title = title;
        this.discountPercentage = discountPercentage;
        this.isPercentageActive = isPercentageActive;
        this.isOneFreeActive = isOneFreeActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isPercentageActive() {
        return isPercentageActive;
    }

    public void setPercentageActive(boolean percentageActive) {
        isPercentageActive = percentageActive;
    }

    public boolean isOneFreeActive() {
        return isOneFreeActive;
    }

    public void setOneFreeActive(boolean oneFreeActive) {
        isOneFreeActive = oneFreeActive;
    }
}
