/**
 * 
 */
package com.plf.mybatis.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.plf.mybatis.constants.Constant;
import com.plf.mybatis.constants.Constant.SqlType;
import com.plf.mybatis.mapping.MappedStatement;
import com.plf.mybatis.session.Configuration;


/**
 * XmlUtil.java
 * 
 * @author PLF
 * @date 2019年3月7日
 */
public final class XmlUtil
{

    /**
     * readMapperXml
     * 
     * @param fileName
     * @param mappedStatements 
     * @see 
     */
    @SuppressWarnings("rawtypes")
    public static void readMapperXml(File fileName, Configuration configuration)
    {

        try
        {

            // 创建一个读取器
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding(Constant.CHARSET_UTF8);
            
            // 读取文件内容
            Document document = saxReader.read(fileName);

            // 获取xml中的根元素
            Element rootElement = document.getRootElement();

            // 不是beans根元素的，文件不对
            if (!Constant.XML_ROOT_LABEL.equals(rootElement.getName()))
            {
                System.err.println("mapper xml文件根元素不是mapper");
                return;
            }

            String namespace = rootElement.attributeValue(Constant.XML_SELECT_NAMESPACE);

            List<MappedStatement> statements = new ArrayList<>();
            for (Iterator iterator = rootElement.elementIterator(); iterator.hasNext();)
            {
                Element element = (Element)iterator.next();
                String eleName = element.getName();
                
                MappedStatement statement = new MappedStatement();
                
                if (SqlType.SELECT.value().equals(eleName))
                {
                    String resultType = element.attributeValue(Constant.XML_SELECT_RESULTTYPE);
                    statement.setResultType(resultType);
                    statement.setSqlCommandType(SqlType.SELECT);
                }
                else if (SqlType.UPDATE.value().equals(eleName))
                {
                    statement.setSqlCommandType(SqlType.UPDATE);
                }
                else
                {
                    // 其他标签自己实现
                    System.err.println("不支持此xml标签解析:" + eleName);
                    statement.setSqlCommandType(SqlType.DEFAULT);
                }

                //设置SQL的唯一ID
                String sqlId = namespace + "." + element.attributeValue(Constant.XML_ELEMENT_ID); 
                
                statement.setSqlId(sqlId);
                statement.setNamespace(namespace);
                statement.setSql(CommonUtis.stringTrim(element.getStringValue()));
                statements.add(statement);
                
                System.out.println(statement);
                configuration.addMappedStatement(sqlId, statement);
                
                //这里其实是在MapperRegistry中生产一个mapper对应的代理工厂
                configuration.addMapper(Class.forName(namespace));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
