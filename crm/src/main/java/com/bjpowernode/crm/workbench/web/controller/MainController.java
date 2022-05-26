package com.bjpowernode.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author shkstart
 * @date 2022/5/2 - 21:03
 */
@Controller
public class MainController {
    @RequestMapping(value = "/workbench/main/index.do")
    public String mainIndex(){
        return "workbench/main/index";
    }
}
