package com.z.admin.service.impl;

import com.z.admin.mapper.ISysUserMapper;
import com.z.admin.model.SysUser;
import com.z.admin.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Date: 2019-11-13 17:16
 * @Author: jy
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private ISysUserMapper sysUserDao;

    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUser(SysUser sysUser) {
        return sysUserDao.selectUser(sysUser);
    }

    /**
     * 通过用户ID删除用户
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    @Override
    public int deleteUser(SysUser sysUser) {
        return sysUserDao.deleteUser(sysUser);
    }

    /**
     * 修改用户信息
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    @Override
    public int updateUser(SysUser sysUser) {
        return sysUserDao.updateUser(sysUser);
    }

    /**
     * 新增用户信息
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    @Override
    public int insertUser(SysUser sysUser) {
        return sysUserDao.insertUser(sysUser);
    }

}
