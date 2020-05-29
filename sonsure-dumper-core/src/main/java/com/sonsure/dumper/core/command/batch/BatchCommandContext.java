/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.CommandContext;

import java.util.Collection;

/**
 * The type Batch command context.
 *
 * @author liyd
 */
public class BatchCommandContext<T> extends CommandContext {

    /**
     * The Batch data.
     */
    private Collection<T> batchData;

    /**
     * The Batch size.
     */
    private int batchSize;

    /**
     * The Parameterized setter.
     */
    private ParameterizedSetter<T> parameterizedSetter;

    public BatchCommandContext() {
    }

    public BatchCommandContext(BatchUpdateExecutorContext<T> batchUpdateExecutorContext) {
        this.setCommand(batchUpdateExecutorContext.getCommand());
        this.batchData = batchUpdateExecutorContext.getBatchData();
        this.batchSize = batchUpdateExecutorContext.getBatchSize();
        this.parameterizedSetter = batchUpdateExecutorContext.getParameterizedSetter();
    }

    public Collection<T> getBatchData() {
        return batchData;
    }

    public void setBatchData(Collection<T> batchData) {
        this.batchData = batchData;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public ParameterizedSetter<T> getParameterizedSetter() {
        return parameterizedSetter;
    }

    public void setParameterizedSetter(ParameterizedSetter<T> parameterizedSetter) {
        this.parameterizedSetter = parameterizedSetter;
    }
}
