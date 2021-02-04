package com.template.item.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.config.lucene.HibernateSearchConfig;
import com.template.config.SpringSecurityTestConfig;
import com.template.item.mappers.ItemMapper;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EntityScan({"com.template"})
@Import({HibernateSearchConfig.class, SpringSecurityTestConfig.class})
class ItemControllerTest extends ItemControllerTestBase {

   /* @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        super.setUp();
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void editItemTest() throws Exception {

        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(AUTHOR_ITEM);

        mockMvc.perform(put("/items/update").
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(itemDTO)).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.link", is(itemDTO.getLink()))).
                andExpect(jsonPath("$.name", is(itemDTO.getName()))).
                andExpect(jsonPath("$.description", is(itemDTO.getDescription()))).
                andExpect(jsonPath("$.notes", is(itemDTO.getNotes()))).
                andExpect(jsonPath("$.categories", hasSize(2))).
                andExpect(jsonPath("$.tags", hasSize(2))).
                andExpect(jsonPath("$.approved", is(false)));
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void editItemTestByOtherUser_ExpectStatusUnauthorized() throws Exception {

        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(ADMIN_ITEM);

        mockMvc.perform(put("/items/update").
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(itemDTO)).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void editItemTestByOtherUser_ExpectStatusNotFound() throws Exception {

        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        itemDTO.setId(1435L);

        mockMvc.perform(put("/items/update").
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(itemDTO)).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isNoContent());
    }

    @Test
    void getItemTest() throws Exception {

        mockMvc.perform(get("/items/get?id=" + APPROVED_ITEM.getId())).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", notNullValue())).
                andExpect(jsonPath("$.link", is(APPROVED_ITEM.getLink()))).
                andExpect(jsonPath("$.name", is(APPROVED_ITEM.getName()))).
                andExpect(jsonPath("$.description", is(APPROVED_ITEM.getDescription()))).
                andExpect(jsonPath("$.notes", is(APPROVED_ITEM.getNotes()))).
                andExpect(jsonPath("$.categories", hasSize(2))).
                andExpect(jsonPath("$.tags", hasSize(2))).
                andExpect(jsonPath("$.approved", is(true)));
    }

    @Test
    void getNonExistingItemTest_ExpectStatusNotFound() throws Exception {

        mockMvc.perform(get("/items/get?id=1245").
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isNoContent());
    }

    @Test
    void getUnapprovedItemTest_ExpectStatusUnauthorized() throws Exception {

        mockMvc.perform(get("/items/get?id=" + AUTHOR_ITEM.getId())).
                andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void addItemByAuthorTest_ExpectNotApproved() throws Exception {

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
                andExpect(jsonPath("$.approved", is(false)));
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    void deleteItemTest_ExpectSuccess() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.
                delete("/items/delete").
                param("id", String.valueOf(AUTHOR_ITEM.getId()))).
                andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    void deleteNonExistingItemTest_ExpectStatusNoContent() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.
                delete("/items/delete").
                param("id", "123456")).
                andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "author", userDetailsServiceBeanName = "userDetailsService")
    void deleteItemBelongsToAnotherUserTest_ExpectStatusUnauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.
                delete("/items/delete").
                param("id", String.valueOf(ADMIN_ITEM.getId()))).
                andExpect(status().isUnauthorized());
    }


    @AfterEach
    public void tearDown(){
        super.tearDown();
    }*/
}