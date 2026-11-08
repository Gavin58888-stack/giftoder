package com.gift_order.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:gift_order;MODE=MySQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class JpaApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void giftListPageRenders() throws Exception {
		mockMvc.perform(get("/gifts"))
				.andExpect(status().isOk())
				.andExpect(view().name("gift_list"))
				.andExpect(content().string(containsString("貴賓送禮名冊")))
				.andExpect(content().string(containsString("/css/gifts.css")));
	}

	@Test
	void giftSearchPageRenders() throws Exception {
		mockMvc.perform(get("/gifts/search").param("q", "張總經理"))
				.andExpect(status().isOk())
				.andExpect(view().name("gift_search"))
				.andExpect(content().string(containsString("搜尋送禮紀錄")))
				.andExpect(content().string(containsString("張總經理")));
	}

	@Test
	void giftSummaryPageRenders() throws Exception {
		mockMvc.perform(get("/gifts/summary"))
				.andExpect(status().isOk())
				.andExpect(view().name("gift_summary"))
				.andExpect(content().string(containsString("禮品發送總表")))
				.andExpect(content().string(containsString("尚未選擇送禮對象")));
	}

	@Test
	void stylesheetIsServed() throws Exception {
		mockMvc.perform(get("/css/gifts.css"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/css"))
				.andExpect(content().string(containsString(".app-shell")));
	}

}
