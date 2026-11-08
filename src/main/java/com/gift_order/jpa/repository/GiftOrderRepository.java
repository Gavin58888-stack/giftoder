package com.gift_order.jpa.repository;

import com.gift_order.jpa.model.GiftOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiftOrderRepository extends JpaRepository<GiftOrder, Long> {
    @Override
    @EntityGraph(attributePaths = "vip")
    java.util.List<GiftOrder> findAll();

    // 透過 vipId 查詢送禮紀錄
    Optional<GiftOrder> findByVipId(Long vipId);

    java.util.List<GiftOrder> findAllByVipId(Long vipId);
}
