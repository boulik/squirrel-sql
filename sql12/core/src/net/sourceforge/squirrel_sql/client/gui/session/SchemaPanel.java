package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.JScrollPopupMenuPosition;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class SchemaPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SchemaPanel.class);

   private static final ILogger s_log = LoggerController.createLogger(SchemaPanel.class);

   private SmallTabButton _btnRefresh;
   private SmallTabButton _btnChooseSchema;

   private ISession _session;

   private JTextField _txtSchema = new JTextField();

   public SchemaPanel(ISession session)
   {
      _session = session;

      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      JLabel label = new JLabel(s_stringMgr.getString("SchemaPanel.refresh.current.schema.label"));
      add(label, gbc);

      JPanel txtSizer = new JPanel(new BorderLayout())
      {
         @Override public Dimension getPreferredSize()
         {
            Dimension size = super.getPreferredSize();
            size.width = _txtSchema.getFont().getSize() * 10;
            return size;
         }
      };
      txtSizer.add(_txtSchema);

      gbc = new GridBagConstraints(1, 0, 1, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,3,0,0), 0,0);
      add(txtSizer, gbc);
      _txtSchema.setEditable(false);
      //_txtSchema.setBorder(BorderFactory.createEmptyBorder());
      _txtSchema.setMargin(new Insets(0, 0, 0, 0));


      gbc = new GridBagConstraints(2, 0, 1, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      _btnRefresh = new SmallTabButton(s_stringMgr.getString("SchemaPanel.refresh.current.schema"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_REFRESH), 4);
      add(_btnRefresh, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0);
      _btnChooseSchema = new SmallTabButton(s_stringMgr.getString("SchemaPanel.refresh.choose.schema"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_CHOOSE_SCHEMA), 4);
      add(_btnChooseSchema, gbc);


      _btnRefresh.addActionListener(e -> onRefreshSchema(true));

      _btnChooseSchema.addActionListener(e -> onChooseSchema());

      _txtSchema.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onTxtSchemaClicked(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            onTxtSchemaClicked(e);
         }

      });


      onRefreshSchema(false);


   }

   private void onTxtSchemaClicked(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupTxtSchemas = new JPopupMenu();

      JMenuItem menuItem = new JMenuItem(s_stringMgr.getString("SchemaPanel.txtschema.copy"));

      menuItem.addActionListener(e -> onCopyTxtSchema() );

      popupTxtSchemas.add(menuItem);

      popupTxtSchemas.show(_txtSchema, me.getX(), me.getY());
   }

   private void onCopyTxtSchema()
   {
      StringSelection ss = new StringSelection(_txtSchema.getText());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
   }

   private void onChooseSchema()
   {
      try
      {
         JScrollPopupMenu popupSchemas = new JScrollPopupMenu();

         int menuCount = 0;
         popupSchemas.setMaximumVisibleRows(20); // Call before adding items

         for (String schema : Main.getApplication().getSessionManager().getAllSchemas(_session))
         {
            if(StringUtilities.isEmpty(schema, true))
            {
               continue;
            }

            JMenuItem menuItem = new JMenuItem(schema);
            menuItem.addActionListener(e -> onSchemaSelected(menuItem));

            popupSchemas.add(menuItem);
            ++menuCount;
         }

         popupSchemas.positionPopRelativeTo(_btnChooseSchema, menuCount, JScrollPopupMenuPosition.NORTH_WEST);

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onSchemaSelected(JMenuItem menuItem)
   {
      try
      {
         _session.getSQLConnection().setSchema(menuItem.getText());

         if( menuItem.getText().equalsIgnoreCase(_session.getSQLConnection().getSchema()))
         {
            onRefreshSchema(true);
         }
         else
         {
            s_log.error("Setting schema didn't take effect.");
         }
      }
      catch (Throwable e)
      {
         s_log.error("Failed to set schema", e);
      }
   }

   /**
    * @param logException Currently getSchema() is not well supported by several RDBMSes/Drivers.
    *                     Using this parameter we prevent logs at start up. But whenever the refresh button is used logging will be done.
    */
   private void onRefreshSchema(boolean logException)
   {

      String noneString = "<None>";

      String schema;

      try
      {
         schema = _session.getSQLConnection().getSchema();
         schema = StringUtilities.isEmpty(schema, true) ? noneString : schema;
      }
      catch (Throwable e)
      {

         schema = "<Not accessible>";
         if (logException)
         {
            s_log.error("Failed to load current schema name", e);
         }
      }

      _txtSchema.setText(schema);

      invalidate();

   }

   @Override
   public void setBackground(Color bg)
   {
      super.setBackground(bg);

      if(null != _txtSchema)
      {
         _txtSchema.setBackground(bg);
      }
   }
}
