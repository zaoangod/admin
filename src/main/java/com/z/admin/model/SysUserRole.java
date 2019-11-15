package com.z.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 用户角色
 * @Date: 2019-11-14 08:58
 * @Author: jy
 */
@Data
@Accessors(chain = true)
public class SysUserRole {

    //用户ID
    private Long userId;
    //角色ID
    private Long roleId;

}