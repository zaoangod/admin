package com.z.admin.util;

import java.util.HashMap;

/**
 * 操作消息
 */
public class AjaxResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static final String CODE_TAG = "code";

    public static final String MSG_TAG = "msg";

    public static final String DATA_TAG = "data";

    //状态码
    private int    code;
    //返回内容
    private String msg;
    //数据对象
    private Object data;

    public AjaxResult() {
    }

    /**
     * 初始化一个新创建的
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public AjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(0, msg, data);
    }

    /**
     * 返回成功消息
     *
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(Object data) {
        return new AjaxResult(0, "操作成功", data);
    }

    /**
     * 返回成功消息
     */
    public static AjaxResult success() {
        return AjaxResult.success("操作成功", null);
    }

    /**
     * 返回失败消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 失败消息
     */
    public static AjaxResult fail(String msg, Object data) {
        return new AjaxResult(1, msg, data);
    }

    /**
     * 返回失败消息
     *
     * @param data 数据对象
     * @return 失败消息
     */
    public static AjaxResult fail(Object data) {
        return new AjaxResult(1, "操作失败", data);
    }

    /**
     * 返回失败消息
     *
     * @return 失败消息
     */
    public static AjaxResult fail() {
        return new AjaxResult(1, "操作失败", null);
    }

}
