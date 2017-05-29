package com.nowcoder.model;
import org.springframework.stereotype.Component;

/**
 * Created by zhanghe on 2017/5/29.
 * 存放已经登录的用户数据
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }
    public void setUser(User user){
        users.set(user);
    }
    public void clear() {
        users.remove();
    }

}
