package com.nhnacademy.coupon.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//cicd 확인용
@Controller
public class IndexController {
    @GetMapping(value = {"/index.html","/"})
    public String index(){
        return "index/index";
    }
}
