package com.hp.sh.expv3.pj.module.dataSummarization.vo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/6/12
 */
public class UserRegisterCountVo {
    private int code;
    private UserRegisterCountData data;
    private String input;
    private String traceId;
    private int cost;
    private String error;
    private String msg;
    private Long time;

    public UserRegisterCountVo() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserRegisterCountData getData() {
        return data;
    }

    public void setData(UserRegisterCountData data) {
        this.data = data;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
