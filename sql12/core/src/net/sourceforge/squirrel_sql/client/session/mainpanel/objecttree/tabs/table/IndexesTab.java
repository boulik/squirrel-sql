package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.session.schemainfo.basetabletype.BaseTableTypeHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This tab shows the columns in the currently selected table.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IndexesTab extends BaseTableTab
{
	
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(IndexesTab.class);
	
    private static final int[] indexIndices = new int[] {5, 6, 8, 9, 10, 4, 7, 11, 12, 13 };
    
	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return s_stringMgr.getString("IndexesTab.title");
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return s_stringMgr.getString("IndexesTab.hint");
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
		final SQLDatabaseMetaData dmd = conn.getSQLMetaData();

		ITableInfo ti = getTableInfo();
		if (     false == "TABLE".equalsIgnoreCase(ti.getType())
				&& false == "SYSTEM TABLE".equalsIgnoreCase(ti.getType())
				&& false == "GLOBAL TEMPORARY".equalsIgnoreCase(ti.getType())
				&& false == "LOCAL TEMPORARY".equalsIgnoreCase(ti.getType())
		)
		{
			// Except from the exceptions below we quit here on account of the following issues:
			// Bug fixes:
			// - #1460 Scripts Plugin: Create table scripts contained redundant index for primary key.
			// - Object tree: NullPointerException occurred when a table's index tab was selected and the table's type other than "TABLE".
			if (false == BaseTableTypeHandler.isDatabaseUsingTypeBaseTableInsteadOpTable(dmd))
			{
				// See also SchemaInfoCache.initTypes(...)
				return null;
			}
		}
		ResultSetDataSet rsds = dmd.getIndexInfo(getTableInfo(), indexIndices, true);
		rsds.next(null);
		String indexName = (String) rsds.get(1);
		if (indexName == null)
		{
			rsds.removeRow(0);
		}
		rsds.resetCursor();
		return rsds;
	}
	
}
