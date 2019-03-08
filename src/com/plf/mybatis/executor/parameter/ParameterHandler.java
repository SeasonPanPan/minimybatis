/**
 * 
 */
package com.plf.mybatis.executor.parameter;


import java.sql.PreparedStatement;


/**
 * ParameterHandler.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public interface ParameterHandler
{

    /**
     * 设置参数
     * 
     * @param paramPreparedStatement 
     * @see 
     */
    void setParameters(PreparedStatement paramPreparedStatement);
}
