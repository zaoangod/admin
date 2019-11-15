package com.z.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 角色
 * @Date: 2019-11-13 22:53
 * @Author: jy
 */
@Data
@Accessors(chain = true)
public class SysRole {

    //数据ID
    private String roleId;
    //角色名称
    private String roleName;
    //角色标识
    private String roleKey;
    //排序
    private String roleSort;
    //数据范围
    private String dataScope;
    //状态
    private String status;
    //创建人ID
    private String createBy;
    //创建时间
    private String createTime;
    //更新人ID
    private String updateBy;
    //更新时间
    private String updateTime;
    //备注
    private String remark;

}
