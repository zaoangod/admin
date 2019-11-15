package com.z.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 角色菜单
 * @Date: 2019-11-14 10:04
 * @Author: jy
 */
@Data
@Accessors(chain = true)
public class SysRoleMenu {

    //数据ID
    private String roleId;
    //角色名称
    private String menuId;
}
