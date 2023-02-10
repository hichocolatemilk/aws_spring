package com.aws_spring.springboot.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfileControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void real_profile_조회(){

        String expectedProfile = "real";
        MockEnvironment env =new MockEnvironment();

        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("oauth");
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);
        String profile = controller.profile();

        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void realProfile_없으면_첫_번째가_조회(){

        String expectedProfile = "oauth";
        MockEnvironment env =new MockEnvironment();

        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);
        String profile = controller.profile();

        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void active_profile이_없으면_default가_조회(){

        String expectedProfile = "default";
        MockEnvironment env =new MockEnvironment();
        ProfileController controller = new ProfileController(env);

        String profile = controller.profile();

        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void profile은_인증없이_호출() throws Exception{

        String expected = "default";

        ResponseEntity<String> response = restTemplate.getForEntity("/profile", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isGreaterThan(expected);

    }
}