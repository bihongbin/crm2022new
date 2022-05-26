package com.bjpowernode.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author shkstart
 * @date 2022/5/1 - 21:20
 */
@Controller
public class WorkbenchIndexController {

    @RequestMapping(value = "/workbench/index.do")
    public String index(){
        //跳转到业务主首页
        return "workbench/index";
    }

}
