package net.sourceforge.squirrel_sql.fw.gui.textfind;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum TextFindMode
{
   CONTAINS(I18nProvider.s_stringMgr.getString("TextFindMode.contains")),
   CONTAINS_IGNORE_CASE(I18nProvider.s_stringMgr.getString("TextFindMode.contains.ignore.case")),
   CONTAINS_REG_EXP(I18nProvider.s_stringMgr.getString("TextFindMode.contains.regexp"));

   private final String _displayName;

   TextFindMode(String displayName)
   {
      _displayName = displayName;
   }

   private interface I18nProvider
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(TextFindMode.class);
   }


   public String getDisplayName()
   {
      return _displayName;
   }
}
