/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * Created by liyd on 17/4/14.
 */
public class DeleteImpl extends AbstractConditionCommandExecutor<Delete> implements Delete {

    protected DeleteContext deleteContext;

    public DeleteImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        deleteContext = new DeleteContext();
    }

    @Override
    public Delete from(Class<?> cls) {
        deleteContext.setModelClass(cls);
        return this;
    }

    public int execute() {
        CommandContext commandContext = this.commandContextBuilder.build(deleteContext, getJdbcEngineConfig());
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.DELETE);
    }

    @Override
    protected WhereContext getWhereContext() {
        return deleteContext;
    }
}
