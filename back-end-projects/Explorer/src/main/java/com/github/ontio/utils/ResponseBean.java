package com.github.ontio.utils;

import lombok.Data;

/**
 * @author zhouq
 * @version 1.0
 * @date 2019/5/13
 */
@Data
public class ResponseBean {


    private Integer code;

    private String msg;

    private Object result;

    public ResponseBean(Integer code, String msg, Object result){
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public ResponseBean(){}
}
