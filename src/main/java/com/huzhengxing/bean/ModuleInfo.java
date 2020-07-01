package com.huzhengxing.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 2020/7/1 17:32  zhengxing.hu
 * @version 2020/7/1 17:32  1.0.0
 * @file ModuleInfo
 * @brief 模块信息定义，Controller ，Service文件
 * @par
 * @warning
 * @par 杭州锘崴信息科技有限公司版权所有©2020版权所有
 */
@Getter
@Setter
public class ModuleInfo {

    /**
     * 所属模块 -> Controller注释
     */
    private String name;

    /**
     * 模块描述 -> Controller命名
     */
    private String desc;


    private List<InterfaceInfo> interfaceInfoList;
}
