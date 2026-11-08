package com.gift_order.jpa.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Audited;

@Entity
@Table(name = "gift_order")
public class GiftOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多對一關聯：多筆送禮紀錄可以對應到一位貴賓
    @ManyToOne
    @JoinColumn(name = "vip_id")
    private Vip vip;

    private String giftItem = "蘋果禮盒"; // 預設送禮品項
    @Column(name = "gift_item_2", length = 100)
    private String giftItem2;
    @Column(name = "gift_item_3", length = 100)
    private String giftItem3;
    @Column(name = "is_selected")
    private boolean selected = false;      // 是否勾選
    private int quantity = 0;              // 第一種禮品數量
    @Column(name = "quantity_2")
    private int quantity2;
    @Column(name = "quantity_3")
    private int quantity3;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vip getVip() { return vip; }
    public void setVip(Vip vip) { this.vip = vip; }
    public String getGiftItem() { return giftItem; }
    public void setGiftItem(String giftItem) { this.giftItem = giftItem; }
    public String getGiftItem2() { return giftItem2; }
    public void setGiftItem2(String giftItem2) { this.giftItem2 = giftItem2; }
    public String getGiftItem3() { return giftItem3; }
    public void setGiftItem3(String giftItem3) { this.giftItem3 = giftItem3; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity2() { return quantity2; }
    public void setQuantity2(int quantity2) { this.quantity2 = quantity2; }
    public int getQuantity3() { return quantity3; }
    public void setQuantity3(int quantity3) { this.quantity3 = quantity3; }
}
