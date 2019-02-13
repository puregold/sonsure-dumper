package com.sonsure.dumper.core.command.sql;

import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.mapping.MappingHandler;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandMappingHandler {

    /**
     * 没有表别名时的默认别名 如单表查询时
     */
    private static final String DEFAULT_ALIAS = "_default_alias";

    protected Map<String, Class<?>> classMapping;

    protected Map<String, Class<?>> customClassMapping;

    private MappingHandler mappingHandler;

    private Map<Column, ColumnMapping> mappingColumns = new HashMap<>();
    private Map<Table, TableMapping> mappingTables = new HashMap<>();

    public CommandMappingHandler(Statement statement, MappingHandler mappingHandler, Map<String, Class<?>> classMapping, Map<String, Class<?>> customClassMapping) {
        this.mappingHandler = mappingHandler;
        this.classMapping = classMapping;
        this.customClassMapping = customClassMapping;
        this.extractMappings(statement);
    }

    protected void extractMappings(Statement statement) {

        Map<String, Object> mappings = new HashMap<>();
        if (statement instanceof Select) {
            Select select = (Select) statement;
            SelectBody selectBody = select.getSelectBody();
            this.extractMappings(selectBody, mappings);
        } else if (statement instanceof Insert) {

            Insert insert = (Insert) statement;
            this.extractTableMappings(insert.getTable(), mappings);

            this.extractColumnMapping(insert.getColumns(), mappings);

            Select select = insert.getSelect();
            if (select != null) {
                this.extractMappings(select);
            }
        } else if (statement instanceof Update) {

            Update update = (Update) statement;

            this.extractTableMappings(update.getTables(), mappings);
            this.extractColumnMapping(update.getColumns(), mappings);
            this.extractExpression(update.getExpressions(), mappings);
            this.extractExpression(update.getWhere(), mappings);

        } else if (statement instanceof Delete) {

            Delete delete = (Delete) statement;
            this.extractTableMappings(delete.getTable(), mappings);
            this.extractTableMappings(delete.getTables(), mappings);
            this.extractExpression(delete.getWhere(), mappings);
        }
    }

    public String getTableName(Table table) {
        TableMapping tableMapping = mappingTables.get(table);
        return tableMapping.getMappingName();
    }

    public String getColumnName(Column column) {
        ColumnMapping columnMapping = mappingColumns.get(column);
        return columnMapping.getSmartMappingName();
    }

    private void extractMappings(PlainSelect plainSelect, Map<String, Object> mappings) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            this.extractTableMappings(((Table) fromItem), mappings);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            for (Join join : joins) {
                FromItem rightItem = join.getRightItem();
                if (rightItem instanceof Table) {
                    this.extractTableMappings(((Table) rightItem), mappings);
                } else if (rightItem instanceof SubSelect) {
                    this.extractSubSelect(((SubSelect) rightItem), mappings);
                }
                Expression onExpression = join.getOnExpression();
                if (onExpression != null) {
                    this.extractExpression(onExpression, mappings);
                }
            }
        }

        Expression where = plainSelect.getWhere();
        if (where != null) {
            this.extractExpression(where, mappings);
        }

        List<Expression> groupByColumnReferences = plainSelect.getGroupByColumnReferences();
        if (groupByColumnReferences != null) {

            for (Expression groupByColumnReference : groupByColumnReferences) {

                if (groupByColumnReference instanceof Column) {
                    this.extractColumnMapping(((Column) groupByColumnReference), mappings);
                }

            }
        }

        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        if (orderByElements != null) {

            for (OrderByElement orderByElement : orderByElements) {
                Expression expression = orderByElement.getExpression();
                this.extractExpression(expression, mappings);
            }
        }

        List<SelectItem> selectItems = plainSelect.getSelectItems();
        for (SelectItem selectItem : selectItems) {

            if (selectItem instanceof SelectExpressionItem) {
                SelectExpressionItem expressionItem = (SelectExpressionItem) selectItem;
                Expression expression = expressionItem.getExpression();
                this.extractExpression(expression, mappings);
            }
        }
    }

    protected void extractColumnMapping(List<Column> columns, Map<String, Object> mappings) {
        if (columns == null) {
            return;
        }
        for (Column column : columns) {
            this.extractColumnMapping(column, mappings);
        }
    }

    protected void extractColumnMapping(Column column, Map<String, Object> mappings) {
        Table table = column.getTable();
        String name = (table == null || StringUtils.isBlank(table.getName())) ? DEFAULT_ALIAS : table.getName();
        String mappingName = column.getColumnName();
        Object obj = mappings.get(name);
        if (obj instanceof String) {
            Class<?> clazz = this.getMappingClass(((String) obj));
            mappingName = mappingHandler.getColumnName(clazz, column.getColumnName());
            mappings.put(column.getColumnName(), mappingName);
        } else if (obj instanceof Map) {
            Map<String, Object> subMap = (Map<String, Object>) obj;
            mappingName = (String) subMap.get(column.getColumnName());
        }
        ColumnMapping mappingColumn = new ColumnMapping();
        mappingColumn.setColumn(column);
        mappingColumn.setMappingName(mappingName);
        mappingColumns.put(column, mappingColumn);
    }

    protected void extractExpression(List<Expression> expressions, Map<String, Object> mappings) {
        if (expressions == null) {
            return;
        }
        for (Expression expression : expressions) {
            this.extractExpression(expression, mappings);
        }
    }

    protected void extractExpression(Expression expression, Map<String, Object> mappings) {

        if (expression == null) {
            return;
        }

        if (expression instanceof BinaryExpression) {

            BinaryExpression binaryExpression = (BinaryExpression) expression;
            Expression leftExpression = binaryExpression.getLeftExpression();
            this.extractExpression(leftExpression, mappings);

            Expression rightExpression = binaryExpression.getRightExpression();
            this.extractExpression(rightExpression, mappings);

        } else if (expression instanceof InExpression) {

            InExpression inExpression = (InExpression) expression;
            Expression leftExpression = inExpression.getLeftExpression();
            this.extractExpression(leftExpression, mappings);

            //暂未实现
//            ItemsList rightItemsList = inExpression.getRightItemsList();
        } else if (expression instanceof Function) {
            Function function = (Function) expression;
            ExpressionList parameters = function.getParameters();
            if (parameters != null) {
                this.extractExpression(parameters.getExpressions(), mappings);
            }

        } else if (expression instanceof Column) {
            this.extractColumnMapping(((Column) expression), mappings);
        }
    }

    private void extractMappings(SelectBody selectBody, Map<String, Object> mappings) {

        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            this.extractMappings(plainSelect, mappings);
        }
    }

    private String getTableAliasName(Table table) {
        Alias alias = table.getAlias();
        return alias == null || StringUtils.isBlank(alias.getName()) ? DEFAULT_ALIAS : alias.getName();
    }

    protected void extractTableMappings(List<Table> tables, Map<String, Object> mappings) {
        if (tables == null) {
            return;
        }
        for (Table table : tables) {
            this.extractTableMappings(table, mappings);
        }
    }

    private void extractTableMappings(Table table, Map<String, Object> mappings) {
        String tableAliasName = this.getTableAliasName(table);
        mappings.put(tableAliasName, table.getName());

        String mappingName = this.getTableName(table.getName());
        TableMapping tableMapping = new TableMapping();
        tableMapping.setTable(table);
        tableMapping.setMappingName(mappingName);

        mappingTables.put(table, tableMapping);
    }

    private void extractSubSelect(SubSelect subSelect, Map<String, Object> mappings) {

        Map<String, Object> subMappings = new HashMap<>();
        Alias alias = subSelect.getAlias();
        mappings.put(alias.getName(), subMappings);

        SelectBody selectBody = subSelect.getSelectBody();
        this.extractMappings(selectBody, subMappings);

    }

    private Class<?> getMappingClass(String modelName) {
        Class<?> clazz = null;
        if (customClassMapping != null) {
            clazz = customClassMapping.get(modelName);
        }
        if (clazz == null && classMapping != null) {
            clazz = classMapping.get(modelName);
        }
        if (clazz == null) {
            throw new SonsureJdbcException("对应的class不存在:" + modelName);
        }
        return clazz;
    }

    private String getTableName(String modelName) {
        Class<?> clazz = this.getMappingClass(modelName);
        String tableName = mappingHandler.getTableName(clazz, null);
        return tableName;
    }
}
