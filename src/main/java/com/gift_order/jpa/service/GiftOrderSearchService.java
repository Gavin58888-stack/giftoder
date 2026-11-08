package com.gift_order.jpa.service;

import com.gift_order.jpa.model.GiftOrder;
import com.gift_order.jpa.model.GiftOrderDocument;
import com.gift_order.jpa.repository.GiftOrderRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftOrderSearchService implements ApplicationRunner {
    private final GiftOrderRepository giftOrderRepository;

    public GiftOrderSearchService(GiftOrderRepository giftOrderRepository) {
        this.giftOrderRepository = giftOrderRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // No-op: the application search is backed by the relational database.
    }

    public List<GiftOrderDocument> search(String query) {
        String keyword = query == null ? "" : query.trim();
        if (keyword.isEmpty()) {
            return List.of();
        }

        String needle = keyword.toLowerCase();
        return giftOrderRepository.findAll().stream()
                .filter(gift -> gift != null)
                .filter(gift -> gift.getVip() != null && !gift.getVip().isInColdPalace())
                .map(GiftOrderDocument::from)
                .filter(document ->
                        contains(document.getVipName(), needle)
                                || contains(document.getVipTitle(), needle)
                                || contains(document.getPhone(), needle)
                                || contains(document.getMobile(), needle)
                                || contains(document.getAddress(), needle)
                                || contains(document.getDeliveryMethod(), needle)
                                || contains(document.getGiftItem(), needle))
                .toList();
    }

    public void index(List<GiftOrder> orders) {
        // Search results are computed directly from the MySQL data, so no ES index sync is needed.
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}
