package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GutterItemsCreator
{

   public static List<GutterItem> createGutterItems(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, String changeTrackBase)
   {
      try
      {
         if(null == changeTrackBase)
         {
            return Collections.EMPTY_LIST;
         }


         List<String> changeTrackBaseLines = Arrays.asList(changeTrackBase.split("\n", -1));

         List<String> currentLines = Arrays.asList(sqlEntry.getText().split("\n", -1));

//         if(0 < currentLines.size() && "".equals(currentLines.get(currentLines.size() - 1)))
//         {
//            currentLines.remove(currentLines.size() - 1);
//         }

         Patch<String> diff = DiffUtils.diff(changeTrackBaseLines, currentLines);


         List<GutterItem> gutterItems = new ArrayList<>();

         for (AbstractDelta<String> delta : diff.getDeltas())
         {
            switch (delta.getType())
            {
               case INSERT:
                  gutterItems.add(createAddedLinesGutterItem(sqlEntry, changeTrackPanel, (InsertDelta<String>) delta));
                  break;
               case CHANGE:
                  gutterItems.add(createChangedLinesGutterItem(sqlEntry, changeTrackPanel, (ChangeDelta<String>) delta));
                  break;
               case DELETE:
                  gutterItems.add(createDeletedLinesGutterItem(sqlEntry, changeTrackPanel, (DeleteDelta<String>) delta, currentLines.size()));
                  break;
            }
         }

         return gutterItems;

//      currentGutterItems.add(new DeletedLinesGutterItem(changeTrackPanel, sqlEntry,12, "Hier Stand fr??her mal Gerd\nund wurde gel??scht"));
//      currentGutterItems.add(new ChangedLinesGutterItem(changeTrackPanel, sqlEntry,25, 3, "Hier Stand fr??her mal Gerd\nund wurde ge??ndert"));
//      currentGutterItems.add(new AddedLinesGutterItem(changeTrackPanel, sqlEntry,35, 2));
//      return gutterItems;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

   }

   private static GutterItem createDeletedLinesGutterItem(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, DeleteDelta<String> delta, int currentLineCount)
   {
      return new DeletedLinesGutterItem(changeTrackPanel, sqlEntry, currentLineCount, delta);
   }

   private static GutterItem createChangedLinesGutterItem(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, ChangeDelta<String> delta)
   {
//      String formerText = String.join("\n", delta.getSource().getLines())  + "\n";
//      return new ChangedLinesGutterItem(changeTrackPanel, sqlEntry, delta.getTarget().getPosition() + 1, delta.getTarget().getLines().size(), formerText);
      return new ChangedLinesGutterItem(changeTrackPanel, sqlEntry, delta);
   }

   private static GutterItem createAddedLinesGutterItem(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, InsertDelta<String> delta)
   {
      return new AddedLinesGutterItem(changeTrackPanel, sqlEntry,delta);
   }
}
