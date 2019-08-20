package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import java.util.Arrays;
import java.util.Optional;

public enum MarkDuplicatesMode
{
   DUPLICATE_VALUES_IN_COLUMNS(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DUPLICATE_VALUES_IN_COLUMNS),
                               StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateValuesInColumns.text"),
                               StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateValuesInColumns.tooltip")),

   DUPLICATE_CONSECUTIVE_VALUES_IN_COLUMNS(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DUPLICATE_VALUES_IN_COLUMNS_IF_CONSECUTIVE),
                               StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateValuesInColumnsIfConsecutive.text"),
                               StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateValuesInColumnsIfConsecutive.tooltip")),

   DUPLICATE_ROWS(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DUPLICATE_ROWS),
                  StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateRows.text"),
                  StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateRows.tooltip")),

   DUPLICATE_CONSECUTIVE_ROWS(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DUPLICATE_ROWS_IF_CONSECUTIVE),
                  StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateRowsIfConsecutive.text"),
                  StaticsAccess.s_stringMgr.getString("MarkDuplicatesMode.duplicateRowsIfConsecutive.tooltip"));


   private static class StaticsAccess
   {
      static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MarkDuplicatesMode.class);
   }

   private final ImageIcon _icon;
   private final String _text;
   private final String _toolTip;

   MarkDuplicatesMode(ImageIcon icon, String text, String toolTip)
   {

      _icon = icon;
      _text = text;
      _toolTip = toolTip;
   }

   public static MarkDuplicatesMode getModeByIcon(Icon icon)
   {
      Optional<MarkDuplicatesMode> ret = Arrays.stream(values()).filter(e -> e._icon == icon).findFirst();

      if(false == ret.isPresent())
      {
         throw new IllegalStateException("No mode for Icon " + icon);
      }

      return ret.get();
   }


   public Icon getIcon()
   {
      return _icon;
   }

   public String getText()
   {
      return _text;
   }

   public String getToolTipText()
   {
      return _toolTip;
   }

   public JToggleButton findButton(ButtonChooser buttonChooser)
   {
      Optional<AbstractButton> ret = buttonChooser.getAllButtons().stream().filter(b -> b.getIcon() == _icon).findFirst();

      if(false == ret.isPresent())
      {
         throw new IllegalStateException("No Button found for mode " + this.name());
      }

      return (JToggleButton) ret.get();
   }
}