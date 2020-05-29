/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.commons.bean.BeanKit;
import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.persist.PersistExecutor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * Created by liyd on 17/4/12.
 */
public class SelectImpl extends AbstractConditionCommandExecutor<Select> implements Select {

    protected SelectContext selectContext;

    public SelectImpl(JdbcEngineConfig jdbcEngineConfig) {
        this(jdbcEngineConfig, null);
    }


    public SelectImpl(JdbcEngineConfig jdbcEngineConfig, String[] fields) {
        super(jdbcEngineConfig);
        selectContext = new SelectContext();
        if (fields != null) {
            selectContext.addSelectFields(fields);
        }
    }

    @Override
    public Select from(Class<?> cls) {
        selectContext.addFromClass(cls);
        return this;
    }

    @Override
    public Select from(Class<?> cls, String alias, Object... clsAndAlias) {
        selectContext.addFromClass(cls, alias);
        if (ArrayUtils.isNotEmpty(clsAndAlias)) {
            if (ArrayUtils.getLength(clsAndAlias) % 2 != 0) {
                throw new SonsureJdbcException("指定多表必须一个class一个别名对应");
            }
            for (int i = 0; i < clsAndAlias.length; i++) {
                Object clazz = clsAndAlias[i];
                Object aliasName = clsAndAlias[++i];
                if (!(clazz instanceof Class) || !(aliasName instanceof String)) {
                    throw new SonsureJdbcException("指定多表必须一个class(Class类型)一个别名(String类型)对应");
                }
                selectContext.addFromClass(((Class) clazz), ((String) aliasName));
            }
        }
        return this;
    }

    @Override
    public Select exclude(String... fields) {
        selectContext.addExcludeFields(fields);
        return this;
    }

    @Override
    public Select groupBy(String... fields) {
        selectContext.addGroupByField(fields);
        return this;
    }

    @Override
    public Select orderBy(String... fields) {
        selectContext.addOrderByField(fields);
        return this;
    }

    @Override
    public <E, R> Select orderBy(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        this.orderBy(fields);
        return this;
    }

    @Override
    public Select asc() {
        this.selectContext.setOrderByType("asc");
        return this;
    }

    @Override
    public Select desc() {
        this.selectContext.setOrderByType("desc");
        return this;
    }

    @Override
    public Select paginate(int pageNum, int pageSize) {
        this.selectContext.paginate(pageNum, pageSize);
        return this;
    }

    @Override
    public Select paginate(Pageable pageable) {
        this.selectContext.paginate(pageable);
        return this;
    }

    @Override
    public Select limit(int offset, int size) {
        this.selectContext.limit(offset, size);
        return this;
    }

    @Override
    public Select isCount(boolean isCount) {
        selectContext.isCount(isCount);
        return this;
    }

    @Override
    public long count() {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        PersistExecutor persistExecutor = this.jdbcEngineConfig.getPersistExecutor();
        String countCommand = this.jdbcEngineConfig.getPageHandler().getCountCommand(commandContext.getCommand(), persistExecutor.getDialect());
        CommandContext countCommandContext = BeanKit.copyProperties(new CommandContext(), commandContext);
        countCommandContext.setCommand(countCommand);
        countCommandContext.setResultType(Long.class);
        Object result = persistExecutor.execute(countCommandContext, CommandType.QUERY_ONE_COL);
        return (Long) result;
    }

    @Override
    public <T> T singleResult(Class<T> cls) {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        commandContext.setResultType(cls);
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_SINGLE_RESULT);
    }

    @Override
    public Object singleResult() {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        return this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
    }

    @Override
    public <T> T firstResult(Class<T> cls) {
        this.paginate(1, 1).isCount(false);
        Page<T> page = this.pageResult(cls);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    @Override
    public Object firstResult() {
        this.paginate(1, 1).isCount(false);
        Page<Object> page = this.pageResult();
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    @Override
    public <E> E oneColResult(Class<E> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (E) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
    }

    @Override
    public <E> List<E> oneColList(Class<E> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (List<E>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL_LIST);
    }

    @Override
    public <T> T oneColFirstResult(Class<T> clazz) {
        this.paginate(1, 1).isCount(false);
        Page<T> page = this.oneColPageResult(clazz);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    @Override
    public <T> List<T> list(Class<T> cls) {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        commandContext.setResultType(cls);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_LIST);
    }

    @Override
    public List<Object> list() {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        return (List<Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
    }

    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        commandContext.setResultType(cls);
        return this.doPageResult(commandContext, selectContext.getPagination(), selectContext.isCount(), new PageQueryHandler<T>() {
            @Override
            public List<T> queryList(CommandContext commandContext) {
                return (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_LIST);
            }
        });
    }

    @Override
    public Page<Object> pageResult() {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        return this.doPageResult(commandContext, selectContext.getPagination(), selectContext.isCount(), new PageQueryHandler<Object>() {
            @Override
            public List<Object> queryList(CommandContext commandContext) {
                return (List<Object>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
            }
        });
    }

    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.selectContext, getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return this.doPageResult(commandContext, selectContext.getPagination(), selectContext.isCount(), new PageQueryHandler<T>() {
            @Override
            public List<T> queryList(CommandContext commandContext) {
                return (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL_LIST);
            }
        });
    }

    @Override
    protected AbstractWhereContext getWhereContext() {
        return this.selectContext;
    }

}
