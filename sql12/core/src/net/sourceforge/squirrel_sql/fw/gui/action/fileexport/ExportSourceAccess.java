package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JTable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExportSourceAccess
{
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportSourceAccess.class);
   static ILogger s_log = LoggerController.createLogger(ExportSourceAccess.class);

   private UITableExportData _uiTableExportData;
   private JDBCResultSetExportData _jdbcResultSetExportData;

   private boolean _exportMultipleResults;
   private MultipleSqlResultExportDestinationInfo _currentExportDestinationInfo;


   public ExportSourceAccess(JTable table)
   {
      _uiTableExportData = new UITableExportData();
      _uiTableExportData._tableExportDialogWasOpenedFor = table;
   }

   public ExportSourceAccess(List<String> sqls, Connection con, DialectType dialect)
   {
      _jdbcResultSetExportData = new JDBCResultSetExportData();
      _jdbcResultSetExportData._originalSqlsToExport = sqls;
      _jdbcResultSetExportData._con = con;
      _jdbcResultSetExportData._dialect = dialect;
   }

   public boolean isUITableMissingBlobData(String separatorChar)
   {
      if(isResultSetExport())
      {
         return false;
      }
      else
      {
         return ExportUtil.isUITableMissingBlobData(_uiTableExportData._tableExportDialogWasOpenedFor, separatorChar);
      }
   }

   public ExportDataInfoList createExportData(ProgressAbortCallback progressController) throws ExportDataException
   {
      if(isResultSetExport())
      {
         return createResultSetExportDataInfoList(progressController);
      }
      else
      {
         return createUiTableExportDataInfoList();
      }
   }

   private boolean isResultSetExport()
   {
      return null != _jdbcResultSetExportData;
   }

   private ExportDataInfoList createResultSetExportDataInfoList(ProgressAbortCallback progressController) throws ExportDataException
   {
      if(_jdbcResultSetExportData._exportSqlsNamed.isEmpty())
      {
         if(_exportMultipleResults)
         {
            // Happens when multiple SQL result export was chosen with empty export list.
            return ExportDataInfoList.EMPTY;
         }
         else
         {
            progress(progressController, s_stringMgr.getString("ResultSetExportCommand.executingQuery.n.of.m", 1, 1));
            // This is the default behavior, i.e. export of single table.
            return ExportDataInfoList.single(createResultSetExportData(_jdbcResultSetExportData._originalSqlsToExport.get(0)));
         }
      }
      else
      {
         List<ExportDataInfo> buf = new ArrayList<>();

         final List<ExportSqlNamed> exportSqlsNamed = _jdbcResultSetExportData._exportSqlsNamed;
         for (int i = 0; i < exportSqlsNamed.size(); i++)
         {
            progress(progressController, s_stringMgr.getString("ResultSetExportCommand.executingQuery.n.of.m", i + 1, exportSqlsNamed.size()));

            ExportSqlNamed exportSqlNamed = exportSqlsNamed.get(i);
            final ResultSetExportData resultSetExportData = createResultSetExportData(exportSqlNamed.getSql());
            buf.add(new ExportDataInfo(resultSetExportData, exportSqlNamed.getExportNameFileNormalized()));
         }

         return new ExportDataInfoList(buf, _currentExportDestinationInfo);
      }
   }

   private ResultSetExportData createResultSetExportData(String sql) throws ExportDataException
   {
      try
      {
         Statement stat = SQLUtilities.createStatementForStreamingResults(_jdbcResultSetExportData._con, _jdbcResultSetExportData._dialect);

         if (_jdbcResultSetExportData._limitRows)
         {
            stat.setMaxRows(_jdbcResultSetExportData._maxRows);
         }
         return new ResultSetExportData(stat, sql, _jdbcResultSetExportData._dialect);
      }
      catch (SQLException e)
      {
         s_log.error(s_stringMgr.getString("ResultSetExportCommand.errorExecuteStatement"), e);
         throw new ExportDataException(s_stringMgr.getString("ResultSetExportCommand.errorExecuteStatement"), e);
      }
   }

   private ExportDataInfoList createUiTableExportDataInfoList()
   {
      if(_uiTableExportData._sqlResultDataSetViewersExportDataList.isEmpty())
      {
         if(_exportMultipleResults)
         {
            // Happens when multiple SQL result export was chosen with empty export list.
            return ExportDataInfoList.EMPTY;
         }
         else
         {
            // This is the default behavior, i.e. export of single table.
            return ExportDataInfoList.single(new JTableExportData(_uiTableExportData._tableExportDialogWasOpenedFor, false == _uiTableExportData._exportUITableSelection));
         }
      }
      else
      {
         return new ExportDataInfoList(_uiTableExportData._sqlResultDataSetViewersExportDataList, _currentExportDestinationInfo);
      }
   }


   public void progress(ProgressAbortCallback progressController, String task)
   {
      if(progressController != null)
      {
         progressController.currentlyLoading(task);
      }
   }


   public TableExportPreferences getPreferences()
   {
      final TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

      if(isResultSetExport())
      {
         /////////////////////////////////////////////////////////////////////////////////////////////////
         // If useColoring was true for a file export a XSSFWorkbook instead of a SXSSFWorkbook was used.
         // This would result in much higher memory usage and much longer export time.
         // See DataExportExcelWriter.beforeWorking(...)
         prefs.setUseColoring(false);
         //
         /////////////////////////////////////////////////////////////////////////////////////////////////
      }

      return prefs;
   }

   public void prepareResultSetExport(List<ExportSqlNamed> exportSqlsNamed, boolean exportSingleFile, boolean limitRows, int maxRows, MultipleSqlResultExportDestinationInfo currentExportDestinationInfo, boolean exportMultipleResults)
   {
      _jdbcResultSetExportData._exportSingleFile = exportSingleFile;
      _jdbcResultSetExportData._limitRows = limitRows;
      _jdbcResultSetExportData._maxRows = maxRows;
      _jdbcResultSetExportData._exportSqlsNamed = exportSqlsNamed;

      _currentExportDestinationInfo = currentExportDestinationInfo;
      _exportMultipleResults = exportMultipleResults;
   }

   public void prepareSqlResultDataSetViewersExport(List<ExportDataInfo> sqlResultDataSetViewersExportDataList, boolean exportUITableSelection, MultipleSqlResultExportDestinationInfo currentExportDestinationInfo, boolean exportMultipleResults)
   {
      _uiTableExportData._sqlResultDataSetViewersExportDataList = sqlResultDataSetViewersExportDataList;
      _uiTableExportData._exportUITableSelection = exportUITableSelection;

      _currentExportDestinationInfo = currentExportDestinationInfo;
      _exportMultipleResults = exportMultipleResults;
   }

   public List<String> getOriginalSqlsToExport()
   {
      if(false == isResultSetExport())
      {
         throw new IllegalStateException("Not exporting SQL statement!!!");
      }

      return _jdbcResultSetExportData._originalSqlsToExport;
   }
}
