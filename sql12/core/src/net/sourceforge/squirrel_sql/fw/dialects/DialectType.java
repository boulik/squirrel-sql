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
package net.sourceforge.squirrel_sql.fw.dialects;

/**
 * An enumeration class that provides an entry for each database that we have a
 * HibernateDialect (SQuirreL's version) implemented.
 *
 * @author manningr
 */
public enum DialectType
{
   AXION,
   CACHE,
   DAFFODIL,
   DB2,
   DERBY,
   FIREBIRD,
   FRONTBASE,
   HADB,
   HSQLDB,
   H2,
   INFORMIX,
   INGRES,
   INTERBASE,
   INTERSYSTEMS_IRIS,
   MAXDB,
   MCKOI,
   MSSQL,
   MYSQL,     // MySQL 4.x and below
   MYSQL5,    // MySQL 5.x and above
   MARIADB,
   NETEZZA,
   ORACLE,
   POINTBASE,
   POSTGRES,
   PROGRESS,
   SQLLITE,
   SYBASEASE, // Sybase Adaptive Server Enterprise
   TEIID,
   TIMESTEN,
   UNITY_JDBC,
   GENERIC
}
