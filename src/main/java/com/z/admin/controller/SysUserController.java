package com.z.admin.controller;

import com.z.admin.model.SysUser;
import com.z.admin.service.ISysUserService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Date: 2019-11-13 17:37
 * @Author: jy
 */
@RestController
@RequestMapping("SysUser")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @RequestMapping("list")
    @RequiresAuthentication
    //@RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public List<SysUser> list() {
        return userService.selectUser(new SysUser());
    }

}