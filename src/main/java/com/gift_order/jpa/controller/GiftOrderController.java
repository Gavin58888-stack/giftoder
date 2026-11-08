package com.gift_order.jpa.controller;

import org.springframework.ui.Model;
import com.gift_order.jpa.model.GiftOrder;
import com.gift_order.jpa.model.GiftOrderDocument;
import com.gift_order.jpa.model.GiftSummaryItem;
import com.gift_order.jpa.model.GiftSummaryPerson;
import com.gift_order.jpa.model.Vip;
import com.gift_order.jpa.repository.GiftOrderRepository;
import com.gift_order.jpa.repository.VipRepository;
import com.gift_order.jpa.service.GiftOrderSearchService;
import com.gift_order.jpa.service.GiftReportAiService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/", "/gifts"})
public class GiftOrderController {
    private static final Logger logger = LoggerFactory.getLogger(GiftOrderController.class);

    @Autowired
    private VipRepository vipRepository;

    @Autowired
    private GiftOrderRepository giftRepository;

    @Autowired
    private GiftOrderSearchService searchService;

    @Autowired
    private ObjectProvider<GiftReportAiService> giftReportAiServiceProvider;

    @GetMapping("/secretary-report")
    public String secretaryReport(@RequestParam(required = false) Long vipId,
                                  @RequestParam(defaultValue = "") String report,
                                  @RequestParam(defaultValue = "") String reportError,
                                  Model model) {
        List<Vip> vips = vipRepository.findAllByInColdPalaceFalseOrderByIdAsc();
        Vip selectedVip = vips.stream().filter(vip -> vip.getId().equals(vipId)).findFirst().orElse(null);
        GiftOrder selectedOrder = selectedVip == null ? null : giftRepository.findByVipId(selectedVip.getId()).orElse(null);
        List<GiftSummaryItem> items = selectedOrder == null || !selectedOrder.isSelected() ? List.of() : List.of(
                new GiftSummaryItem(selectedVip, selectedOrder.getGiftItem(), selectedOrder.getQuantity()),
                new GiftSummaryItem(selectedVip, selectedOrder.getGiftItem2(), selectedOrder.getQuantity2()),
                new GiftSummaryItem(selectedVip, selectedOrder.getGiftItem3(), selectedOrder.getQuantity3()))
                .stream().filter(item -> item.getQuantity() > 0 && item.getGiftItem() != null && !item.getGiftItem().isBlank()).toList();
        model.addAttribute("vips", vips);
        model.addAttribute("selectedVip", selectedVip);
        model.addAttribute("giftItems", items);
        model.addAttribute("report", report);
        model.addAttribute("reportError", reportError);
        model.addAttribute("aiAvailable", giftReportAiServiceProvider.getIfAvailable() != null);
        return "secretary_report";
    }

    @PostMapping("/secretary-report/generate")
    public String generateSecretaryReport(@RequestParam Long vipId, Model model) {
        Vip vip = vipRepository.findById(vipId).filter(candidate -> !candidate.isInColdPalace()).orElse(null);
        if (vip == null) return "redirect:/gifts/secretary-report?reportError=" + encode("找不到這位貴賓，請重新選擇。");
        GiftOrder order = giftRepository.findByVipId(vipId).orElse(null);
        List<String> details = new ArrayList<>();
        if (order != null && order.isSelected()) {
            addGiftDetail(details, order.getGiftItem(), order.getQuantity());
            addGiftDetail(details, order.getGiftItem2(), order.getQuantity2());
            addGiftDetail(details, order.getGiftItem3(), order.getQuantity3());
        }
        if (details.isEmpty()) return "redirect:/gifts/secretary-report?vipId=" + vipId + "&reportError=" + encode("這位貴賓尚未勾選送禮或設定有效的禮品數量。");
        GiftReportAiService aiService = giftReportAiServiceProvider.getIfAvailable();
        if (aiService == null) return "redirect:/gifts/secretary-report?vipId=" + vipId + "&reportError=" + encode("尚未設定 GEMINI_API_KEY，請先設定模型金鑰後再生成。");
        try {
            String report = aiService.generate(vip.getName(), String.join("、", details));
            return "redirect:/gifts/secretary-report?vipId=" + vipId + "&report=" + encode(report);
        } catch (Exception ex) {
            logger.warn("AI report generation failed: {}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            return "redirect:/gifts/secretary-report?vipId=" + vipId + "&reportError=" + encode("AI 生成失敗，請稍後重試。" );
        }
    }

    private void addGiftDetail(List<String> details, String item, int quantity) {
        if (item != null && !item.isBlank() && quantity > 0) details.add(item + " × " + quantity + " 份");
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }

    // 1. 載入所有 VIP，並對應其送禮紀錄
    @GetMapping
    public String listGifts(Model model) {
        List<Vip> vips = vipRepository.findAllByInColdPalaceFalseOrderByIdAsc();
        List<GiftOrder> displayList = new ArrayList<>();

        for (Vip vip : vips) {
            // 如果該貴賓已有送禮紀錄就讀取，沒有的話建立一個預設物件
            GiftOrder order = giftRepository.findByVipId(vip.getId())
                    .orElseGet(() -> {
                        GiftOrder newOrder = new GiftOrder();
                        newOrder.setVip(vip);
                        return newOrder;
                    });
            displayList.add(order);
        }

        WrapperGiftOrder wrapper = new WrapperGiftOrder();
        wrapper.setGifts(displayList);
        GiftOrder firstSavedOrder = displayList.stream().filter(order -> order.getId() != null).findFirst().orElse(null);
        if (firstSavedOrder != null) {
            wrapper.setGiftItem1(firstSavedOrder.getGiftItem());
            wrapper.setGiftItem2(firstSavedOrder.getGiftItem2());
            wrapper.setGiftItem3(firstSavedOrder.getGiftItem3());
        } else {
            wrapper.setGiftItem1("蘋果禮盒");
        }

        model.addAttribute("formWrapper", wrapper);
        return "gift_list";
    }
    @PutMapping("/update")
    public String updateGifts(@ModelAttribute WrapperGiftOrder formWrapper) {
        if (formWrapper == null || formWrapper.getGifts() == null) {
            return "redirect:/gifts";
        }

        List<GiftOrder> gifts = formWrapper.getGifts().stream()
                .filter(g -> g != null)
                .peek(g -> {
                    if (g.getVip() == null) {
                        g.setVip(new Vip());
                    }
                    if (g.getQuantity() <= 0) {
                        g.setQuantity(1);
                    }
                })
                .toList();

        List<GiftOrder> savedGifts = giftRepository.saveAll(gifts);
        searchService.index(savedGifts);
        return "redirect:/gifts/summary";
    }

    @DeleteMapping("/delete")
    public String deleteGifts(@ModelAttribute WrapperGiftOrder formWrapper) {
        if (formWrapper == null || formWrapper.getGifts() == null) {
            return "redirect:/gifts";
        }

        List<GiftOrder> giftsToDelete = formWrapper.getGifts().stream()
                .filter(g -> g != null)
                .filter(g -> g.isSelected() || g.getId() != null)
                .toList();

        if (!giftsToDelete.isEmpty()) {
            giftRepository.deleteAll(giftsToDelete);
        }
        return "redirect:/gifts";
    }

    // 2. 儲存/更新送禮清單
    @PostMapping("/save")
    public String saveGifts(@ModelAttribute WrapperGiftOrder formWrapper) {
        if (formWrapper == null || formWrapper.getGifts() == null) {
            return "redirect:/gifts";
        }
        List<GiftOrder> gifts = formWrapper.getGifts();
        gifts.forEach(gift -> {
            gift.setGiftItem(cleanItem(formWrapper.getGiftItem1()));
            gift.setGiftItem2(cleanItem(formWrapper.getGiftItem2()));
            gift.setGiftItem3(cleanItem(formWrapper.getGiftItem3()));
            gift.setQuantity(Math.max(0, gift.getQuantity()));
            gift.setQuantity2(Math.max(0, gift.getQuantity2()));
            gift.setQuantity3(Math.max(0, gift.getQuantity3()));
        });
        List<GiftOrder> savedGifts = giftRepository.saveAll(gifts);
        searchService.index(savedGifts);
        return "redirect:/gifts/summary";
    }

    @GetMapping({"/search", "/gifts/search"})
    public String searchGifts(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "") String q,
                              Model model) {
        List<GiftOrderDocument> results = q.isBlank() ? List.of() : searchService.search(q.trim());
        model.addAttribute("query", q);
        model.addAttribute("results", results);
        return "gift_search";
    }

    // 3. 統計報表
    @GetMapping("/summary")
    public String showSummary(@RequestParam(defaultValue = "") String q,
                              @RequestParam(defaultValue = "") String item,
                              @RequestParam(defaultValue = "") String delivery,
                              Model model) {
        List<GiftSummaryPerson> allPeople = getSummaryPeople();
        List<GiftSummaryPerson> filteredPeople = filterSummaryPeople(allPeople, q, item, delivery);
        long selectedVipCount = filteredPeople.size();
        int totalQuantity = filteredPeople.stream().mapToInt(GiftSummaryPerson::getTotalQuantity).sum();

        model.addAttribute("reportPeople", filteredPeople);
        model.addAttribute("selectedVipCount", selectedVipCount);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("query", q);
        model.addAttribute("itemFilter", item);
        model.addAttribute("deliveryFilter", delivery);
        model.addAttribute("giftNames", allPeople.stream().flatMap(person -> person.getGifts().stream())
                .map(GiftSummaryItem::getGiftItem).distinct().toList());
        model.addAttribute("deliveryOptions", allPeople.stream().map(row -> row.getVip().getDeliveryMethodDisplay())
                .filter(value -> value != null && !value.isBlank()).distinct().sorted().toList());
        return "gift_summary";
    }

    @GetMapping("/summary/excel")
    public ResponseEntity<byte[]> exportSummaryExcel(@RequestParam(defaultValue = "") String q,
                                                      @RequestParam(defaultValue = "") String item,
                                                      @RequestParam(defaultValue = "") String delivery) throws IOException {
        List<GiftSummaryPerson> rows = filterSummaryPeople(getSummaryPeople(), q, item, delivery);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("禮品發送總表");
            Row header = sheet.createRow(0);
            String[] columns = {"貴賓ID", "貴賓姓名", "公司／職稱", "禮品項目與數量", "禮品總數", "手機", "聯絡電話", "配送地址", "配送方式"};
            for (int i = 0; i < columns.length; i++) header.createCell(i).setCellValue(columns[i]);
            int rowIndex = 1;
            for (GiftSummaryPerson person : rows) {
                Row row = sheet.createRow(rowIndex++);
                Vip vip = person.getVip();
                row.createCell(0).setCellValue(vip.getId());
                row.createCell(1).setCellValue(orEmpty(vip.getName()));
                row.createCell(2).setCellValue(orEmpty(vip.getTitle()));
                row.createCell(3).setCellValue(person.getGifts().stream()
                        .map(gift -> gift.getGiftItem() + " × " + gift.getQuantity())
                        .collect(java.util.stream.Collectors.joining(" / ")));
                row.createCell(4).setCellValue(person.getTotalQuantity());
                row.createCell(5).setCellValue(orEmpty(vip.getMobile()));
                row.createCell(6).setCellValue(orEmpty(vip.getPhone()));
                row.createCell(7).setCellValue(orEmpty(vip.getAddress()));
                row.createCell(8).setCellValue(orEmpty(vip.getDeliveryMethodDisplay()));
            }
            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
            workbook.write(output);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gift-summary.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(output.toByteArray());
        }
    }

    private List<GiftSummaryPerson> getSummaryPeople() {
        Map<Long, Vip> vipById = new LinkedHashMap<>();
        Map<Long, List<GiftSummaryItem>> giftsByVip = new LinkedHashMap<>();
        for (GiftOrder order : giftRepository.findAll()) {
            Vip vip = order.getVip();
            if (!order.isSelected() || vip == null || vip.isInColdPalace()) continue;
            List<GiftSummaryItem> giftItems = List.of(
                    summaryGiftItem(vip, order.getGiftItem(), order.getQuantity()),
                    summaryGiftItem(vip, order.getGiftItem2(), order.getQuantity2()),
                    summaryGiftItem(vip, order.getGiftItem3(), order.getQuantity3()));
            for (GiftSummaryItem gift : giftItems) {
                if (gift.getQuantity() <= 0) continue;
                vipById.putIfAbsent(vip.getId(), vip);
                giftsByVip.computeIfAbsent(vip.getId(), ignored -> new ArrayList<>()).add(gift);
            }
        }
        return giftsByVip.entrySet().stream()
                .map(entry -> new GiftSummaryPerson(vipById.get(entry.getKey()), entry.getValue()))
                .toList();
    }

    private GiftSummaryItem summaryGiftItem(Vip vip, String itemName, int quantity) {
        String displayName = itemName == null || itemName.isBlank() ? "未設定品項" : itemName;
        return new GiftSummaryItem(vip, displayName, quantity);
    }

    private List<GiftSummaryPerson> filterSummaryPeople(List<GiftSummaryPerson> people, String query, String item, String delivery) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        return people.stream().map(person -> {
            Vip vip = person.getVip();
            String deliveryMethod = orEmpty(vip.getDeliveryMethodDisplay());
            boolean matchesVip = needle.isEmpty()
                    || String.valueOf(vip.getId()).contains(needle)
                    || contains(vip.getName(), needle) || contains(vip.getTitle(), needle)
                    || contains(vip.getPhone(), needle)
                    || contains(vip.getMobile(), needle) || contains(vip.getAddress(), needle)
                    || contains(deliveryMethod, needle);
            List<GiftSummaryItem> gifts = person.getGifts().stream()
                    .filter(gift -> item == null || item.isBlank() || gift.getGiftItem().equals(item))
                    .filter(gift -> matchesVip || needle.isEmpty() || contains(gift.getGiftItem(), needle))
                    .toList();
            boolean hasTextMatch = matchesVip || gifts.stream().anyMatch(gift -> contains(gift.getGiftItem(), needle));
            if (!hasTextMatch || gifts.isEmpty() || (delivery != null && !delivery.isBlank() && !deliveryMethod.equals(delivery))) {
                return null;
            }
            return new GiftSummaryPerson(vip, gifts);
        }).filter(person -> person != null).toList();
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }

    private String orEmpty(String value) {
        return value == null ? "" : value;
    }

    private String cleanItem(String item) {
        if (item == null) return null;
        String trimmed = item.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

// 接收前端表單List綁定的Wrapper類別
class WrapperGiftOrder {
    private List<GiftOrder> gifts = new ArrayList<>();
    private String giftItem1;
    private String giftItem2;
    private String giftItem3;
    public List<GiftOrder> getGifts() { return gifts; }
    public void setGifts(List<GiftOrder> gifts) { this.gifts = gifts; }
    public String getGiftItem1() { return giftItem1; }
    public void setGiftItem1(String giftItem1) { this.giftItem1 = giftItem1; }
    public String getGiftItem2() { return giftItem2; }
    public void setGiftItem2(String giftItem2) { this.giftItem2 = giftItem2; }
    public String getGiftItem3() { return giftItem3; }
    public void setGiftItem3(String giftItem3) { this.giftItem3 = giftItem3; }
}

