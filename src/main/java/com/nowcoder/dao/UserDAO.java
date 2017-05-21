package com.nowcoder.dao;

import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

import static com.nowcoder.dao.UserDAO.INSERT_FIELDS;
import static com.nowcoder.dao.UserDAO.TABLE_NAME;

/**
 * Created by zhanghe on 2017/5/20.
 * 访问数据库
 */
//Mapper说明我这个是一个与Mybatis关联的一个dao
@Mapper
public interface UserDAO {
    String TABLE_NAME = " user "; //设置一个表名直接用在语句里
    String INSERT_FIELDS = " name, password, salt, head_url"; //把insert语句抽取出来
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    //里面的headUrl不能写成head_url，因为读取的是User里面的字段
    @Insert({
            "insert into", TABLE_NAME, "(",  INSERT_FIELDS, ") values(#{name}, #{password}, #{salt}, #{headUrl})"
    })
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(int id);
}
