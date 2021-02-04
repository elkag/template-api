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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EntityScan({"com.template"})
@Import({HibernateSearchConfig.class, SpringSecurityTestConfig.class})
class AdminItemControllerTest extends ItemControllerTestBase {

   /* @Autowired
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
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void addItemByAdminTest_ExpectApproved() throws Exception {

        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();

        mockMvc.perform(post("/items/add").
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(itemDTO)).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andExpect(jsonPath("$.link", is(itemDTO.getLink()))).
                andExpect(jsonPath("$.name", is(itemDTO.getName()))).
                andExpect(jsonPath("$.description", is(itemDTO.getDescription()))).
                andExpect(jsonPath("$.notes", is(itemDTO.getNotes()))).
                andExpect(jsonPath("$.categories", hasSize(2))).
                andExpect(jsonPath("$.tags", hasSize(2))).
                andExpect(jsonPath("$.approved", is(true)));
    }

    @Test
    @WithUserDetails(value = "superadmin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void approveItem() throws Exception {

        mockMvc.perform(post("/items/admin/approve").
                param("id", String.valueOf(AUTHOR_ITEM.getId())).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.approved", is(true)));
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void approveItemByAdmin_ExpectError() throws Exception {

        mockMvc.perform(post("/items/admin/approve").
                param("id", String.valueOf(AUTHOR_ITEM.getId())).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "superadmin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void approveItemByAdmin_ExpectStatusNoContent() throws Exception {
        final String nonExistingItemID = "1897";
        mockMvc.perform(post("/items/admin/approve").
                param("id", nonExistingItemID).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isNoContent());
    }

    @AfterEach
    @Override
    public void tearDown(){
       super.tearDown();
    }*/
}