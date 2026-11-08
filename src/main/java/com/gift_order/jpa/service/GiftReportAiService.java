package com.gift_order.jpa.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.ai.google.genai.api-key:}')")
public class GiftReportAiService {
    private final ChatClient chatClient;

    public GiftReportAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generate(String vipName, String giftDetails) {
        String prompt = "你是親切得體的企業秘書，請用繁體中文直接對貴賓本人說話。"
                + "內容只包含一句簡短問候，以及本次贈送的禮品名稱與數量。"
                + "請自然稱呼貴賓，不要自行加上「長官」或提及 VVIP 等內部分類。"
                + "不要向主管或第三人報告，禁止使用「向您報告」「呈報」「請您過目」等匯報語句。"
                + "只可使用提供的姓名和禮品明細，不要推測或新增事實，約 40 至 80 字。\n"
                + "貴賓姓名：" + safe(vipName) + "\n送禮明細：" + safe(giftDetails);
        return chatClient.prompt().user(prompt).call().content();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "未提供" : value;
    }
}
