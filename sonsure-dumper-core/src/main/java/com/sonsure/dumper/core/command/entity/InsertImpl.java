/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.commons.utils.ClassUtils;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Map;

/**
 * @author liyd
 * @date 17/4/14
 */
public class InsertImpl extends AbstractEntityCommandExecutor<Insert> implements Insert {

    public InsertImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Insert into(Class<?> cls) {
        this.getCommandExecutorContext().addModelClass(cls);
        return this;
    }

    @Override
    public Insert set(String field, Object value) {
        this.getCommandExecutorContext().addInsertField(field, value);
        return this;
    }

    @Override
    public <E, R> Insert set(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.getCommandExecutorContext().addInsertField(field, value);
        return this;
    }

    @Override
    public Insert forEntity(Object entity) {
        this.into(entity.getClass());
        Map<String, Object> beanPropMap = ClassUtils.getSelfBeanPropMap(entity, Transient.class);
        for (Map.Entry<String, Object> entry : beanPropMap.entrySet()) {
            //忽略掉null
            if (entry.getValue() == null) {
                continue;
            }
            this.getCommandExecutorContext().addInsertField(entry.getKey(), entry.getValue(), entity.getClass());
        }
        return this;
    }

    @Override
    public Object execute() {
        CommandContext commandContext = this.commandContextBuilder.build(getCommandExecutorContext(), getJdbcEngineConfig());
        return getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.INSERT);
    }

}
