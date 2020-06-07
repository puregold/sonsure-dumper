/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

/**
 *
 * @author liyd
 * @date 17/4/14
 */
public interface Delete extends EntityConditionCommandExecutor<Delete> {

    /**
     * 指定表
     *
     * @param cls
     * @return
     */
    Delete from(Class<?> cls);

    /**
     * 执行
     *
     * @return
     */
    int execute();
}
