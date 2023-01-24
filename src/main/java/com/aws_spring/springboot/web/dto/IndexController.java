package com.aws_spring.springboot.web.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
<<<<<<< HEAD
        
        return "index";
    }
    
=======

        return "index";
    }

>>>>>>> origin/mustech
    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }
}
