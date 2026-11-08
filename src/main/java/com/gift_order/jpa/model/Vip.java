package com.gift_order.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "vip")
public class Vip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // 貴賓姓名
    private String title;          // 職稱/備註
    private String phone;          // 電話
    private String mobile;         // 手機
    private String address;        // 地址
    @Column(length = 500)
    private String notes;          // 備註
    private String deliveryMethod; // 配送方式
    @Column(name = "delivery_method_other", length = 100)
    private String deliveryMethodOther;
    @Column(name = "in_cold_palace", nullable = false)
    private boolean inColdPalace;

    public Vip() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public String getDeliveryMethodOther() { return deliveryMethodOther; }
    public void setDeliveryMethodOther(String deliveryMethodOther) { this.deliveryMethodOther = deliveryMethodOther; }
    public String getDeliveryMethodDisplay() {
        if ("其他".equals(deliveryMethod) && deliveryMethodOther != null && !deliveryMethodOther.isBlank()) {
            return "其他（" + deliveryMethodOther + "）";
        }
        return deliveryMethod;
    }
    public boolean isInColdPalace() { return inColdPalace; }
    public void setInColdPalace(boolean inColdPalace) { this.inColdPalace = inColdPalace; }
}
