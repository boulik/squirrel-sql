package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class SavedSessionListCellRenderer implements ListCellRenderer<SavedSessionJsonBean>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionListCellRenderer.class);

   @Override
   public Component getListCellRendererComponent(JList<? extends SavedSessionJsonBean> list, SavedSessionJsonBean value, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == value)
      {
         return null;
      }

      JTextArea comp = new JTextArea(SavedSessionUtil.getDisplayString(value));
      comp.setEditable(false);

      if(isSelected)
      {
         comp.setBackground(new JTextField().getSelectionColor());
      }

      if(cellHasFocus)
      {
         comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }

      return comp;

   }

}
