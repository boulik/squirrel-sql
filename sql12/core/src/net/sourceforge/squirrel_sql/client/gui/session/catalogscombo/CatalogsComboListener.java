package net.sourceforge.squirrel_sql.client.gui.session.catalogscombo;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeFinderGoToNextResultHandle;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder.ObjectTreeSearchResultFuture;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class CatalogsComboListener implements ActionListener
{
   private final ISession _session;
   private CatalogsPanel _catalogsPanel;

   public CatalogsComboListener(ISession session, CatalogsPanel catalogsPanel)
   {
      _session = session;
      _catalogsPanel = catalogsPanel;
   }

   public void actionPerformed(ActionEvent evt)
   {
      String selectedCatalog = _catalogsPanel.getSelectedCatalog();
      if (selectedCatalog != null)
      {
         try
         {
            //_session.getSQLConnection().setCatalog(selectedCatalog);
            _session.getConnectionPool().setSessionCatalog(selectedCatalog);
            refreshSchemaAndTree();
         }
         catch (Exception ex)
         {
            _session.showErrorMessage(ex);
            _catalogsPanel.refreshCatalogs();
         }
      }
   }

   private void refreshSchemaAndTree()
   {
      // Not needed here. Schema is reloaded in call api.refreshTree(true);
      //_session.getSchemaInfo().reloadAll();

      final String selectedCatalog = _catalogsPanel.getSelectedCatalog();
      IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
      api.refreshTree(true);

      ObjectTreeSearchResultFuture resultFuture =
            api.selectInObjectTree(selectedCatalog, null, new FilterMatcher("TABLE", null), ObjectTreeFinderGoToNextResultHandle.DONT_GO_TO_NEXT_RESULT_HANDLE);

      resultFuture.addFinishedListenerOrdered(tn -> onFindFinished(tn, api));
   }

   private void onFindFinished(TreePath tn, IObjectTreeAPI api)
   {
      if (tn != null)
      {
         ObjectTreeNode[] nodes = api.getSelectedNodes();

         if (nodes.length > 0)
         {
            ObjectTreeNode tableNode = nodes[0];

            // send a tree expansion event to the object tree
            api.expandNode(tableNode);
         }
      }
   }
}
