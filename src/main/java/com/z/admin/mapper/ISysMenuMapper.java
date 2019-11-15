package com.z.admin.mapper;

import com.z.admin.model.SysMenu;
import com.z.admin.model.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @Description: 菜单
 * @Date: 2019-11-13 16:57
 * @Author: jy
 */
@Repository
public interface ISysMenuMapper {

    /**
     * 查询菜单列表
     *
     * @param param 菜单信息
     * @return 菜单信息集合信息
     */
    List<SysUser> selectMenu(SysMenu param);

    /**
     * 删除菜单信息
     *
     * @param param 菜单信息
     * @return 结果
     */
    int deleteMenu(SysMenu param);

    /**
     * 修改菜单信息
     *
     * @param param 菜单信息
     * @return 结果
     */
    int updateMenu(SysMenu param);

    /**
     * 新增菜单信息
     *
     * @param param 菜单信息
     * @return 结果
     */
    int insertMenu(SysMenu param);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 结果
     */
    Set<String> selectPermissionsByUserId(long userId);

}