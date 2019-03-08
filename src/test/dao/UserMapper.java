/**
 * 
 */
package test.dao;


import java.util.List;

import test.bean.User;


/**
 * UserMapper.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public interface UserMapper
{

    /**
     * 获取单个user
     * 
     * @param id
     * @return 
     * @see 
     */
    User getUser(String id);
    
    /**
     * 获取所有用户
     * 
     * @return 
     * @see 
     */
    List<User> getAll();
    
    /**
     * 更新用户（功能未完成）
     * 
     * @param id 
     */
    void updateUser(String id);
}
