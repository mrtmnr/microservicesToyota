package com.toyota.saleservice.Entity;

import com.toyota.saleservice.Enum.EnumPayment;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String username;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sale_id")
    private List<Entry>entries;

    @Column(name = "total_price")
    private float totalPrice;
    @Column(name ="total_received" )
    private float totalReceived;

    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "enum_payment")
    private EnumPayment payment;

    public Sale() {
    }

    public Sale(float totalPrice, float totalReceived, Date date,EnumPayment payment) {
        this.totalPrice = totalPrice;
        this.totalReceived = totalReceived;

        this.date = date;
        this.payment=payment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
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

    public void addEntry(Entry entry){

        if (entries==null){
            entries=new ArrayList<>();
        }

        entries.add(entry);

    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", entries=" + entries +
                ", totalPrice=" + totalPrice +
                ", totalReceived=" + totalReceived +
                ", date=" + date +
                ", payment=" + payment +
                '}';
    }
}
