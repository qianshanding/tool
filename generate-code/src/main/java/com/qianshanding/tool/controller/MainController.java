package com.qianshanding.tool.controller;

import com.qianshanding.tool.config.HoldConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by fish on 2017/3/25.
 */
@Controller
public class MainController {
    @Autowired
    private HoldConfig holdConfig;

    @RequestMapping(value = "/")
    public String mian() {
        return "main";
    }

    @RequestMapping(value = "/project")
    public String project() {
        System.out.println(holdConfig.getBasePath());
        return "main";
    }
}