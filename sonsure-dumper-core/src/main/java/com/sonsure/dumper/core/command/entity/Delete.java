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
 * Created by liyd on 17/4/14.
 */
public interface Delete extends ConditionCommandExecutor<Delete> {

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
