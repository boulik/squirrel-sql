package net.sourceforge.squirrel_sql.plugins.h2.tab;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.client.session.schemainfo.basetabletype.BaseTableTypeHandler;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * This class will display the details for an H2 index.
 *
 */
public class IndexDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexDetailsTab.class);


	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		// i18n[IndexDetailsTab.title=Details]
		String TITLE = s_stringMgr.getString("IndexDetailsTab.title");
		// i18n[IndexDetailsTab.hint=Display index details]
		String HINT = s_stringMgr.getString("IndexDetailsTab.hint");
	}

	public IndexDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		String sql;

		if(BaseTableTypeHandler.isH2VersionGreaterOrEqual2(getSession().getMetaData()))
		{
			sql = "SELECT " +
					"INFORMATION_SCHEMA.INDEXES.TABLE_CATALOG, " +
					"INFORMATION_SCHEMA.INDEXES.TABLE_SCHEMA, " +
					"INFORMATION_SCHEMA.INDEXES.TABLE_NAME, " +
					"INFORMATION_SCHEMA.INDEX_COLUMNS.IS_UNIQUE, " +
					"INFORMATION_SCHEMA.INDEX_COLUMNS.ORDINAL_POSITION, " +
					"INFORMATION_SCHEMA.INDEX_COLUMNS.COLUMN_NAME, " +
					"INFORMATION_SCHEMA.INDEXES.INDEX_TYPE_NAME, " +
					"INFORMATION_SCHEMA.INDEXES.IS_GENERATED, " +
					"INFORMATION_SCHEMA.INDEXES.INDEX_TYPE_NAME, " +
					"INFORMATION_SCHEMA.INDEXES.REMARKS " +
					"FROM INFORMATION_SCHEMA.INDEXES " +
					"INNER JOIN INFORMATION_SCHEMA.INDEX_COLUMNS ON " +
					"      INFORMATION_SCHEMA.INDEX_COLUMNS.INDEX_CATALOG = INFORMATION_SCHEMA.INDEXES.INDEX_CATALOG " +
					"  AND INFORMATION_SCHEMA.INDEX_COLUMNS.INDEX_SCHEMA = INFORMATION_SCHEMA.INDEXES.INDEX_SCHEMA " +
					"  AND INFORMATION_SCHEMA.INDEX_COLUMNS.INDEX_NAME = INFORMATION_SCHEMA.INDEXES.INDEX_NAME " +
					"WHERE INFORMATION_SCHEMA.INDEXES.TABLE_SCHEMA = ? " +
					"AND INFORMATION_SCHEMA.INDEXES.INDEX_NAME = ? ";
		}
		else
		{
			sql = "SELECT TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME, " +
					"NON_UNIQUE,ORDINAL_POSITION,COLUMN_NAME, " +
					"CARDINALITY,PRIMARY_KEY,INDEX_TYPE_NAME, " +
					"IS_GENERATED,INDEX_TYPE,ASC_OR_DESC,PAGES, " +
					"FILTER_CONDITION,REMARKS " +
					"FROM INFORMATION_SCHEMA.INDEXES " +
					"WHERE TABLE_SCHEMA = ? " +
					"AND INDEX_NAME = ? ";;
		}

		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
