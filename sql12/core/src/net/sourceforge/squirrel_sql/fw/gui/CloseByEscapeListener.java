package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.*;

@FunctionalInterface
public interface CloseByEscapeListener
{
   void willCloseByEcape(JDialog dialog);
}
