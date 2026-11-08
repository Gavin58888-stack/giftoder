package com.gift_order.jpa.model;

import java.util.List;

public class GiftSummaryPerson {
    private final Vip vip;
    private final List<GiftSummaryItem> gifts;

    public GiftSummaryPerson(Vip vip, List<GiftSummaryItem> gifts) {
        this.vip = vip;
        this.gifts = List.copyOf(gifts);
    }

    public Vip getVip() { return vip; }
    public List<GiftSummaryItem> getGifts() { return gifts; }
    public int getTotalQuantity() { return gifts.stream().mapToInt(GiftSummaryItem::getQuantity).sum(); }
}
