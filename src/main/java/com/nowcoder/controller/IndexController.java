package com.nowcoder.controller;

import com.nowcoder.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by zhanghe on 2016/7/13.
 * 在开发网站的时候，所有网站的入口是一个URL，每一个url在后台都有一个对应的处理函数，
 * 这个函数就是对应在@Controller注解的类里面
 */
//@Controller //访问入口 入口层
public class IndexController {
     //第一个网址，是一个/，意思是说，若是这样的一个网址，那么返回Hello Noecoder这个字符串
     @RequestMapping(path={"/","/index"}) //路径映射
     //ResponseBody表示返回一个字符串而不是模板
     @ResponseBody
    public String index( HttpSession httpSession){ //一个首页,首页的controller
    //又加了一个session消息，直接从session中读取出来，见redirect函数
         return "Hello Noecoder" + httpSession.getAttribute("msg");
    }
    //用户个人主页
    @RequestMapping(path={"/profile/{groupId}/{userId}"})
    @ResponseBody
    //userid在路径里面，使用pathvariable，他的意思是现在要解析的是一个路径变量
    //两个参数也可以
    //通过使用pathvariable将路径里面的参数解析到变量里面去，在网页的处理函数里可直接用该变量
    public String profile(@PathVariable("userId") int userId,
                          @PathVariable("groupId") String groupId,
                          //@RequestParam是以请求参数的方法来将参数传递过去
                          @RequestParam("type") int type,
                          //若url上没有提供，就是用defaultValue,require就是必须的，有就是用defaultValue后，require会默认为true
                          //若require写为false，那么没有默认的，url上没写也不会出错，但会传回一个null
                          //http://localhost:8080/profile/user/123?type=2
                          // 输出Profile page of user / 123, t:2 k: zz
                          @RequestParam(value = "key",defaultValue = "zz",required = false) String key){
        return String.format("Profile page of %s / %d, t:%d k: %s", groupId, userId, type, key);
    }

    /**
     * 这个方法演示了从后台向前端页面传递参数
     * method指定方法
     * 这个函数意思是访问页面地址地址/vm，返回内容是一个叫做home的模板
     * 模板就是显示页面的地方
     * 在文件application.properties中写spring.velocity.suffix=.html就会识别.html文件
     * 使用模板传输数据时，使用Model，它是spring mvc中ui的框架，他会将数据传到模板里面去
     * 除了加一些基础的变量以外，还可加自定义的对象。现在定义了一个user类
     */
    @RequestMapping(path={"/vm"},method = {RequestMethod.GET})
    public String template(Model model){
        //传入简单变量
        model.addAttribute("value1","vvvvvvl");
        List<String> color = Arrays.asList(new String[]{"RED","GREEN","BULE"});
        //传入复杂变量List，Map
        model.addAttribute("color",color);

        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 4; i++){
            map.put(String.valueOf(i), String.valueOf(i*i));

        }
        model.addAttribute("map",map);
        model.addAttribute("user",new User("lin"));
        return "home";
    }
    /**
     * 获取request请求的所有信息。
     * header，打印头的值
     * 还可以对cookie进行遍历,除了遍历，也可以通过注解的方式直接读取值，如 @CookieValue("JSESSIONID")
     * reponse是我们返回给用户的
     *
     * request中主要负责参数解析，cookie读取，http请求字段
     * response中主要是下发，请求我，我需要给一个回复，还可以添加cookie，还可在header中写一些东西
     *
     * 页面结果：http://localhost:8080/request?type=2
     *
     * COOKIEVALUE：E1E09A35F1DEAB106F06EAAFDF7026EB
     * host:localhost:8080
     * connection:keep-alive
     * upgrade-insecure-requests:1
     * user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36
     * accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*;q=0.8
     * accept-encoding:gzip, deflate, sdch, br
     * accept-language:zh-CN,zh;q=0.8
     * cookie:JSESSIONID=E1E09A35F1DEAB106F06EAAFDF7026EB; username=nowcoder
     * Cookies:JSESSIONID value:E1E09A35F1DEAB106F06EAAFDF7026EBCookies:username value:nowcoderGET
     * type=2
     * null
     * http://localhost:8080/request
     * */
    @RequestMapping(path={"/request"},method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletResponse response,
                           HttpServletRequest request,
                           HttpSession httpSession,
                           @CookieValue("JSESSIONID") String sessionId) {

        StringBuilder sb = new StringBuilder();
        //将JSESSIONID的值添加进sb
        sb.append("COOKIEVALUE：" + sessionId);
        //把头打印出来
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        //cookies可以单独获取。。
        //也可以通过注解的方式直接读取JSESSIONID的值，在函数的变量括号里面，并且直接在函数开头打印出来了
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                sb.append("Cookies:" + cookie.getName() + " value:" + cookie.getValue());
            }
        }
        sb.append(request.getMethod() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        sb.append(request.getPathInfo() + "<br>");
        sb.append(request.getRequestURL() + "<br>");
        //response是我们返回给用户的
        //在response请求中会有有一个nowcoder字段，内容是hello
        response.addHeader("nowcoder","hello");
        //添加一个cookie
        response.addCookie(new Cookie("username","nowcoder"));
        //可以写二进制的流，例如验证码，在write中写二进制流或图片
       // response.getOutputStream().write();

        return sb.toString();
    }
/**
 *重定向
 * 从点·重定向需要传一个参数进来就是code
 * 有301 302跳转，302临时跳转，301强制跳转
 * 打开redirect界面时，会跳转到首页，这时session中的信息可以在首页取出来
 */
    @RequestMapping(path={"/redirect/{code}"},method = {RequestMethod.GET})

    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession){
        httpSession.setAttribute("msg","jump from redirect");
        RedirectView red = new RedirectView("/", true);
        if (code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return  red;
    }
/**
 * 异常捕获
 * 首先定义一个admin类，传一个参数key=admin，否则抛出异常
 * 自定义一个@ExceptionHandler()，当有异常没被spring处理，就跳到这里，作为统一异常处理
 * */
    @RequestMapping(path={"/admin"},method = {RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("参数不对");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "error:" + e.getMessage();
    }

}
