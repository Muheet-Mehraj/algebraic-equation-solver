package com.equationsolver.equations.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @Hidden
    @GetMapping("/")
    public String root() {
        return "redirect:/swagger-ui.html";
    }
}