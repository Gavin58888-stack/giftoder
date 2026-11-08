package com.gift_order.jpa.model;

public class GiftSummaryItem {
    private final Vip vip;
    private final String giftItem;
    private final int quantity;

    public GiftSummaryItem(Vip vip, String giftItem, int quantity) {
        this.vip = vip;
        this.giftItem = giftItem;
        this.quantity = quantity;
    }

    public Vip getVip() { return vip; }
    public String getGiftItem() { return giftItem; }
    public int getQuantity() { return quantity; }
}
