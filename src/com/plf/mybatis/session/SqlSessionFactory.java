/**
 * 
 */
package com.plf.mybatis.session;

/**
 * SqlSessionFactory.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public interface SqlSessionFactory
{

    /**
     * 开启数据库会话
     * 
     * @return 
     * @see 
     */
    SqlSession openSession();

}
