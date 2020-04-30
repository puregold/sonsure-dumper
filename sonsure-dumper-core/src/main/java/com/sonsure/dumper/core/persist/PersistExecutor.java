/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;

/**
 * 持久化执行
 * <p>
 * Created by liyd on 17/4/11.
 */
public interface PersistExecutor {

    /**
     * 获取数据库方言
     *
     * @return
     */
    String getDialect();

    /**
     * 执行command
     *
     * @param commandContext
     * @param commandType
     * @return
     */
    Object execute(CommandContext commandContext, CommandType commandType);

}
