package com.z.admin.service;

import com.z.admin.model.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Date: 2019-11-13 17:16
 * @Author: jy
 */
@Service
public interface ISysUserService {

    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUser(SysUser sysUser);

    /**
     * 删除用户信息
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    int deleteUser(SysUser sysUser);

    /**
     * 修改用户信息
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    int updateUser(SysUser sysUser);

    /**
     * 新增用户信息
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    int insertUser(SysUser sysUser);

}