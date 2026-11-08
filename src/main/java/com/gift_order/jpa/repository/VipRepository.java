package com.gift_order.jpa.repository;

import com.gift_order.jpa.model.Vip;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VipRepository extends JpaRepository<Vip, Long> {
    List<Vip> findAllByInColdPalaceFalseOrderByIdAsc();
    List<Vip> findAllByInColdPalaceTrueOrderByIdAsc();
}
