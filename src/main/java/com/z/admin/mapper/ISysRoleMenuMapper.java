package com.z.admin.mapper;

import com.z.admin.model.SysRoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISysRoleMenuMapper {

    /**
     * 删除角色菜单信息
     *
     * @param param 角色菜单信息
     * @return 结果
     */
    int deleteRoleMenu(SysRoleMenu param);

    /**
     * 新增角色菜单信息
     *
     * @param param 角色菜单信息
     * @return 结果
     */
    int insertUserRole(List<SysRoleMenu> param);

}