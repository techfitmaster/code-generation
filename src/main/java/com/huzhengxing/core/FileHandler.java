package com.huzhengxing.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huzhengxing.bean.InterfaceInfo;
import com.huzhengxing.bean.ModuleInfo;
import com.huzhengxing.bean.ParamInfo;
import org.apache.commons.collections.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                String reqType = "";
                List<ParamInfo> paramList = new ArrayList<ParamInfo>();
                List<ParamInfo> queryList = new ArrayList<ParamInfo>();
                List<ParamInfo> reqBody = new ArrayList<ParamInfo>();

                String resType = "";
                List<ParamInfo> resBody = new ArrayList<ParamInfo>();

                JSONObject interfaceObject = interfaceArray.getJSONObject(j);
                InterfaceInfo interfaceInfo = new InterfaceInfo();
                interfaceInfo.setPath(interfaceObject.getString("path"));
                interfaceInfo.setDesc(interfaceObject.getString("title"));
                interfaceInfo.setMethod(interfaceObject.getString("method"));

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
                interfaceInfo.setReqParams(paramList);

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
                interfaceInfo.setReqQuerys(queryList);

                // 入参层--reqBody
                interfaceInfo.setReqBody(reqBody);
                if (interfaceObject.containsKey("req_body_other")) {
                    reqBody = this.searchReqbody(interfaceObject);
                    interfaceInfo.setReqBody(reqBody);
                }

                // 判断入参类型
                reqType = this.judgeReqType(paramList, queryList, reqBody);
                interfaceInfo.setReqType(reqType);


                // 返回层--resBody
                interfaceInfo.setResBody(resBody);
                String strOrObject = interfaceObject.getJSONObject("res_body").getJSONObject("properties").getJSONObject("data").getString("type");
                if ("string".equals(strOrObject)) {
                    resType = "1";
                    interfaceInfo.setResType(resType);
                } else if ("object".equals(strOrObject)) {
                    resType = "2";
                    interfaceInfo.setResType(resType);

                    resBody = this.searchResbody(interfaceObject);
                    interfaceInfo.setResBody(resBody);
                }

                interfaceInfoList.add(interfaceInfo);
            }

        }

        return moduleInfoList;
    }

    /**
     * 解析"req_body_other"
     *
     * @param interfaceObject 入参
     * @return List<ParamInfo>
     */
    private List<ParamInfo> searchReqbody(JSONObject interfaceObject) {
        List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();

        JSONObject reqBodyObject = interfaceObject.getJSONObject("req_body_other").getJSONObject("properties");
        JSONArray requiredJsonArray = interfaceObject.getJSONObject("req_body_other").getJSONArray("required");
        List<String> requiredList = new ArrayList<String>();
        for (int i = 0; i < requiredJsonArray.size(); i++) {
            requiredList.add(requiredJsonArray.getString(i));
        }

        Map<String, Object> map = JSONObject.parseObject(reqBodyObject.toJSONString(), new TypeReference<Map<String, Object>>() {
        });
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            JSONObject valueObject = JSONObject.parseObject(entry.getValue().toString());

            ParamInfo paramInfo = new ParamInfo();
            paramInfo.setName(entry.getKey());
            paramInfo.setDesc(valueObject.getString("description"));
            paramInfo.setType(valueObject.getString("type"));
            if (requiredList.contains(entry.getKey())) {
                paramInfo.setRequired("1");
            }
            paramInfoList.add(paramInfo);

        }
        return paramInfoList;
    }


    /**
     * 解析"res_body"
     *
     * @param interfaceObject 入参
     * @return List<ParamInfo>
     */
    private List<ParamInfo> searchResbody(JSONObject interfaceObject) {
        List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();

        JSONObject resBodyObject = interfaceObject.getJSONObject("res_body").getJSONObject("properties").getJSONObject("data").getJSONObject("properties");

        Map<String, Object> map = JSONObject.parseObject(resBodyObject.toJSONString(), new TypeReference<Map<String, Object>>() {
        });
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            JSONObject valueObject = JSONObject.parseObject(entry.getValue().toString());

            ParamInfo paramInfo = new ParamInfo();
            paramInfo.setName(entry.getKey());
            paramInfo.setDesc(valueObject.getString("description"));
            paramInfo.setType(valueObject.getString("type"));
            paramInfoList.add(paramInfo);
        }
        return paramInfoList;
    }

    /**
     * 判断入参类型
     *
     * @param paramList 1
     * @param queryList 2
     * @param reqBody   3
     * @return 入参类型
     */
    private String judgeReqType(List<ParamInfo> paramList, List<ParamInfo> queryList, List<ParamInfo> reqBody) {
        if (CollectionUtils.isNotEmpty(paramList)) {
            return "1";
        }
        if (CollectionUtils.isNotEmpty(queryList)) {
            return "2";
        }
        if (CollectionUtils.isNotEmpty(reqBody)) {
            return "3";
        }
        return null;
    }


    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        List<ModuleInfo> moduleInfos = fileHandler.handleJsonFile();
        System.out.println(JSONObject.toJSONString(moduleInfos));
    }
}
