package com.nowcoder.controller;

import com.nowcoder.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by zhanghe on 2016/7/14.
 * 通过注释的方式（@Autowired）把所有的服务都注入进来
 * 参数是通过已经定义好的@service来的，在这里引用后就可以用了
 */
@Controller
public class SettingController {
    @Autowired
    WendaService wendaService;

    @RequestMapping(path={"/setting"},method = {RequestMethod.GET})
    @ResponseBody
    public String setting( HttpSession httpSession){
        return "Setting OK" + wendaService.getMessage(22);
    }
}
