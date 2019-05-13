package com.github.ontio.aop;

/**
 * @author zhouq
 * @version 1.0
 * @date 2019/5/13
 */

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
//最高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {
    /**
     * 允许访问的次数
     */
    int count() default 60;

    /**
     * 时间段，多少时间段内运行访问count次
     */
    long time() default 60;

}