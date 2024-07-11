package com.toyota.saleservice.Entity;

import com.toyota.saleservice.Enum.EnumPayment;
import jakarta.persistence.*;
import java.util.Date;


@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id")
    Checkout checkout;

    private String username;

    @Column(name ="total_received" )
    private float totalReceived;

    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "enum_payment")
    private EnumPayment payment;

    public Sale() {
    }

    public Sale(String username, float totalReceived, Date date, EnumPayment payment) {
        this.username = username;
        this.totalReceived = totalReceived;
        this.date = date;
        this.payment = payment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }


    public float getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(float totalReceived) {
        this.totalReceived = totalReceived;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EnumPayment getPayment() {
        return payment;
    }

    public void setPayment(EnumPayment payment) {
        this.payment = payment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", totalReceived=" + totalReceived +
                ", date=" + date +
                ", payment=" + payment +
                '}';
    }
}
