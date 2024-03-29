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

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.client.session.schemainfo.basetabletype.BaseTableTypeHandler;

/**
 * This class provides the necessary information to the parent tab to display the source for an H2 index.
 */
public class IndexSourceTab extends FormattedSourceTab
{
	/**
	 * Constructor
	 * 
	 * @param hint
	 *        what the user sees on mouse-over tool-tip
	 * @param stmtSep        
	 *        the character that separates SQL statements
	 */
	public IndexSourceTab(String hint, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);        
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PSFormattedSourceTab#getSqlStatement()
	 */
	@Override
   protected String getSqlStatement()
   {
		if(BaseTableTypeHandler.isH2VersionGreaterOrEqual2(getSession().getMetaData()))
		{
			return "SELECT " +
					 "'CREATE ' || INFORMATION_SCHEMA.INDEXES.INDEX_TYPE_NAME || ' ' || INFORMATION_SCHEMA.INDEXES.INDEX_NAME " +
					 "|| ' ON ' || INFORMATION_SCHEMA.INDEXES.table_name || '(' || INFORMATION_SCHEMA.INDEX_COLUMNS.column_name || ')' " +
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
			return
					"select " +
					"'CREATE '||index_type_name||' '||index_name||' ON '||table_name||'('||column_name||')' " +
					"from INFORMATION_SCHEMA.INDEXES " +
					"where table_schema = ? " +
					"and index_name = ? ";
		}
	}
}
