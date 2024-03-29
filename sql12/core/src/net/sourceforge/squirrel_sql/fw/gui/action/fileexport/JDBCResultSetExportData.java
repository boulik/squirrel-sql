package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

import java.sql.Connection;
import java.util.List;

public class JDBCResultSetExportData
{
   public List<String> _originalSqlsToExport;

   public Connection _con;
   public DialectType _dialect;
   public boolean _limitRows;

   List<ExportSqlNamed> _exportSqlsNamed;
   public boolean _exportSingleFile;
   public int _maxRows;
}
