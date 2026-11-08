package com.gift_order.jpa.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "gift_orders")
public class GiftOrderDocument {
    @Id
    private String id;
    private Long vipId;
    private String vipName;
    private String vipTitle;
    private String phone;
    private String mobile;
    private String address;
    private String deliveryMethod;
    private String giftItem;
    private boolean selected;
    private int quantity;

    public GiftOrderDocument() {}

    public static GiftOrderDocument from(GiftOrder order) {
        GiftOrderDocument document = new GiftOrderDocument();
        document.setId(order.getId().toString());
        document.setGiftItem(java.util.stream.Stream.of(
                        giftDetail(order.getGiftItem(), order.getQuantity()),
                        giftDetail(order.getGiftItem2(), order.getQuantity2()),
                        giftDetail(order.getGiftItem3(), order.getQuantity3()))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.joining(" / ")));
        document.setSelected(order.isSelected());
        document.setQuantity(order.getQuantity() + order.getQuantity2() + order.getQuantity3());
        if (order.getVip() != null) {
            Vip vip = order.getVip();
            document.setVipId(vip.getId());
            document.setVipName(vip.getName());
            document.setVipTitle(vip.getTitle());
            document.setPhone(vip.getPhone());
            document.setMobile(vip.getMobile());
            document.setAddress(vip.getAddress());
            document.setDeliveryMethod(vip.getDeliveryMethodDisplay());
        }
        return document;
    }

    private static String giftDetail(String item, int quantity) {
        if (quantity <= 0) return null;
        String itemName = item == null || item.isBlank() ? "未設定品項" : item;
        return itemName + " × " + quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getVipId() { return vipId; }
    public void setVipId(Long vipId) { this.vipId = vipId; }
    public String getVipName() { return vipName; }
    public void setVipName(String vipName) { this.vipName = vipName; }
    public String getVipTitle() { return vipTitle; }
    public void setVipTitle(String vipTitle) { this.vipTitle = vipTitle; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public String getGiftItem() { return giftItem; }
    public void setGiftItem(String giftItem) { this.giftItem = giftItem; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
