package com.toyota.authservice.Entity;


import com.toyota.authservice.Enum.EnumRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "enum_role")
    private EnumRole enumName;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EnumRole getEnumName() {
        return enumName;
    }

    public void setEnumName(EnumRole name) {
        this.enumName = name;
    }
}