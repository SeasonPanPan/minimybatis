/**
 * 
 */
package test;


import com.plf.mybatis.session.SqlSession;
import com.plf.mybatis.session.SqlSessionFactory;
import com.plf.mybatis.session.SqlSessionFactoryBuilder;

import test.bean.User;
import test.dao.UserMapper;


/**
 * TestMain.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public class TestMain
{

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args)
    {

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("conf.properties");
        SqlSession session = factory.openSession();

        UserMapper userMapper = session.getMapper(UserMapper.class);
        User user = userMapper.getUser("1");
        System.out.println("******* " + user);
        System.out.println("*******all " + userMapper.getAll());
        
        userMapper.updateUser("1");
        System.out.println("*******all " + userMapper.getAll());
    }

}
