package com.huzhengxing.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 2020/7/1 17:19  zhengxing.hu
 * @version 2020/7/1 17:19  1.0.0
 * @file ParamInfo
 * @brief 参数信息
 * @par
 * @warning
 * @par 杭州锘崴信息科技有限公司版权所有©2020版权所有
 */
@Getter
@Setter
public class ParamInfo {
    /**
     * 参数命名
     */
    private String name;

    /**
     * 参数描述
     */
    private String desc;

    /**
     * 设定参数类型
     */
    private String type;
}
