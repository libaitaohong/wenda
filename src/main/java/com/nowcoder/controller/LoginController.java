package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by zhanghe on 2017/5/27.
 * 登录注册Controller
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password){
        try{
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("msg")){
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            //如果map中没有msg，则说明注册成功，跳转到首页
            return "redirect:/";
        }catch(Exception e){
            logger.error("注册异常" + e.getMessage() );
            return "login";
        }
    }
    //注册逻辑
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String reg(Model model){
        return "login";
    }

    //登录
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpResponse response){
        try{
            Map<String, String> map = userService.login(username, password);
            if (map.containsKey("msg")){
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            //如果map中没有msg，则说明注册成功，跳转到首页
            return "redirect:/";
        }catch(Exception e){
            logger.error("注册异常" + e.getMessage() );
            return "login";
        }
    }

}
