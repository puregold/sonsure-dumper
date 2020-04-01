package com.sonsure.dumper.test.executor;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.ExecutorContext;
import com.sonsure.dumper.core.command.entity.AbstractCommandContextBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

public class CountCommandContextBuilder extends AbstractCommandContextBuilder {

    @Override
    public CommandContext doBuild(ExecutorContext executorContext, JdbcEngineConfig jdbcEngineConfig) {
        Class<?> clazz = executorContext.getModelClasses()[0];
        CommandContext commandContext = new CommandContext();
        commandContext.setCommand("select count(*) from " + clazz.getSimpleName());
        return commandContext;
    }
}