package com.huzhengxing.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huzhengxing.bean.InterfaceInfo;
import com.huzhengxing.bean.ModuleInfo;
import com.huzhengxing.bean.ParamInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 2020/7/1 17:30  zhengxing.hu
 * @version 2020/7/1 17:30  1.0.0
 * @file FileHandler
 * @brief 解析Json文件
 * @par
 * @warning
 * @par 杭州锘崴信息科技有限公司版权所有©2020版权所有
 */
public class FileHandler {

    /**
     * 解析Json文件
     */
    private List<ModuleInfo> handleJsonFile() {
        List<ModuleInfo> moduleInfoList = new ArrayList<ModuleInfo>();
        String yapiFileStr = this.readFile();


        // controller层
        JSONArray controllerArray = JSONObject.parseArray(yapiFileStr);
        int size010 = controllerArray.size();
        for (int i = 0; i < size010; i++) {
            List<InterfaceInfo> interfaceInfoList = new ArrayList<InterfaceInfo>();

            JSONObject controllerObject = controllerArray.getJSONObject(i);
            ModuleInfo moduleInfo = new ModuleInfo();
            moduleInfo.setName(controllerObject.getString("name"));
            moduleInfo.setDesc(controllerObject.getString("desc"));
            moduleInfo.setInterfaceInfoList(interfaceInfoList);
            moduleInfoList.add(moduleInfo);

            // interface层
            JSONArray interfaceArray = controllerObject.getJSONArray("list");
            int size020 = interfaceArray.size();
            for (int j = 0; j < size020; j++) {
                List<ParamInfo> paramList = new ArrayList<ParamInfo>();
                List<ParamInfo> queryList = new ArrayList<ParamInfo>();
                String reqBody = null;
                String resBody = null;

                JSONObject interfaceObject = interfaceArray.getJSONObject(j);
                InterfaceInfo interfaceInfo = new InterfaceInfo();
                interfaceInfo.setPath(interfaceObject.getString("path"));
                interfaceInfo.setDesc(interfaceObject.getString("title"));
                interfaceInfo.setMethod(interfaceObject.getString("method"));
                interfaceInfo.setReqParams(paramList);
                interfaceInfo.setReqQuerys(queryList);
                interfaceInfoList.add(interfaceInfo);

                // 入参层--reqParams
                JSONArray reqParamsArray = interfaceObject.getJSONArray("req_params");
                int size031 = reqParamsArray.size();
                for (int k1 = 0; k1 < size031; k1++) {
                    JSONObject paramObject = reqParamsArray.getJSONObject(k1);
                    ParamInfo paramInfo = new ParamInfo();
                    paramInfo.setName(paramObject.getString("name"));
                    paramInfo.setDesc(paramObject.getString("desc"));
                    paramInfo.setRequired(paramObject.getString("required"));
                    paramList.add(paramInfo);
                }

                // 入参层--reqQuerys
                JSONArray reqQuerysArray = interfaceObject.getJSONArray("req_query");
                int size032 = reqQuerysArray.size();
                for (int k2 = 0; k2 < size032; k2++) {
                    JSONObject paramObject = reqQuerysArray.getJSONObject(k2);
                    ParamInfo paramInfo = new ParamInfo();
                    paramInfo.setName(paramObject.getString("name"));
                    paramInfo.setDesc(paramObject.getString("desc"));
                    paramInfo.setRequired(paramObject.getString("required"));
                    queryList.add(paramInfo);
                }

                // 入参层--reqBody
                if(interfaceObject.containsKey("req_body_other")){
                    reqBody = interfaceObject.getJSONObject("req_body_other").getString("properties");
                    interfaceInfo.setReqBody(reqBody);
                }

                // 返回层--resBody
                resBody = interfaceObject.getJSONObject("res_body").getString("properties");
                interfaceInfo.setResBody(resBody);
            }

        }

        return moduleInfoList;
    }

    /**
     * 读取文件
     */
    private String readFile() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            File file = new File("src/main/resources/yapifile/demo--api.json");
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                stringBuffer.append(lineTxt);
            }
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(stringBuffer);
    }


    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        List<ModuleInfo> moduleInfos = fileHandler.handleJsonFile();
        System.out.println(JSONObject.toJSONString(moduleInfos));
    }
}
