package com.z.admin.controller;

import com.z.admin.config.shiro.jwt.JwtUtil;
import com.z.admin.model.SysUser;
import com.z.admin.service.ISysUserService;
import com.z.admin.util.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Date: 2019-11-13 17:37
 * @Author: jy
 */
@Slf4j
@Controller
public class SysLoginController {

    @Autowired
    private ISysUserService userService;

    @GetMapping("login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "error/unauthorized";
    }

    @ResponseBody
    @PostMapping("/login")
    public AjaxResult login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse httpServletResponse) {
        log.info("=> 登录");
        log.info("=> username:   {}", username);
        log.info("=> password:   {}", password);
        SysUser user = new SysUser();
        user = userService.selectUser(user.setLoginName(username)).stream().findFirst().orElse(null);
        if (user == null) {
            return AjaxResult.fail("登录用户不存在", null);
        }
        if (!user.getPassword().equals(password)) {
            return AjaxResult.fail("密码错误", null);
        }
        //生成token
        String token = JwtUtil.sign(username, password);
        httpServletResponse.setHeader("Authorization", token);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        log.info("=> token: {}", token);
        log.info("=> 登录成功");
        return AjaxResult.success("登录成功", token);
    }

}