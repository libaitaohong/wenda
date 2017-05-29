package com.nowcoder.interceptor;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by zhanghe on 2017/5/29.
 *用户身份验证
 */
@Component
public class PassportInterceptor implements HandlerInterceptor{

    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHolder hostHolder;

    //请求开始之前做的事，如果返回false就结束了，被拦截器拦截掉，失败了
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        //如果http请求中cookie不为空
       if (httpServletRequest.getCookies() != null){
           //遍历cookie中的值，找到ticket，并把该值赋值给String变量ticket
           for (Cookie cookie : httpServletRequest.getCookies()){
               if (cookie.getName().equals("ticket")){
                   ticket = cookie.getValue();
                   break;
               }
           }
       }
        //把这个ticket对应的用户信息取出来
        if (ticket != null){
            //把这个ticket导出来
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            //判断这个ticket是不是=null，或者在当前时间点以前，说明已经过期了，或者它的状态不是0，说明他是无效的
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus()!=0){
                return true;
            }
            //得到用户信息
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }
//指handler处理完之后再回调这个函数
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
       //所有的controller在渲染之前，都会把user加入进去，这样可以直接在页面上使用user
        if (modelAndView != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }
//整体渲染完后调用这个函数，比如清除数据等
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
