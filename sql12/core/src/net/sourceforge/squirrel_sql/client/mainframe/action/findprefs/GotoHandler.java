package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.List;

public class GotoHandler
{
   public final static ILogger s_log = LoggerController.createLogger(GotoHandler.class);
   private PrefsFindInfo _prefsFindInfo;

   private Timer _timer;
   private int _blinkCount;

   public GotoHandler()
   {
   }

   public boolean gotoPath(List<String> path)
   {
      _prefsFindInfo = ComponentInfoByPathUtil.createPrefsFindInfo();

      PrefComponentInfo tabComponentInfo = _prefsFindInfo.showTabOfPathComponent(path);

      _prefsFindInfo = _prefsFindInfo.getPrefsFindInfoUpdate();

      if(null == tabComponentInfo)
      {
         // Happens when the dialog node was gone to.
         return true;
      }

      PrefComponentInfo componentInfoToGoTo = _prefsFindInfo.getComponentInfoByPath(path);

      if(null == componentInfoToGoTo)
      {
         s_log.warn("Failed to find Component for path:\n" + path);
         final String pathNoNewLines = StringUtils.replace("" + path, "\n", " ");
         Main.getApplication().getMessageHandler().showWarningMessage("Failed to find Component for path:\n " + StringUtils.abbreviate(pathNoNewLines, 300));
         return false;
      }

      return blinkComponent(tabComponentInfo.getComponent(), componentInfoToGoTo.getComponent());
   }

   private boolean blinkComponent(Component containingTabComponent, Component componentToBlink)
   {
      if(containingTabComponent instanceof JScrollPane && containingTabComponent != componentToBlink)
      {
         JComponent componentInScrollPane = (JComponent) ((JScrollPane) containingTabComponent).getViewport().getView();

         int x = componentToBlink.getX();
         int y = componentToBlink.getY();

         Container parent = componentToBlink.getParent();
         while (parent != componentInScrollPane)
         {
            x += parent.getX();
            y += parent.getY();
            parent = parent.getParent();
         }

         final Rectangle rect = new Rectangle(x, y, componentToBlink.getWidth(), componentToBlink.getHeight());

         GUIUtils.forceProperty(() -> {
            componentInScrollPane.scrollRectToVisible(rect);
            final boolean contains = ((JScrollPane) containingTabComponent).getVisibleRect().contains(rect);
            return contains;
         });
      }

      _timer = new Timer(500, e -> onBlinkComponent(componentToBlink));

      _timer.setRepeats(true);

      SwingUtilities.invokeLater(() -> onBlinkComponent(componentToBlink));

      _timer.start();

      return true;
   }

   public PrefsFindInfo getPrefsFindInfoUpdate()
   {
      if(null == _prefsFindInfo)
      {
         throw new IllegalStateException("Call gotoPath() first");
      }

      return _prefsFindInfo;
   }

   private void onBlinkComponent(Component component)
   {
      Graphics graphics = component.getGraphics();

      if(_blinkCount++ % 2 == 0)
      {
         Color formerColor = graphics.getColor();
         graphics.setColor(Color.red);

         Stroke formerStroke = null;
         int strokeWidth = 4;
         if (graphics instanceof Graphics2D )
         {
            formerStroke = ((Graphics2D)graphics).getStroke();
            ((Graphics2D)graphics).setStroke(new BasicStroke(strokeWidth));
         }

         graphics.drawRect(strokeWidth, strokeWidth, component.getBounds().width - 2 * strokeWidth, component.getBounds().height - 2 * strokeWidth);

         graphics.setColor(formerColor);

         if (graphics instanceof Graphics2D)
         {
            ((Graphics2D)graphics).setStroke(formerStroke);
         }
      }
      else
      {
         component.repaint();
      }


      if(_blinkCount > 10)
      {
         _timer.stop();

         component.repaint();
      }
   }
}
