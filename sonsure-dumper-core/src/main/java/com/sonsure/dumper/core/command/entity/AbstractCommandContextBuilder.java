package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilder;
import com.sonsure.dumper.core.command.ExecutorContext;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.management.ClassField;
import com.sonsure.dumper.core.management.ClassFieldWrapper;
import com.sonsure.dumper.core.management.ModelClassCache;
import com.sonsure.dumper.core.management.ModelFieldMeta;
import com.sonsure.dumper.core.mapping.AbstractMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by liyd on 17/4/12.
 */
public abstract class AbstractCommandContextBuilder implements CommandContextBuilder {

    public CommandContext build(ExecutorContext executorContext, JdbcEngineConfig jdbcEngineConfig) {

        CommandContext commandContext = this.doBuild(executorContext, jdbcEngineConfig);
        MappingHandler mappingHandler = jdbcEngineConfig.getMappingHandler();
        if (mappingHandler instanceof AbstractMappingHandler) {
            Class<?>[] modelClasses = executorContext.getModelClasses();
            ((AbstractMappingHandler) mappingHandler).addClassMapping(modelClasses);
        }

//        commandContext.setCommandCase(commandTable.getCommandCase());
        String resolvedCommand = commandContext.getCommand();
        Map<String, Object> params = new HashMap<>();
//        if (!commandTable.isForceNative()) {
        resolvedCommand = jdbcEngineConfig.getCommandConversionHandler().convert(commandContext.getCommand(), params);
//        }
        commandContext.setCommand(resolvedCommand);
        return commandContext;
    }

    /**
     * 构建执行内容
     *
     * @param executorContext  the executor context
     * @param jdbcEngineConfig the jdbc engine config
     * @return command context
     */
    public abstract CommandContext doBuild(ExecutorContext executorContext, JdbcEngineConfig jdbcEngineConfig);

    /**
     * 获取带别名的field
     *
     * @param tableAlias the table alias
     * @param field      the field
     * @return table alias field
     */
    protected String getTableAliasField(String tableAlias, String field) {
        if (StringUtils.isNotBlank(tableAlias)) {
            return new StringBuilder(tableAlias).append(".").append(field).toString();
        }
        return field;
    }

//    /**
//     * 获取带表别名的原生属性列
//     *
//     * @param commandTable
//     * @param nativeField
//     * @return
//     */
//    protected String getTableAliasNativeField(CommandTable commandTable, String nativeField) {
//        if (StringUtils.isNotBlank(commandTable.getTableAlias())) {
//            return new StringBuilder(commandTable.getTableAlias()).append(".").append(nativeField).toString();
//        }
//        return nativeField;
//    }

    /**
     * 获取带别名的model名
     *
     * @param modelClass the model class
     * @param tableAlias the table alias
     * @return column table alias name
     */
    protected String getModelAliasName(Class<?> modelClass, String tableAlias) {
        StringBuilder sb = new StringBuilder(modelClass.getSimpleName());
        if (StringUtils.isNotBlank(tableAlias)) {
            sb.append(" ").append(tableAlias);
        }
        return sb.toString();
    }

    protected String getModelName(Class<?> modelClass) {
        return modelClass.getSimpleName();
    }

    protected String getPkField(Class<?> modelClass, MappingHandler mappingHandler) {
        return mappingHandler.getPkField(modelClass);
    }

//    protected String getTableName(ExecutorContext commandTable, Map<String, Object> params) {
//        return this.getCommandExecutor().getMappingHandler().getTable(commandTable.getModelClass(), params);
//    }

    /**
     * 获取class的属性
     *
     * @return
     */
    protected Collection<ModelFieldMeta> getClassFields(Class<?> clazz) {
        return ModelClassCache.getClassFieldMetas(clazz);
    }

    /**
     * 获取设置了通用参数的CommandContext
     *
     * @param executorContext the executor context
     * @return generic command context
     */
    protected CommandContext getCommonCommandContext(ExecutorContext executorContext) {
        CommandContext commandContext = new CommandContext();
//        commandContext.setModelClass(commandTable.getModelClass());
//        commandContext.setResultType(commandTable.getResultType());
//        String pkField = this.getPkField(commandTable);
//        commandContext.setPkField(pkField);
//        String pkColumn = this.getCommandExecutor().getMappingHandler().getColumn(commandTable.getModelClass(), pkField);
//        commandContext.setPkColumn(pkColumn);
//        if (this.commandExecutor.getKeyGenerator() == null || this.commandExecutor.getKeyGenerator().isPkValueByDb()) {
//            commandContext.setPkValueByDb(true);
//        } else {
//            commandContext.setPkValueByDb(false);
//        }
        return commandContext;
    }

//    /**
//     * 判断是否原生属性
//     *
//     * @param classField the class field
//     * @return object[] 元素见下说明
//     * 0 是否原生属性
//     * 1 是否原生value
//     * 2 field名
//     * 3 带表别名的field名 如果没有表别名，field名一致
//     * 4 解析过的value 只对String有效
//     */
//    protected ClassFieldNativeWrapper decideNativeField(ClassField classField) {
//
//        String field = classField.getName();
//        Object val = classField.getValue();
//        boolean isNativeField = false;
//        boolean isNativeValue = false;
//        if (StringUtils.startsWith(field, NATIVE_FIELD_OPEN_TOKEN) && StringUtils.endsWith(field, NATIVE_FIELD_CLOSE_TOKEN)) {
//            isNativeField = true;
//            isNativeValue = true;
//            field = StringUtils.substring(field, NATIVE_FIELD_OPEN_TOKEN.length(), field.length() - NATIVE_FIELD_CLOSE_TOKEN.length());
//        } else if (StringUtils.startsWith(field, NATIVE_CONTENT_OPEN_TOKEN) && StringUtils.endsWith(field, NATIVE_CONTENT_CLOSE_TOKEN)) {
//            isNativeField = true;
//            field = StringUtils.substring(field, NATIVE_CONTENT_OPEN_TOKEN.length(), field.length() - NATIVE_CONTENT_CLOSE_TOKEN.length());
//        }
//        String aliasField = this.getTableAliasField(classField.getTableAlias(), field);
//        return new ClassFieldNativeWrapper(field, aliasField, val, isNativeField, isNativeValue);
//    }

    /**
     * 构建where部分sql
     *
     * @param whereContext the where context
     * @return string command context
     */
    protected CommandContext buildWhereSql(WhereContext whereContext) {
        List<ClassField> whereFields = whereContext.getWhereFields();
        if (whereFields == null || whereFields.isEmpty()) {
            return null;
        }

        StringBuilder whereCommand = new StringBuilder(" ");
        List<Object> parameters = new ArrayList<>();
        for (ClassField classField : whereFields) {

            //在前面处理，有单独where or and 的情况
            if (StringUtils.isNotBlank(classField.getLogicalOperator())) {
                //没有where不管如何and or等操作符都换成where
                if (whereCommand.length() < 5) {
                    whereCommand.append("where ");
                } else {
                    whereCommand.append(classField.getLogicalOperator()).append(" ");
                }
            }
            //只有where or and 的情况
            if (StringUtils.isBlank(classField.getName())) {
                continue;
            }

            ClassFieldWrapper fieldWrapper = new ClassFieldWrapper(classField);

            if (fieldWrapper.getValue() == null) {
                String operator = StringUtils.isBlank(classField.getFieldOperator()) ? "is" : classField.getFieldOperator();
                whereCommand.append(this.getTableAliasField(fieldWrapper.getTableAlias(), fieldWrapper.getFieldName()))
                        .append(" ")
                        .append(operator)
                        .append(" null ");
            } else if (fieldWrapper.getValue() instanceof Object[]) {
                this.processArrayArgs(fieldWrapper, whereCommand, parameters);
            } else {
                whereCommand.append(this.getTableAliasField(fieldWrapper.getTableAlias(), fieldWrapper.getFieldName()))
                        .append(" ")
                        .append(classField.getFieldOperator())
                        .append(" ");

                //native 不传参方式
                if (fieldWrapper.isNative()) {
                    whereCommand.append(fieldWrapper.isFieldOperatorNeedBracket() ? String.format(" ( %s ) ", fieldWrapper.getValue()) : String.format(" %s ", fieldWrapper.getValue()));
                } else {
                    whereCommand.append(fieldWrapper.isFieldOperatorNeedBracket() ? " ( ? ) " : " ? ");
                    parameters.add(fieldWrapper.getValue());
                }
            }
        }
        //只有where的情况
        if (whereCommand.length() < 8) {
            whereCommand.delete(0, whereCommand.length());
        }
        CommandContext commandContext = new CommandContext();
        commandContext.setCommand(whereCommand.toString());
        commandContext.addParameters(parameters);
        return commandContext;
    }

    /**
     * 构建group by部分sql
     *
     * @param selectContext the select context
     * @return string
     */
    protected String buildGroupBySql(SelectContext selectContext) {
        List<ClassField> groupByFields = selectContext.getGroupByFields();
        if (groupByFields == null || groupByFields.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" group by ");
        for (ClassField groupByField : groupByFields) {
            String aliasField = this.getTableAliasField(groupByField.getTableAlias(), groupByField.getName());
            sb.append(aliasField).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 构建order by部分sql
     *
     * @param selectContext the select context
     * @return string
     */
    protected String buildOrderBySql(SelectContext selectContext) {

        List<ClassField> orderByFields = selectContext.getOrderByFields();
        if (orderByFields == null || orderByFields.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" order by ");
        for (ClassField orderByField : orderByFields) {
            String aliasField = this.getTableAliasField(orderByField.getTableAlias(), orderByField.getName());
            sb.append(aliasField).append(" ").append(orderByField.getFieldOperator()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


    /**
     * 处理数组参数
     */
    protected void processArrayArgs(ClassFieldWrapper fieldWrapper, StringBuilder whereCommand, List<Object> parameters) {
        String aliasField = this.getTableAliasField(fieldWrapper.getTableAlias(), fieldWrapper.getFieldName());
        Object[] args = (Object[]) fieldWrapper.getValue();
        if (fieldWrapper.isFieldOperatorNeedBracket()) {
            whereCommand.append(aliasField).append(" ").append(fieldWrapper.getFieldOperator()).append(" (");
            for (int i = 0; i < args.length; i++) {
                if (fieldWrapper.isNative()) {
                    whereCommand.append(args[i]);
                } else {
                    whereCommand.append("?");
                    parameters.add(args[i]);
                }
                if (i != args.length - 1) {
                    whereCommand.append(",");
                }
            }
            whereCommand.append(") ");
        } else {
            if (ArrayUtils.getLength(args) > 1) {
                whereCommand.append(" (");
            }
            for (int i = 0; i < args.length; i++) {
                whereCommand.append(aliasField).append(" ").append(fieldWrapper.getFieldOperator());
                if (fieldWrapper.isNative()) {
                    whereCommand.append(String.format(" %s ", args[i]));
                } else {
                    whereCommand.append(" ? ");
                    parameters.add(args[i]);
                }
                if (i != args.length - 1) {
                    whereCommand.append(" or ");
                }
            }
            if (ArrayUtils.getLength(args) > 1) {
                whereCommand.append(") ");
            }
        }
    }
}
