package com.toyota.saleservice.Enum;

public enum EnumPayment {
    CASH,
    CARD;

    public String getPayment(){
        return this.name();
    }
}
