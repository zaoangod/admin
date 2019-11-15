package com.z.admin.mapper;

import com.z.admin.model.SysRole;
import com.z.admin.model.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @Description: 角色Mapper
 * @Date: 2019-11-13 16:57
 * @Author: jy
 */
@Repository
public interface ISysRoleMapper {

    /**
     * 查询角色列表
     *
     * @param sysRole 角色信息
     * @return 角色信息集合信息
     */
    List<SysUser> selectRole(SysRole sysRole);

    /**
     * 删除角色信息
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