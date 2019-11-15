package com.z.admin.service;

import com.z.admin.model.SysRole;
import com.z.admin.model.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Description: 角色Service
 * @Date: 2019-11-13 17:16
 * @Author: jy
 */
@Service
public interface ISysRoleService {

    /**
     * 根据条件分页查询列表
     *
     * @param sysRole 角色信息
     * @return 角色信息集合信息
     */
    List<SysUser> selectRole(SysRole sysRole);

    /**
     * 删除角色
     *
     * @param sysRole 角色信息
     * @return 结果
     */
    int deleteRole(SysRole sysRole);

    /**
     * 修改角色信息
     *
     * @param sysRole 角色信息
     * @return 结果
     */
    int updateRole(SysRole sysRole);

    /**
     * 新增角色信息
     *
     * @param sysRole 角色信息
     * @return 结果
     */
    int insertRole(SysRole sysRole);

    /**
     * 根据用户ID查询角色Key
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    Set<String> selectRoleKeyByUserId(long userId);

}