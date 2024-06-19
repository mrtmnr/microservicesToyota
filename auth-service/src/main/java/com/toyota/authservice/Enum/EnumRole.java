package com.toyota.authservice.Enum;

public enum EnumRole {
    CASHIER,
    MANAGER,
    ADMIN;


    public String getRole() {
        return this.name();
    }
}
