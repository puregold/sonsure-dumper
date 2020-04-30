/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;


import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liyd on 17/4/12.
 */
public interface JdbcEngine {

    /**
     * jdbc 配置
     *
     * @return
     */
    JdbcEngineConfig getJdbcEngineConfig();


    /**
     * 创建执行器
     *
     * @param <T>                  the type parameter
     * @param commandExecutorClass 执行器class
     * @param param                the param
     * @return t
     */
    <T extends CommandExecutor> T createExecutor(Class<T> commandExecutorClass, Object param);

    /**
     * 创建执行器
     *
     * @param <T>                  the type parameter
     * @param commandExecutorClass 执行器class
     * @return t
     */
    <T extends CommandExecutor> T createExecutor(Class<T> commandExecutorClass);

    /**
     * insert对象
     *
     * @return
     */
    Insert insert();

    /**
     * insert数据
     *
     * @param entity
     * @return
     */
    Object executeInsert(Object entity);

    /**
     * 创建insert对象后指定into对象
     *
     * @param cls
     * @return
     */
    Insert insertInto(Class<?> cls);

    /**
     * select对象
     *
     * @return
     */
    Select select();

    /**
     * select对象
     *
     * @param fields the fields
     * @return select
     */
    Select select(String... fields);


    /**
     * 查询所有列表
     *
     * @param cls
     * @param <T>
     * @return
     */
    <T> List<T> find(Class<T> cls);

    /**
     * 查询列表，以entity中不为null属性为where条件
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> List<T> find(T entity);

    /**
     * 查询分页列表,以entity中不为null属性为where条件
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T extends Pageable> Page<T> pageResult(T entity);

    /**
     * 查询记录数
     *
     * @param entity
     * @return
     */
    long findCount(Object entity);

    /**
     * 查询记录数
     *
     * @param cls the cls
     * @return long
     */
    long findCount(Class<?> cls);

    /**
     * 查询单个结果
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> T singleResult(T entity);

    /**
     * 第一个结果
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> T firstResult(T entity);


    /**
     * update对象
     *
     * @return
     */
    Update update();

    /**
     * update对象
     *
     * @return
     */
    Update update(Class<?> cls);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    int executeUpdate(Object entity);

    /**
     * delete对象
     *
     * @return
     */
    Delete delete();

    /**
     * delete对象
     *
     * @param cls the cls
     * @return delete
     */
    Delete deleteFrom(Class<?> cls);

    /**
     * delete对象，以entity中不为null属性做为条件
     *
     * @param entity
     * @return
     */
    int executeDelete(Object entity);

    /**
     * delete对象，以entity中不为null属性做为条件
     *
     * @param cls the cls
     * @param id  the id
     * @return int
     */
    int executeDelete(Class<?> cls, Serializable id);

    /**
     * 删除对象
     *
     * @param cls
     * @return
     */
    int executeDelete(Class<?> cls);

    /**
     * 创建select后指定from
     *
     * @param cls
     * @return
     */
    Select selectFrom(Class<?> cls);


    /**
     * 直接get对象
     *
     * @param cls
     * @param id
     * @param <T>
     * @return
     */
    <T> T get(Class<T> cls, Serializable id);


}
