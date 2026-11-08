package com.gift_order.jpa.controller;

import com.gift_order.jpa.model.Vip;
import com.gift_order.jpa.repository.GiftOrderRepository;
import com.gift_order.jpa.repository.VipRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VipController {
    private final VipRepository vipRepository;
    private final GiftOrderRepository giftOrderRepository;

    public VipController(VipRepository vipRepository,
                         GiftOrderRepository giftOrderRepository) {
        this.vipRepository = vipRepository;
        this.giftOrderRepository = giftOrderRepository;
    }

    @GetMapping("/vips")
    public String listVips(@RequestParam(required = false) Long edit, Model model) {
        var vips = vipRepository.findAllByInColdPalaceFalseOrderByIdAsc();
        model.addAttribute("vips", vips);
        Vip vipForm = edit == null
                ? new Vip()
                : vipRepository.findById(edit)
                        .filter(vip -> !vip.isInColdPalace())
                        .orElseGet(Vip::new);
        model.addAttribute("vipForm", vipForm);
        model.addAttribute("editing", vipForm.getId() != null);
        return "vip_list";
    }

    @GetMapping("/vips/cold-palace")
    public String listColdPalace(Model model) {
        model.addAttribute("vips", vipRepository.findAllByInColdPalaceTrueOrderByIdAsc());
        return "cold_palace_list";
    }

    @PostMapping("/vips/cold-palace/save")
    public String saveColdPalaceVip(@RequestParam Long id,
                                    @RequestParam String name,
                                    @RequestParam(required = false) String title,
                                    @RequestParam(required = false) String phone,
                                    @RequestParam(required = false) String mobile,
                                    @RequestParam(required = false) String address,
                                    @RequestParam(required = false) String notes,
                                    @RequestParam(required = false) String deliveryMethod,
                                    @RequestParam(required = false) String deliveryMethodOther,
                                    RedirectAttributes redirectAttributes) {
        Vip vip = vipRepository.findById(id).orElse(null);
        if (vip == null || !vip.isInColdPalace()) {
            redirectAttributes.addFlashAttribute("errorMessage", "找不到這位冷宮貴賓，名單可能已更新。");
            return "redirect:/vips/cold-palace";
        }
        if (name.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "貴賓姓名為必填欄位。");
            return "redirect:/vips/cold-palace";
        }
        if ("其他".equals(deliveryMethod) && (deliveryMethodOther == null || deliveryMethodOther.isBlank())) {
            redirectAttributes.addFlashAttribute("errorMessage", "請填寫其他配送方式。");
            return "redirect:/vips/cold-palace";
        }

        vip.setName(name.trim());
        vip.setTitle(title == null || title.isBlank() ? null : title.trim());
        vip.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        vip.setMobile(mobile == null || mobile.isBlank() ? null : mobile.trim());
        vip.setAddress(address == null || address.isBlank() ? null : address.trim());
        vip.setNotes(notes == null || notes.isBlank() ? null : notes.trim());
        vip.setDeliveryMethod(deliveryMethod == null || deliveryMethod.isBlank() ? null : deliveryMethod.trim());
        vip.setDeliveryMethodOther("其他".equals(deliveryMethod) ? deliveryMethodOther.trim() : null);
        vipRepository.save(vip);
        redirectAttributes.addFlashAttribute("successMessage", "冷宮貴賓的備註與配送方式已儲存。");
        return "redirect:/vips/cold-palace";
    }

    @PostMapping("/vips/save")
    public String saveVip(@ModelAttribute("vipForm") Vip vip, RedirectAttributes redirectAttributes) {
        if (vip.getName() == null || vip.getName().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "請填寫貴賓姓名。");
            return "redirect:/vips";
        }
        if ("其他".equals(vip.getDeliveryMethod())) {
            if (vip.getDeliveryMethodOther() == null || vip.getDeliveryMethodOther().isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "請填寫其他配送方式。");
                return "redirect:/vips";
            }
            vip.setDeliveryMethodOther(vip.getDeliveryMethodOther().trim());
        } else {
            vip.setDeliveryMethodOther(null);
        }
        if (vip.getNotes() != null) {
            String notes = vip.getNotes().trim();
            vip.setNotes(notes.isEmpty() ? null : notes);
        }

        Vip savedVip = vip;
        if (vip.getId() != null) {
            savedVip = vipRepository.findById(vip.getId()).orElse(null);
            if (savedVip == null || savedVip.isInColdPalace()) {
                redirectAttributes.addFlashAttribute("errorMessage", "找不到這位貴賓，名冊可能已更新。");
                return "redirect:/vips";
            }
            savedVip.setName(vip.getName());
            savedVip.setTitle(vip.getTitle());
            savedVip.setPhone(vip.getPhone());
            savedVip.setMobile(vip.getMobile());
            savedVip.setAddress(vip.getAddress());
            savedVip.setNotes(vip.getNotes());
            savedVip.setDeliveryMethod(vip.getDeliveryMethod());
            savedVip.setDeliveryMethodOther(vip.getDeliveryMethodOther());
        }
        savedVip.setName(savedVip.getName().trim());
        savedVip = vipRepository.save(savedVip);
        if (vip.getId() != null) {
            Long savedId = savedVip.getId();
            Long nextVipId = vipRepository.findAllByInColdPalaceFalseOrderByIdAsc().stream()
                    .map(Vip::getId)
                    .filter(id -> id > savedId)
                    .findFirst()
                    .orElse(null);
            if (nextVipId != null) {
                redirectAttributes.addFlashAttribute("successMessage", "貴賓資料已儲存，請接續編輯下一位。");
                return "redirect:/vips?edit=" + nextVipId;
            }
        }
        redirectAttributes.addFlashAttribute("successMessage", "貴賓資料已儲存。");
        return "redirect:/vips";
    }

    @PostMapping("/vips/cold-palace/ban")
    @Transactional
    public String moveToColdPalace(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Vip vip = vipRepository.findById(id).orElse(null);
        if (vip == null || vip.isInColdPalace()) {
            redirectAttributes.addFlashAttribute("errorMessage", "找不到這位貴賓，名冊可能已更新。");
            return "redirect:/vips";
        }

        vip.setInColdPalace(true);
        vipRepository.save(vip);
        var giftOrders = giftOrderRepository.findAllByVipId(id);
        giftOrders.forEach(order -> order.setSelected(false));
        giftOrderRepository.saveAll(giftOrders);
        redirectAttributes.addFlashAttribute("successMessage", "貴賓已移入冷宮名單，資料仍會保留。");
        return "redirect:/vips";
    }

    @PostMapping("/vips/cold-palace/restore")
    @Transactional
    public String restoreFromColdPalace(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Vip vip = vipRepository.findById(id).orElse(null);
        if (vip == null || !vip.isInColdPalace()) {
            redirectAttributes.addFlashAttribute("errorMessage", "找不到這位冷宮貴賓，名單可能已更新。");
            return "redirect:/vips/cold-palace";
        }

        vip.setInColdPalace(false);
        vipRepository.save(vip);
        redirectAttributes.addFlashAttribute("successMessage", "貴賓已移回貴賓名冊。");
        return "redirect:/vips/cold-palace";
    }
}
