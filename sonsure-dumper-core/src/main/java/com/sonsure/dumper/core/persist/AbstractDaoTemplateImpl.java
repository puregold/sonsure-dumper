/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.config.JdbcEngine;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author liyd
 * @date 17/4/13
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDaoTemplateImpl implements JdbcDao {

    protected DataSource dataSource;

    protected JdbcEngine defaultJdbcEngine;

    protected Map<String, JdbcEngine> jdbcEngineMap;

    protected boolean globalJdbc = false;

    @Override
    public DaoTemplate use(String name) {
        if (jdbcEngineMap == null) {
            throw new SonsureJdbcException("使用多数据源模式请先初始化jdbcEngineMap属性");
        }
        JdbcEngine jdbcEngine = jdbcEngineMap.get(name);
        if (jdbcEngine == null) {
            throw new SonsureJdbcException("指定的数据源操作对象不存在");
        }
        return new FlexibleDaoTemplate(jdbcEngine);
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id) {
        return this.getDefaultJdbcEngine().get(entityClass, id);
    }

    @Override
    public <T> List<T> find(Class<T> entityClass) {
        return this.getDefaultJdbcEngine().find(entityClass);
    }

    @Override
    public <T> List<T> find(T entity) {
        return this.getDefaultJdbcEngine().find(entity);
    }

    @Override
    public <T extends Pageable> Page<T> pageResult(T entity) {
        return this.getDefaultJdbcEngine().pageResult(entity);
    }

    @Override
    public long findCount(Object entity) {
        return this.getDefaultJdbcEngine().findCount(entity);
    }

    @Override
    public long findCount(Class<?> cls) {
        return this.getDefaultJdbcEngine().findCount(cls);
    }

    @Override
    public <T> T singleResult(T entity) {
        return (T) this.getDefaultJdbcEngine().singleResult(entity);
    }

    @Override
    public <T> T firstResult(T entity) {
        return (T) this.getDefaultJdbcEngine().firstResult(entity);
    }

    @Override
    public Object executeInsert(Object entity) {
        return this.getDefaultJdbcEngine().executeInsert(entity);
    }

    @Override
    public Insert insertInto(Class<?> cls) {
        return this.getDefaultJdbcEngine().insertInto(cls);
    }

    @Override
    public int executeDelete(Class<?> entityClass, Serializable id) {
        return this.getDefaultJdbcEngine().executeDelete(entityClass, id);
    }

    @Override
    public int executeDelete(Object entity) {
        return this.getDefaultJdbcEngine().executeDelete(entity);
    }

    @Override
    public int executeDelete(Class<?> cls) {
        return this.getDefaultJdbcEngine().executeDelete(cls);
    }

    @Override
    public int executeUpdate(Object entity) {
        return this.getDefaultJdbcEngine().executeUpdate(entity);
    }

    @Override
    public Update update(Class<?> cls) {
        return this.getDefaultJdbcEngine().update(cls);
    }

    @Override
    public Select selectFrom(Class<?> cls) {
        return this.getDefaultJdbcEngine().selectFrom(cls);
    }

    @Override
    public Select select() {
        return this.getDefaultJdbcEngine().select();
    }

    @Override
    public Select select(String... fields) {
        return this.getDefaultJdbcEngine().select(fields);
    }

    @Override
    public <E, R> Select select(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        return this.select(fields);
    }

    @Override
    public Insert insert() {
        return this.getDefaultJdbcEngine().insert();
    }

    @Override
    public Delete delete() {
        return this.getDefaultJdbcEngine().delete();
    }

    @Override
    public Delete deleteFrom(Class<?> cls) {
        return this.getDefaultJdbcEngine().deleteFrom(cls);
    }

    @Override
    public Update update() {
        return this.getDefaultJdbcEngine().update();
    }

    @Override
    public BatchUpdateExecutor batchUpdate() {
        return this.getDefaultJdbcEngine().batchUpdate();
    }

    @Override
    public <T> Object executeBatchUpdate(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        return this.getDefaultJdbcEngine().executeBatchUpdate(command, batchData, batchSize, parameterizedSetter);
    }

    @Override
    public void executeScript(String script) {
        this.nativeExecutor()
                .command(script)
                .nativeCommand()
                .executeScript();
    }

    @Override
    public NativeExecutor nativeExecutor() {
        return this.getDefaultJdbcEngine().createExecutor(NativeExecutor.class);
    }

    @Override
    public MybatisExecutor myBatisExecutor() {
        return this.getDefaultJdbcEngine().createExecutor(MybatisExecutor.class);
    }

    @Override
    public <T extends CommandExecutor> T executor(Class<T> executor) {
        return this.getDefaultJdbcEngine().createExecutor(executor);
    }

    public JdbcEngine getDefaultJdbcEngine() {
        if (this.defaultJdbcEngine == null) {
            throw new SonsureJdbcException("jdbcEngine不能为空");
        }
        return this.defaultJdbcEngine;
    }

    public void enableGlobalJdbc() {
        this.globalJdbc = true;
        Jdbc.setDefaultJdbcEngine(this.defaultJdbcEngine);
        if (this.jdbcEngineMap != null) {
            Jdbc.addJdbcEngine(this.jdbcEngineMap);
        }
    }

    public DataSource getDataSource() {
        return dataSource != null ? dataSource : this.getDefaultJdbcEngine().getDataSource();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDefaultJdbcEngine(JdbcEngine jdbcEngine) {
        this.defaultJdbcEngine = jdbcEngine;
    }

    public Map<String, JdbcEngine> getJdbcEngineMap() {
        return jdbcEngineMap;
    }

    public void setJdbcEngineMap(Map<String, JdbcEngine> jdbcEngineMap) {
        this.jdbcEngineMap = jdbcEngineMap;
    }

    public boolean isGlobalJdbc() {
        return globalJdbc;
    }

    public void setGlobalJdbc(boolean globalJdbc) {
        this.globalJdbc = globalJdbc;
    }
}
