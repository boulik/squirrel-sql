package net.sourceforge.squirrel_sql.plugins.refactoring;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBUtil
{

	public static String[] getAlterSQLForColumnChange(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		// It is important to process the name change first - so that we can use
		// the new name instead of the old in subsequent alterations
		String nameSQL = getColumnNameAlterSQL(from, to, dialect, qualifier, prefs);
		if (nameSQL != null)
		{
			result.add(nameSQL);
		}
		String[] nullSQL = getNullAlterSQL(from, to, dialect, qualifier, prefs);
		if (nullSQL != null)
		{
			result.addAll(Arrays.asList(nullSQL));
		}
		String commentSQL = getCommentAlterSQL(from, to, dialect, qualifier, prefs);
		if (commentSQL != null)
		{
			result.add(commentSQL);
		}
		List<String> typeSQL = getTypeAlterSQL(from, to, dialect, qualifier, prefs);
		if (typeSQL != null)
		{
			result.addAll(typeSQL);
		}
		String defaultSQL = getAlterSQLForColumnDefault(from, to, dialect, qualifier, prefs);
		if (defaultSQL != null)
		{
			result.add(defaultSQL);
		}
		return result.toArray(new String[result.size()]);
	}

	public static List<String> getTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		if (from.getDataType() == to.getDataType() && from.getColumnSize() == to.getColumnSize())
		{
			return null;
		}
		return dialect.getColumnTypeAlterSQL(from, to, qualifier, prefs);
	}

	public static String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		if (from.getColumnName().equals(to.getColumnName()))
		{
			return null;
		}
		return dialect.getColumnNameAlterSQL(from, to, qualifier, prefs);
	}

	public static String[] getNullAlterSQL(TableColumnInfo from, TableColumnInfo to, HibernateDialect dialect,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		if (from.isNullable().equalsIgnoreCase(to.isNullable()))
		{
			return null;
		}
		return dialect.getColumnNullableAlterSQL(to, qualifier, prefs);
	}

	public static String getCommentAlterSQL(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String oldComment = from.getRemarks();
		String newComment = to.getRemarks();
		if (!dialect.supportsColumnComment())
		{
			return null;
		}
		if (oldComment == null && newComment == null)
		{
			return null;
		}
		if (StringUtils.isEmpty(oldComment) && StringUtils.isEmpty(newComment)) {
			return null;
		}
		if (oldComment == null || !oldComment.equals(newComment))
		{
			return dialect.getColumnCommentAlterSQL(to, qualifier, prefs);
		}
		return null;
	}

	public static String getAlterSQLForColumnDefault(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String oldDefault = from.getDefaultValue();
		String newDefault = to.getDefaultValue();
		// empty string ('') seems to be represented as null in some drivers.
		// Not sure if this is the best thing to do here, but it fixes an issue
		// where SQL returns is set default to '', when it is already null.
		if (oldDefault == null)
		{
			oldDefault = "";
		}
		if (newDefault == null)
		{
			newDefault = "";
		}
		if (!oldDefault.equals(newDefault))
		{
			if (!dialect.supportsAlterColumnDefault())
			{
				throw new UnsupportedOperationException(dialect.getDisplayName()
					+ " doesn't support column default value alterations");
			}
			return dialect.getColumnDefaultAlterSQL(to, qualifier, prefs);
		}
		return null;
	}
}
