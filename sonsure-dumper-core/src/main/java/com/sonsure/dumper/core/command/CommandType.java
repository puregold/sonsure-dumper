/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

public enum CommandType {

    /**
     * Insert command type.
     */
    INSERT,

    /**
     * Query for list command type.
     */
    QUERY_FOR_LIST,

    /**
     * Query single result command type.
     */
    QUERY_SINGLE_RESULT,

    /**
     * Query for map command type.
     */
    QUERY_FOR_MAP,

    /**
     * Query for map list command type.
     */
    QUERY_FOR_MAP_LIST,

    /**
     * Query one col command type.
     */
    QUERY_ONE_COL,

    /**
     * Query one col list command type.
     */
    QUERY_ONE_COL_LIST,

    /**
     * Update command type.
     */
    UPDATE,

    /**
     * Batch update command type.
     */
    BATCH_UPDATE,

    /**
     * Delete command type.
     */
    DELETE,

    /**
     * Execute command type.
     */
    EXECUTE,

    /**
     * Execute script command type.
     */
    EXECUTE_SCRIPT;
}
