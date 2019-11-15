package com.z.admin.service.impl;

import com.z.admin.mapper.ISysRoleMapper;
import com.z.admin.model.SysRole;
import com.z.admin.model.SysUser;
import com.z.admin.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Description: 角色Service
 * @Date: 2019-11-13 17:16
 * @Author: jy
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Autowired
    private ISysRoleMapper sysRoleDao;

    /**
     * 根据条件分页查询列表
     *
     * @param sysRole 角色信息
     * @return 角色信息集合信息
     */
    @Override
    public List<SysUser> selectRole(SysRole sysRole) {
        return sysRoleDao.selectRole(sysRole);
    }

    /**
     * 删除角色
     *
     * @param sysRole 角色信息
     * @return 结果
     */
    @Override
    public int deleteRole(SysRole sysRole) {
        return sysRoleDao.deleteRole(sysRole);
    }

    /**
     * 修改角色信息
     *
     * @param sysRole 角色信息
     * @return 结果
     */
    @Override
    public int updateRole(SysRole sysRole) {
        return sysRoleDao.updateRole(sysRole);
    }

    /**
     * 新增角色信息
     *
     * @param sysRole 角色信息
     * @return 结果
     */
    @Override
    public int insertRole(SysRole sysRole) {
        return sysRoleDao.insertRole(sysRole);
    }

    /**
     * 根据用户ID查询角色Key
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    @Override
    public Set<String> selectRoleKeyByUserId(long userId) {
        return sysRoleDao.selectRoleKeyByUserId(userId);
    }
}
