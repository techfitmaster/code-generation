package com.huzhengxing.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 2020/7/1 17:14  zhengxing.hu
 * @version 2020/7/1 17:14  1.0.0
 * @file InterfaceInfo
 * @brief 接口信息
 * @par
 * @warning
 * @par 杭州锘崴信息科技有限公司版权所有©2020版权所有
 */
@Getter
@Setter
public class InterfaceInfo {


    /**
     * 接口请求地址 -> 1. 接口是地址 2.请求参数，返回参数，方法命名
     */
    private String path;

    /**
     * 接口描述 —> 接口描述
     */
    private String desc;


    /**
     * 请求方法 -> GET / POST / ...
     */
    private String method;


    /**
     * 入参类型  -> 1.url之中;  2.url之后; 3. body
     */
    private String reqType;

    /**
     * 请求参数设定(url之中)
     */
    private List<ParamInfo> reqParams;

    /**
     * 请求参数设定(url之后)
     */
    private List<ParamInfo> reqQuerys;

    /**
     * 请求参数设定(body)
     */
    private List<ParamInfo> reqBody;



    /**
     * 返回参数类型  -> 1.String;  2.body
     */
    private String resType;


    /**
     * 返回参数设定(返回body)
     */
    private List<ParamInfo> resBody;

}
