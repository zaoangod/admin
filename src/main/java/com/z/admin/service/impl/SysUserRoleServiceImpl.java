package com.z.admin.service.impl;

import com.z.admin.mapper.ISysUserRoleMapper;
import com.z.admin.model.SysUserRole;
import com.z.admin.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Date: 2019-11-13 17:16
 * @Author: jy
 */
@Service
public class SysUserRoleServiceImpl implements ISysUserRoleService {

    @Autowired
    private ISysUserRoleMapper sysUserRoleDao;

    /**
     * 删除用户角色信息
     *
     * @param param 用户角色信息
     * @return 结果
     */
    @Override
    public int deleteUserRole(SysUserRole param) {
        return sysUserRoleDao.deleteUserRole(param);
    }

    /**
     * 新增用户信息
     *
     * @param param 用户角色信息列表
     * @return 结果
     */
    @Override
    public int insertUserRole(List<SysUserRole> param) {
        return sysUserRoleDao.insertUserRole(param);
    }

}