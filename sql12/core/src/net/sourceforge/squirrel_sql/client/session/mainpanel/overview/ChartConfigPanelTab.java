package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ChartConfigPanelTab extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChartConfigController.class);

   JComboBox cboXColumns;
   JComboBox cboYColumns;
   JButton btnChart;
   JComboBox cboCallDepth;
   JComboBox cboYAxisKind;

   JComboBox cboTimeScale;

   private ChartConfigPanelTabMode _chartConfigPanelTabMode;


   public ChartConfigPanelTab(ChartConfigPanelTabMode chartConfigPanelTabMode)
   {
      super(new GridBagLayout());
      _chartConfigPanelTabMode = chartConfigPanelTabMode;

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(15,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("OverviewPanel.XAxis")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(15,10,5,5),0,0);
      cboXColumns = new JComboBox();
      add(cboXColumns, gbc);


      if (chartConfigPanelTabMode == ChartConfigPanelTabMode.SINGLE_COLUMN)
      {
         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.YAxis")), gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,5),0,0);
         cboYAxisKind = new JComboBox();
         add(cboYAxisKind, gbc);
      }
      else if(chartConfigPanelTabMode == ChartConfigPanelTabMode.TWO_COLUMN)
      {
         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.YAxis")), gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,5),0,0);
         cboYColumns = new JComboBox();
         add(cboYColumns, gbc);

         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5),0,0);
         add(createAsKindPanel(), gbc);
      }
      else if(chartConfigPanelTabMode == ChartConfigPanelTabMode.XY_CHART)
      {
         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.YAxis")), gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,5),0,0);
         cboYColumns = new JComboBox();
         add(cboYColumns, gbc);
      }
      else if(chartConfigPanelTabMode == ChartConfigPanelTabMode.SCATTER_CHART)
      {
         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.YAxis")), gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,5),0,0);
         cboYColumns = new JComboBox();
         add(cboYColumns, gbc);
      }
      else if(chartConfigPanelTabMode == ChartConfigPanelTabMode.DIFFERENCES_CHART)
      {
         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.YAxis")), gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,5),0,0);
         cboYColumns = new JComboBox();
         add(cboYColumns, gbc);

         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.time.scale")), gbc);

         gbc = new GridBagConstraints(3,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5),0,0);
         cboTimeScale = new JComboBox();
         add(cboTimeScale, gbc);


         gbc = new GridBagConstraints(0,2, GridBagConstraints.REMAINDER,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10,10,0,5),0,0);
         MultipleLineLabel lblDesc= new MultipleLineLabel(s_stringMgr.getString("OverviewPanel.differences.description"));
         lblDesc.setFont(lblDesc.getFont().deriveFont(Font.BOLD));
         add(lblDesc, gbc);
      }


      if(   chartConfigPanelTabMode != ChartConfigPanelTabMode.XY_CHART
         && chartConfigPanelTabMode != ChartConfigPanelTabMode.SCATTER_CHART
         && chartConfigPanelTabMode != ChartConfigPanelTabMode.DIFFERENCES_CHART)
      {
         gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,10,5,5),0,0);
         add(new JLabel(s_stringMgr.getString("OverviewPanel.maxBarCount")), gbc);

         gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0,10,5,5),0,0);
         cboCallDepth = new JComboBox();
         add(cboCallDepth, gbc);
      }




      gbc = new GridBagConstraints(0,3,2,1,0,0,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(15,10,5,0),0,0);
      btnChart = new JButton(s_stringMgr.getString("OverviewPanel.showChartEndButton"));
      add(btnChart, gbc);



      int gbcCol = chartConfigPanelTabMode == ChartConfigPanelTabMode.SINGLE_COLUMN ? 4:5;
      gbc = new GridBagConstraints(0,gbcCol,3,1,0,1,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
      add(new JPanel(), gbc);


      gbc = new GridBagConstraints(3,0,1,4,1,0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
      add(new JPanel(), gbc);
   }

   private JPanel createAsKindPanel()
   {
      JPanel pnl = new JPanel(new BorderLayout(5,0));
      pnl.add(new JLabel(s_stringMgr.getString("OverviewPanel.as")), BorderLayout.WEST);

      cboYAxisKind = new JComboBox();
      pnl.add(cboYAxisKind, BorderLayout.CENTER);
      return pnl;
   }

   public String getTabTitle()
   {
      return s_stringMgr.getString(_chartConfigPanelTabMode.getTabTitleKey());
   }
}
