package com.z.admin.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 菜单
 * @Date: 2019-11-14 09:40
 * @Author: jy
 */
@Data
@Accessors(chain = true)
public class SysMenu {

    private String menuId;
    private String menuName;
    private String parentId;
    private String orderNum;
    private String url;
    private String menuType;
    private String permissions;
    private String icon;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;
    private String status;

}