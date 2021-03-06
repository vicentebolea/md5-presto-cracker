/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.cracker;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.ColumnMetadata;
import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.ConnectorTableHandle;
import com.facebook.presto.spi.ConnectorTableLayout;
import com.facebook.presto.spi.ConnectorTableLayoutHandle;
import com.facebook.presto.spi.ConnectorTableLayoutResult;
import com.facebook.presto.spi.ConnectorTableMetadata;
import com.facebook.presto.spi.Constraint;
import com.facebook.presto.spi.SchemaTableName;
import com.facebook.presto.spi.SchemaTablePrefix;
import com.facebook.presto.spi.connector.ConnectorMetadata;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CrackerMetadata implements ConnectorMetadata
{
    private final String connectorId;

    public CrackerMetadata(String connectorId)
    {
        this.connectorId = connectorId;
    }

    // Return schema name
    @Override
    public List<String> listSchemaNames(ConnectorSession session)
    {
        return ImmutableList.of(CrackerConsts.SCHEMA_NAME);
    }

    // SchemaTableName have schema name, table name
    @Override
    public ConnectorTableHandle getTableHandle(ConnectorSession session, SchemaTableName schemaTableName)
    {
        if (!schemaTableName.getTableName().equals(CrackerConsts.TABLE_NAME)) {
            return null;
        }
        return new CrackerTableHandle(connectorId, schemaTableName.getTableName());
    }

    @Override
    public List<ConnectorTableLayoutResult> getTableLayouts(ConnectorSession session, ConnectorTableHandle tableHandle, Constraint<ColumnHandle> constraint, Optional<Set<ColumnHandle>> desiredColumns)
    {
        CrackerTableHandle crackerTableHandle = Types.checkType(tableHandle, CrackerTableHandle.class, "tableHandle");
        ConnectorTableLayout layout = new ConnectorTableLayout(new CrackerTableLayoutHandle(crackerTableHandle));
        return ImmutableList.of(new ConnectorTableLayoutResult(layout, constraint.getSummary()));
    }

    @Override
    public ConnectorTableLayout getTableLayout(ConnectorSession session, ConnectorTableLayoutHandle handle)
    {
        return new ConnectorTableLayout(handle);
    }

    @Override
    public ConnectorTableMetadata getTableMetadata(ConnectorSession session, ConnectorTableHandle tableHandle)
    {
        CrackerTableHandle crackerTableHandle = Types.checkType(tableHandle, CrackerTableHandle.class, "tableHandle");
        SchemaTableName schemaTableName = new SchemaTableName(CrackerConsts.SCHEMA_NAME, crackerTableHandle.getTableName());

        return new ConnectorTableMetadata(schemaTableName, ImmutableList.of(new ColumnMetadata(CrackerConsts.COLUMN_NAME, CrackerConsts.COLUMN_TYPE)));
    }

    @Override
    public List<SchemaTableName> listTables(ConnectorSession session, String schemaNameOrNull)
    {
        if (!schemaNameOrNull.equals(CrackerConsts.SCHEMA_NAME)) {
            return null;
        }

        return ImmutableList.of(new SchemaTableName(schemaNameOrNull, CrackerConsts.TABLE_NAME));
    }

    @Override
    public Map<String, ColumnHandle> getColumnHandles(ConnectorSession session, ConnectorTableHandle tableHandle)
    {
        return ImmutableMap.of(CrackerConsts.COLUMN_NAME, new CrackerColumnHandle(CrackerConsts.COLUMN_NAME, CrackerConsts.COLUMN_TYPE));
    }

    @Override
    public ColumnMetadata getColumnMetadata(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle columnHandle)
    {
        CrackerColumnHandle crackerColumnHandle = Types.checkType(columnHandle, CrackerColumnHandle.class, "columnHandle");
        return new ColumnMetadata(crackerColumnHandle.getColumnName(), crackerColumnHandle.getType());
    }

    @Override
    public Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
    {
        return null;
    }
}
