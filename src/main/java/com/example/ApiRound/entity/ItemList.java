package com.example.ApiRound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_list")
@Data
public class ItemList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "subregion")
    private String subregion;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "homepage")
    private String homepage;
    
    @Column(name = "coord_x")
    private Double coordX;
    
    @Column(name = "coord_y")
    private Double coordY;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ItemList() {}
    
    public ItemList(String name, String region, String subregion, String address, 
                   String phone, String homepage, Double coordX, Double coordY, String category) {
        this.name = name;
        this.region = region;
        this.subregion = subregion;
        this.address = address;
        this.phone = phone;
        this.homepage = homepage;
        this.coordX = coordX;
        this.coordY = coordY;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
   
}
