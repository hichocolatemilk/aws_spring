package com.aws_spring.springboot.web;


import com.aws_spring.springboot.domain.posts.Posts;
import com.aws_spring.springboot.domain.posts.PostsRepository;
import com.aws_spring.springboot.web.dto.PostsSaveRequestDto;
import com.aws_spring.springboot.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

   @Autowired
   private WebApplicationContext webApplicationContext;

   @Autowired
   private PostsRepository postsRepository;


   @LocalServerPort
   private int port;

   private MockMvc mvc;


   @Before
   public void setup(){
       mvc = MockMvcBuilders
               .webAppContextSetup(webApplicationContext)
               .apply(springSecurity())
               .build();
   }

   @After
   public void tearDown() throws Exception{
       postsRepository.deleteAll();
    }

   @Test
   @WithMockUser(roles = "USER")
   public void Posts_test() throws Exception{
       String title = "title";
       String content = "content";
       PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
               .title(title)
               .content(content)
               .author("author")
               .build();

       String url = "http://localhost:" + port + "/api/v1/posts";

       mvc.perform(post(url)
               .contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(requestDto)))
               .andExpect(status().isOk());

       List<Posts> all = postsRepository.findAll();
       assertThat(all.get(0).getTitle()).isEqualTo(title);
       assertThat(all.get(0).getContent()).isEqualTo(content);
   }
//
   @Test
   @WithMockUser(roles = "USER")
   public void Posts_update() throws Exception{
       Posts savePosts = postsRepository.save(Posts.builder()
               .title("title")
               .content("content")
               .author("author")
               .build());

       Long updateId = savePosts.getId();
       String expectedTitle = "title2";
       String expectedContent = "content2";

       PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
               .title(expectedTitle)
               .content(expectedContent)
               .build();

       String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

       HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

       mvc.perform(put(url)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(new ObjectMapper().writeValueAsString(requestDto)))
               .andExpect(status().isOk());

       List<Posts> all = postsRepository.findAll();
       assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
       assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
   }

}