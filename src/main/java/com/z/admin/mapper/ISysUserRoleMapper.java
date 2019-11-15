package com.z.admin.mapper;

import com.z.admin.model.SysUserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISysUserRoleMapper {

    /**
     * 删除用户角色信息
     *
     * @param param 用户角色信息
     * @return 结果
     */
    int deleteUserRole(SysUserRole param);

    /**
     * 新增用户信息
     *
     * @param param 用户角色信息列表
     * @return 结果
     */
    int insertUserRole(List<SysUserRole> param);
}