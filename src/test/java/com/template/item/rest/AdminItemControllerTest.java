package com.template.item.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.config.lucene.HibernateSearchConfig;
import com.template.config.SpringSecurityTestConfig;
import com.template.item.models.ItemDTO;
import com.template.item.utils.ItemServiceTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EntityScan({"com.template"})
@Import({HibernateSearchConfig.class, SpringSecurityTestConfig.class})
class AdminItemControllerTest extends ItemControllerTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup(){
        super.setUp();
    }

    @Test
    @WithUserDetails(value = "superadmin", userDetailsServiceBeanName = "userDetailsService")
    void deleteItemTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.
                delete("/items/admin/delete").
                param("id", String.valueOf(AUTHOR_ITEM.getId())).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "superadmin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void approveItem() throws Exception {

        String request =    "[" +
                                "{" +
                                    "\"id\": "+ AUTHOR_ITEM.getId() + ", \"isApproved\": true" +
                                "}" +
                            "]";

        mockMvc.perform(put("/items/admin/approve").
                contentType(MediaType.APPLICATION_JSON).
                content(request).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.error", is(false))).
                andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void approveItemByAdmin_ExpectNotApproved() throws Exception {
        String request =    "[" +
                                "{" +
                                    "\"id\": "+ AUTHOR_ITEM.getId() + ", \"isApproved\": true" +
                                "}" +
                            "]";

        mockMvc.perform(put("/items/admin/approve").
                contentType(MediaType.APPLICATION_JSON).
                content(request).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.items", hasSize(1))).
                andExpect(jsonPath("$.items[0].approved", is(false)));
    }

    @Test
    @WithUserDetails(value = "superadmin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void approveItemByAdmin_ExpectStatusNoContent() throws Exception {
        final String nonExistingItemID = "1897";
        String request ="[" +
                            "{" +
                                "\"id\": "+ nonExistingItemID + ", \"isApproved\": true" +
                            "}" +
                        "]";
        mockMvc.perform(put("/items/admin/approve").
                contentType(MediaType.APPLICATION_JSON).
                content(request).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.error", is(true))).
                andExpect(jsonPath("$.items", hasSize(0)));
    }

    @AfterEach
    @Override
    public void tearDown(){
       super.tearDown();
    }
}