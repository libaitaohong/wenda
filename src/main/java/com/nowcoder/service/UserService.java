package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    //这里返回值是map，是因为会有各种返回值，比如用户名被注册了等等，把返回值写在map中，如果成功就返回空
    public Map<String, String> register(String username, String password){
        System.out.println("*************controller register*******************");
        Map<String,String> map = new HashMap<String, String>();
        //StringUtil就是给String使用的工具类，类似还有ArrayUtil
        if (StringUtils.isBlank(username)){ //如果用户名为空
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){ //如果密码为空
            map.put("msg", "密码不能为空");
            return map;
        }
        //注册的用户名必须是没有存在过的
        //首先查询用户名是不是已经存在
        User user = userDAO.selectByName(username);
        if (user != null){
            map.put("msg", "用户名已注册");
            return map;
        }
        //真正的注册逻辑
        user = new User();
        user.setName(username);
        //给用户密码加盐
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        //设置头像
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5( password+user.getSalt()));
        userDAO.addUser(user);
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    //登录
    public Map<String, String> login(String username, String password){
        Map<String,String> map = new HashMap<String, String>();
        //StringUtil就是给String使用的工具类，类似还有ArrayUtil
        if (StringUtils.isBlank(username)){ //如果用户名为空
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){ //如果密码为空
            map.put("msg", "密码不能为空");
            return map;
        }
        //首先查询用户名是不是已经存在
        User user = userDAO.selectByName(username);
        if (user == null){
            map.put("msg", "用户名不存在");
            return map;
        }
        if (!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码错误");
            return map;
        }
        //登录成功后
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    private String addLoginTicket(int userID){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userID);
        //设置过期时间100天
        Date now = new Date();
        now.setTime(3600*24*100 + now.getTime());
        loginTicket.setExpired(now);
        //设置状态为0，表示ticket是有效的
        loginTicket.setStatus(0);
        //随机生成ticket
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        //将ticket加入数据库
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

}
