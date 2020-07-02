package com.huzhengxing.core;

import com.huzhengxing.bean.ModuleInfo;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * @author 2020/7/1 17:12  zhengxing.hu
 * @version 2020/7/1 17:12  1.0.0
 * @file Generator
 * @brief 代码生成
 * @par
 * @warning
 * @par 杭州锘崴信息科技有限公司版权所有©2020版权所有
 */
public class Generator {
    public static String UTF_8 = "UTF-8";
    /**
     * 包名
     */
    private static String pakage;

    /**
     * 类名
     */
    private static String className;




    /** 状态编码转换 */
    public static Map<String, String> javaTypeMap = new HashMap<String, String>();
    static
    {
        javaTypeMap.put("tinyint", "Integer");
        javaTypeMap.put("smallint", "Integer");
        javaTypeMap.put("mediumint", "Integer");
        javaTypeMap.put("int", "Integer");
        javaTypeMap.put("integer", "integer");
        javaTypeMap.put("bigint", "Long");
        javaTypeMap.put("float", "Float");
        javaTypeMap.put("double", "Double");
        javaTypeMap.put("decimal", "BigDecimal");
        javaTypeMap.put("bit", "Boolean");
        javaTypeMap.put("char", "String");
        javaTypeMap.put("varchar", "String");
        javaTypeMap.put("tinytext", "String");
        javaTypeMap.put("text", "String");
        javaTypeMap.put("mediumtext", "String");
        javaTypeMap.put("longtext", "String");
        javaTypeMap.put("date", "Date");
        javaTypeMap.put("datetime", "Date");
        javaTypeMap.put("timestamp", "Date");
    }


    private final List<String> templates = new ArrayList<String>();

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "src/main/java/com/nvxclouds";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "src/main/resources/mapper";

    /**
     * html空间路径
     */
    private static final String TEMPLATES_PATH = "src/main/resources/templates";

    private static final String PACKAGE_NAME = "com.nvxclouds";

    private VelocityContext context;

    private ModuleInfo moduleInfo;


    public void generate() throws Exception {
        init();
        writeParamToTemplate();
        for (String template : templates) {
            BufferedWriter bufferedWriter = null;
            String filePath = getFileName(template);
            System.out.println(filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);


            if (bufferedWriter != null) {
                Template tpl = Velocity.getTemplate(template, UTF_8);
                tpl.merge(context, bufferedWriter);
            }
            bufferedWriter.flush();

            outputStreamWriter.close();

            fileOutputStream.close();

        }
    }

    /**
     * 向模板写入参数
     */
    private void writeParamToTemplate() {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("package", PACKAGE_NAME);
        //都是大写
        velocityContext.put("className", moduleInfo.getDesc());
        velocityContext.put("classname", uncapitalize(moduleInfo.getDesc()));
        velocityContext.put("interfaceInfos",moduleInfo.getInterfaceInfoList());
        this.context = velocityContext;
    }

    private void init() {
        initVelocity();
        initTemplates();
    }


    public static String convertToCamelCase(String name)
    {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty())
        {
            // 没必要转换
            return "";
        }
        else if (!name.contains("_"))
        {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels)
        {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty())
            {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }


    /**
     * 驼峰首字符小写
     */
    public static String uncapitalize(String str)
    {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
        {
            return str;
        }
        return new StrBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }


    /**
     * 获取文件名
     */
    public static String getFileName(String template) {

        // 大写类名
        String javaPath = PROJECT_PATH + "/";
        String mybatisPath = MYBATIS_PATH + "/";
        String htmlPath = TEMPLATES_PATH + "/";

        if (template.contains("domain.java.vm")) {
            return javaPath + "domain" + "/" + className + ".java";
        }

        if (template.contains("Mapper.java.vm")) {
            return javaPath + "mapper" + "/" + className + "Mapper.java";
        }

        if (template.contains("Service.java.vm")) {
            return javaPath + "service" + "/" + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return javaPath + "service" + "/" + "impl" + "/" + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return javaPath + "controller" + "/" + className + "Controller.java";
        }

        if (template.contains("Mapper.xml.vm")) {
            return mybatisPath + className + "Mapper.xml";
        }

        if (template.contains("list.html.vm")) {
            return htmlPath + "/" + "list.html";
        }
        if (template.contains("add.html.vm")) {
            return htmlPath + "/" + "add.html";
        }
        if (template.contains("edit.html.vm")) {
            return htmlPath + "/" + "edit.html";
        }
        if (template.contains("sql.vm")) {
            return className + "Menu.sql";
        }
        return null;
    }


    /**
     * 需要生成的模板文件
     * @return
     */
    private List<String> initTemplates() {
        templates.add("templates/vm/java/domain.java.vm");
        templates.add("templates/vm/java/Mapper.java.vm");
        templates.add("templates/vm/java/Service.java.vm");
        templates.add("templates/vm/java/ServiceImpl.java.vm");
        templates.add("templates/vm/java/Controller.java.vm");
        templates.add("templates/vm/xml/Mapper.xml.vm");
        return templates;
    }

    private void initVelocity() {
        Properties p = new Properties();
        try {
            // 加载classpath目录下的vm文件
            p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(Velocity.ENCODING_DEFAULT, UTF_8);
            p.setProperty(Velocity.OUTPUT_ENCODING, UTF_8);
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }






}
