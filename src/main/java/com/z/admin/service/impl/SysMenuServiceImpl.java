package com.z.admin.service.impl;

import com.z.admin.mapper.ISysMenuMapper;
import com.z.admin.model.SysMenu;
import com.z.admin.model.SysUser;
import com.z.admin.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Description: 菜单
 * @Date: 2019-11-13 17:16
 * @Author: jy
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    @Autowired
    private ISysMenuMapper sysMenuDao;

    /**
     * 查询菜单列表
     *
     * @param param 菜单信息
     * @return 菜单信息集合信息
     */
    @Override
    public List<SysUser> selectMenu(SysMenu param) {
        return sysMenuDao.selectMenu(param);
    }

    /**
     * 删除菜单信息
     *
     * @param param 菜单信息
     * @return 结果
     */
    @Override
    public int deleteMenu(SysMenu param) {
        return sysMenuDao.deleteMenu(param);
    }

    /**
     * 修改菜单信息
     *
     * @param param 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu param) {
        return sysMenuDao.updateMenu(param);
    }

    /**
     * 新增菜单信息
     *
     * @param param 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu param) {
        return sysMenuDao.insertMenu(param);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public Set<String> selectPermissionsByUserId(long userId) {
        return sysMenuDao.selectPermissionsByUserId(userId);
    }
}
