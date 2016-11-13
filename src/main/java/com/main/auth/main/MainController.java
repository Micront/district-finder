package com.main.auth.main;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Controller
public class MainController extends WebMvcConfigurerAdapter {

    @RequestMapping("/")
    public String index() {
        return "redirect:/index.html";
    }
}
