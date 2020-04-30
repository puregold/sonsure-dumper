/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.simple;

/**
 * @author liyd
 */
public interface ResultHandler<T> {

    /**
     * 处理结果
     *
     * @param object
     * @return
     */
    T handle(Object object);
}
