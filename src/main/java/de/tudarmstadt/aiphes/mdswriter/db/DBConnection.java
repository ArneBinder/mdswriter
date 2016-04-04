/*******************************************************************************
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.aiphes.mdswriter.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.tudarmstadt.aiphes.mdswriter.db.DBTable.FieldDefinition;
import de.tudarmstadt.aiphes.mdswriter.db.DBTable.FieldIndex;

public abstract class DBConnection {

	protected Connection connection;

	protected DBConnection(final Connection connection) {
		this.connection = connection;
	}

	public void close() throws SQLException {
		connection.close();
	}

	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}

	public PreparedStatement prepareStatement(final String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(final String sql,
			final int autoGeneratedKeys) throws SQLException {
		return connection.prepareStatement(sql, autoGeneratedKeys);
	}

	public int getGeneratedID(final Statement stmt) throws SQLException {
		ResultSet resSet = stmt.getGeneratedKeys();
		try {
			if (resSet.next())
				return resSet.getInt(1);

			return -1;
		} finally {
			resSet.close();
		}
	}

	public boolean executeStatement(final String sql) throws SQLException {
		Statement stmt = connection.createStatement();
		try {
			return stmt.execute(sql);
		} finally {
			stmt.close();
		}
	}

	public void createTable(final DBTable table) throws SQLException {
		dropTable(table);

		String sql = makeCreateTableSQL(table);
		executeStatement(sql);

		if (table.getIndices() != null)
			for (FieldIndex index : table.getIndices())
				createIndex(table, index);
	}

	protected abstract String makeCreateTableSQL(final DBTable table);

	public void dropTable(final DBTable table) throws SQLException {
		if (tableExists(table))
			executeStatement("DROP TABLE " + table.getName());
	}

	public abstract boolean tableExists(final DBTable table)
			throws SQLException;

	public void addField(final DBTable table, final FieldDefinition field)
			throws SQLException {
		executeStatement("ALTER TABLE " + table.getName()
				+ " ADD COLUMN " + fieldAsSQL(field));
	}

	public void dropField(final DBTable table, final FieldDefinition field)
			throws SQLException {
		executeStatement("ALTER TABLE " + table.getName()
				+ " DROP COLUMN " + field.getFieldName());
	}

	public abstract boolean fieldExists(final DBTable table,
			final FieldDefinition field) throws SQLException;

	protected abstract String fieldAsSQL(final FieldDefinition field);

	public void createIndex(final DBTable table, final FieldIndex index)
			throws SQLException {
		int[] fieldLengths = index.getFieldLengths();
		StringBuilder sql = new StringBuilder();
		int idx = 0;
		for (String field : index.getFields()) {
			if (sql.length() > 0)
				sql.append(", ");
			sql.append(field);
			if (fieldLengths != null && fieldLengths[idx] > 0)
				sql.append("(").append(fieldLengths[idx]).append(")");
			idx++;
		}

		executeStatement("CREATE INDEX " + index.makeIndexName(
				table.getName().replace('.', '_'))
				+ " ON " + table.getName()
				+ " (" + sql.toString() + ")");
	}

	public void dropIndex(final DBTable table, final FieldIndex index)
			throws SQLException {
		executeStatement("DROP INDEX " + index.makeIndexName(
				table.getName()) + " ON " + table.getName());
	}

	public abstract boolean indexExists(final DBTable table,
			final FieldIndex index) throws SQLException;

	public String sanitizeSQLParam(final String sql) {
		StringBuilder result = new StringBuilder();
		for (char c : sql.toCharArray())
			if (c == '%' || c == '_' || c == '?')
				result.append("\\").append(c);
			else
				result.append(c);
		return result.toString();
	}

}
