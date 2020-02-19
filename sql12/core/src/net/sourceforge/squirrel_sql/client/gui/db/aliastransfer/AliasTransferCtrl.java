package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasFolderState;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.db.IToogleableAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class AliasTransferCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasTransferCtrl.class);

   private static final String PREF_LAST_EXPORT_FILE_DIR = "AliasTransferCtrl.last.export.file.dir";

   private static final String ZIP_ENTRY_SQL_ALIASES = "export_SQLAliases23.xml";
   private static final String ZIP_ENTRY_ALIAS_TREE = "export_SQLAliases23_treeStructure.xml";
   private static final String ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME = "export_driverIdentifierToName.props";

   private final AliasTransferDialog _dlg;
   private IToogleableAliasesList _aliasesList;

   public AliasTransferCtrl(IToogleableAliasesList aliasesList)
   {
      _aliasesList = aliasesList;
      _dlg = new AliasTransferDialog(Main.getApplication().getMainFrame());

      locateDialogBesidesAliases();

      ExportImportTreeHandler exportImportTreeHandler = new ExportImportTreeHandler(_dlg.treeExportedAliases);

      _dlg.btnExport.addActionListener(e -> onExport(exportImportTreeHandler));
      _dlg.btnImport.addActionListener(e -> onImport(exportImportTreeHandler));

      GUIUtils.enableCloseByEscape(_dlg);
      _dlg.setVisible(true);
   }

   private void onImport(ExportImportTreeHandler exportImportTreeHandler)
   {
      try
      {
         JFileChooser importFC = createFileChooser();

         importFC.setDialogTitle(s_stringMgr.getString("AliasTransferCtrl.import.file.dialog.title"));

         if (importFC.showOpenDialog(_dlg) != JFileChooser.APPROVE_OPTION)
         {
            return;
         }

         File importFile = importFC.getSelectedFile();

         ZipFile zipIn = new ZipFile(importFile);

         Enumeration<? extends ZipEntry> entries = zipIn.entries();

         XMLBeanReader sqlAliasesReader = null;
         XMLBeanReader treeFolderStateReader = null;
         Properties driverIdentifierToName = null;
         while(entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();

            if(ZIP_ENTRY_SQL_ALIASES.equals(entry.getName()))
            {
               sqlAliasesReader = new XMLBeanReader();
               sqlAliasesReader.load(new InputStreamReader(zipIn.getInputStream(entry)));
            }
            else if(ZIP_ENTRY_ALIAS_TREE.equals(entry.getName()))
            {
               treeFolderStateReader = new XMLBeanReader();
               treeFolderStateReader.load(new InputStreamReader(zipIn.getInputStream(entry)));
            }
            else if(ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME.equals(entry.getName()))
            {
               driverIdentifierToName = new Properties();
               driverIdentifierToName.load(new InputStreamReader(zipIn.getInputStream(entry)));
            }
            else
            {
               throw new IllegalStateException("Unexpected zip entry: " + entry.getName());
            }
         }

         if(null == sqlAliasesReader)
         {
            throw new IllegalStateException("Zip did not contain: " + ZIP_ENTRY_SQL_ALIASES);
         }
         if(null == treeFolderStateReader)
         {
            throw new IllegalStateException("Zip did not contain: " + ZIP_ENTRY_ALIAS_TREE);
         }
         if(null == driverIdentifierToName)
         {
            throw new IllegalStateException("Zip did not contain: " + ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME);
         }

         List<SQLAlias> sqlAliases = sqlAliasesReader.getBeans();

         AssignDriversCtrl assignDriversCtrl = new AssignDriversCtrl(driverIdentifierToName, sqlAliases, _dlg);

         if(false == assignDriversCtrl.areAllDriversAssigned())
         {
            return;
         }

         assignDriversCtrl.updateDriverIdentifiersInAliases(sqlAliases, assignDriversCtrl);

         AliasFolderState aliasFolderState = (AliasFolderState) treeFolderStateReader.getBeans().get(0);

         exportImportTreeHandler.load(sqlAliases, aliasFolderState);

      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private void onExport(ExportImportTreeHandler exportImportTreeHandler)
   {
      try
      {
         if(exportImportTreeHandler.isEmpty())
         {
            JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasTransferCtrl.no.aliases.to.export"));

            return;
         }

         JFileChooser exportFC = createFileChooser();
         exportFC.setDialogTitle(s_stringMgr.getString("AliasTransferCtrl.export.file.dialog.title"));

         if (exportFC.showSaveDialog(_dlg) != JFileChooser.APPROVE_OPTION)
         {
            return;
         }

         File exportFile = exportFC.getSelectedFile();

         Props.putString(PREF_LAST_EXPORT_FILE_DIR, exportFile.getParent());


         if(false == exportFile.getName().toLowerCase().endsWith(".zip"))
         {
            exportFile = new File(exportFile.getAbsolutePath() + ".zip");
         }

         List<SQLAlias> sqlAliasesToExport = exportImportTreeHandler.getSqlAliasesToExport();
         XMLBeanWriter sqlAliasesWriter = new XMLBeanWriter();
         sqlAliasesWriter.addIteratorToRoot(sqlAliasesToExport.iterator());


         AliasFolderState state = exportImportTreeHandler.getAliasFolderState();
         XMLBeanWriter treeFolderStateWriter = new XMLBeanWriter(state);


         ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(exportFile));

         writeXMLBeanZipEntry(zipOut, sqlAliasesWriter, ZIP_ENTRY_SQL_ALIASES);
         writeXMLBeanZipEntry(zipOut, treeFolderStateWriter, ZIP_ENTRY_ALIAS_TREE);

         writeDriverIdentifierToNamePropZipEntry(sqlAliasesToExport, zipOut);

         zipOut.close();


         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("AliasTransferCtrl.file.export.success.msg", exportFile.getAbsolutePath()));


         String[] selectionValues =
               {
                     s_stringMgr.getString("AliasTransferCtrl.export.completed.ok"),
                     s_stringMgr.getString("AliasTransferCtrl.export.completed.ok.show.in.file.manager"),
               };

         int selectIndex = JOptionPane.showOptionDialog(
               _dlg,
               s_stringMgr.getString("AliasTransferCtrl.file.export.success.dlg.msg", exportFile.getAbsolutePath()),
               s_stringMgr.getString("AliasTransferCtrl.file.export.success.dlg.title"),
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               null,
               selectionValues,
               selectionValues[0]);

         if (selectIndex == 1)
         {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(exportFile.getParentFile());
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void writeDriverIdentifierToNamePropZipEntry(List<SQLAlias> sqlAliasesToExport, ZipOutputStream zipOut) throws IOException
   {
      Properties props = createDriverIdentifierToNameProps(sqlAliasesToExport);

      ZipEntry zipEntry = new ZipEntry(ZIP_ENTRY_DRIVER_IDENTIFIER_TO_NAME);
      zipOut.putNextEntry(zipEntry);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      props.store(bos, "JBCD-Driver identifier to driver name");
      zipOut.write(bos.toByteArray());
      zipOut.flush();
      zipOut.closeEntry();
   }

   private Properties createDriverIdentifierToNameProps(List<SQLAlias> sqlAliasesToExport)
   {
      Properties ret = new Properties();

      DataCache dataCache = Main.getApplication().getDataCache();

      sqlAliasesToExport.forEach( a -> ret.put(a.getDriverIdentifier().toString(), dataCache.getDriver(a.getDriverIdentifier()).getName()));

      return ret;
   }

   private JFileChooser createFileChooser()
   {
      JFileChooser exportFC = new JFileChooser(Props.getString(PREF_LAST_EXPORT_FILE_DIR, System.getProperty("user.home")));

      for (FileFilter chooseableFileFilter : exportFC.getChoosableFileFilters())
      {
         exportFC.removeChoosableFileFilter(chooseableFileFilter);
      }

      FileExtensionFilter zipFilter = new FileExtensionFilter("Export zip files", new String[]{".zip"});
      exportFC.addChoosableFileFilter(zipFilter);
      return exportFC;
   }

   private void writeXMLBeanZipEntry(ZipOutputStream zipOut, XMLBeanWriter xmlBeanWriter, String fileNameInZip) throws IOException
   {
      ZipEntry zipEntry;
      ByteArrayOutputStream bos;

      zipEntry = new ZipEntry(fileNameInZip);
      zipOut.putNextEntry(zipEntry);
      bos = new ByteArrayOutputStream();
      xmlBeanWriter.saveToOutputStream(bos);
      zipOut.write(bos.toByteArray());
      zipOut.flush();
      zipOut.closeEntry();
   }


   private void locateDialogBesidesAliases()
   {
      Point locOnScreen = GUIUtils.getScreenLocationFor(_aliasesList.getComponent());

      int x = locOnScreen.x + _aliasesList.getComponent().getWidth() + new JSplitPane().getDividerSize();

      Rectangle boundsOnScreen = new Rectangle(x, locOnScreen.y, Math.max(_aliasesList.getComponent().getWidth(), 250), _aliasesList.getComponent().getHeight());

      _dlg.setBounds(boundsOnScreen);
   }
}
