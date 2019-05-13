package com.github.ontio.aop;


import com.github.ontio.exception.ExplorerException;
import com.github.ontio.utils.ConfigParam;
import com.github.ontio.utils.ErrorInfo;
import com.github.ontio.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;



/**
 * @author zhouq
 * @version 1.0
 * @date 2019/5/13
 */
@Component
@Aspect
@Slf4j
public class RequestLimitAspect {

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Before("execution(public * com.github.ontio.controller.*.*(..)) && @annotation(limit)")
    public void requestLimit(JoinPoint joinpoint, RequestLimit limit) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String ip = Helper.getIpAddr(request);
        String url = request.getRequestURL().toString();
        String key = "reqLimit_".concat(url).concat("_").concat(ip);
        //加1后看看值
        long count = redisTemplate.opsForValue().increment(key, 1);
        log.info("reqLimit_key:{},count:{}",key,count);
        //刚创建
        if (count == 1) {
            //设置xx秒过期
            redisTemplate.expire(key, configParam.REQLIMIT_EXPIRE_SECOND, TimeUnit.SECONDS);
        }
        if (count > limit.count()) {
            log.warn("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
            throw new ExplorerException(((Long) ErrorInfo.REQ_EXCEED.code()).intValue(), ErrorInfo.REQ_EXCEED.desc(), false);
        }
    }

}