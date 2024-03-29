package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

public enum ExportDialogType
{
   UI_TABLE_EXPORT(false, true),
   RESULT_SET_EXPORT(true, false);

   private final boolean _warnIfExcel;
   private final boolean _enableColoring;

   ExportDialogType(boolean warnIfExcel, boolean enableColoring)
   {
      _warnIfExcel = warnIfExcel;
      _enableColoring = enableColoring;
   }

   public boolean isWarnIfExcel()
   {
      return _warnIfExcel;
   }

   public boolean isEnableColoring()
   {
      return _enableColoring;
   }
}
