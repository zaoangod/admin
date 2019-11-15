package com.z.admin.controller;

import com.z.admin.util.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 通用处理
 * @Date: 2019-11-14 15:42
 * @Author: jy
 */
@Slf4j
@RestController
@RequestMapping("common")
public class CommonController {
    /**
     * 没有权限
     */
    @GetMapping("403")
    public AjaxResult unauthorized() {
        log.error("=> 没有权限，请联系管理员授权");
        return new AjaxResult(500, "没有权限，请联系管理员授权", null);
    }
}
