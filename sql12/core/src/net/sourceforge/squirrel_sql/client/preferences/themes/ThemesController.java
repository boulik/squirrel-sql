package net.sourceforge.squirrel_sql.client.preferences.themes;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.messagepanel.MessagePrefsCtrl;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;
import java.awt.Color;

public class ThemesController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ThemesController.class);


   private final ThemesPanel _themesPanel;

   /**
    * Is saved in Props instead of Global preferences because applying a theme persistently changes
    * LAF-Preferences and New Session Properties. Hence the applied theme is saved immediately, too.
    *
    */
   private MessagePrefsCtrl _messagePrefsCtrl;

   public ThemesController(MessagePrefsCtrl messagePrefsCtrl)
   {
      _messagePrefsCtrl = messagePrefsCtrl;
      _themesPanel = new ThemesPanel();

      _themesPanel.cboThemes.setSelectedItem(ThemesEnum.getCurrentTheme());

      _themesPanel.btnApply.addActionListener(e -> onApply());
   }

   private void onApply()
   {
      ThemesEnum.saveCurrentTheme((ThemesEnum) _themesPanel.cboThemes.getSelectedItem());


      switch (ThemesEnum.getCurrentTheme())
      {
         case LIGH:
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_themesPanel, s_stringMgr.getString("ThemesController.apply.light.theme")))
            {
               return;
            }

            LAFPluginAccessor.applyMetalOcean();
            SyntaxPluginAccessor.applyDefaultTheme();
            Main.getApplication().getSquirrelPreferences().getSessionProperties().setNullValueColorRGB(SessionProperties.DEFAULT_NULL_VALUE_COLOR_RGB);

            _messagePrefsCtrl.switchToLight();
            break;
         case DARK:
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_themesPanel, s_stringMgr.getString("ThemesController.apply.dark.theme")))
            {
               return;
            }

            LAFPluginAccessor.applyFlatLafDark();
            SyntaxPluginAccessor.applyDarkTheme();
            Main.getApplication().getSquirrelPreferences().getSessionProperties().setNullValueColorRGB(new Color(90,100,90).getRGB());

            _messagePrefsCtrl.switchToDark();
            break;
      }
   }

   public ThemesPanel getPanel()
   {
      return _themesPanel;
   }

}
