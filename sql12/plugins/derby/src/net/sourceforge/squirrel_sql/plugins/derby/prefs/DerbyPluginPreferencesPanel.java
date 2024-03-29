/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.plugins.derby.prefs;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;


/**
 * Adds the preference widget for allowing the user to specify whether or not 
 * Derby extended CLOB support should be enabled.
 *  
 * @author manningr 
 */
public class DerbyPluginPreferencesPanel extends
        PluginQueryTokenizerPreferencesPanel {


    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DerbyPluginPreferencesPanel.class);   
    
    static interface i18n {
       
       //i18n[DerbyPluginPreferencesPanel.readClobsFullyCheckBoxLabel=Enable 
       //extended Derby CLOB support]
       String READ_DERBY_CLOBS_FULLY_LABEL =  
          s_stringMgr.getString("DerbyPluginPreferencesPanel.readClobsFullyCheckBoxLabel");

       //i18n[OraclePluginPreferencesPanel.readClobsFullyCheckBoxTT=Read all 
       //CLOB data when the Contents tab is displayed]
       String READ_DERBY_CLOBS_FULLY_TT = 
          s_stringMgr.getString("DerbyPluginPreferencesPanel.readClobsFullyCheckBoxTT");
       
    }
    
    /** The checkbox for specifying exclusion of recycle bin tables */
    private final static JCheckBox readClobsFullyCheckBox = 
        new JCheckBox(i18n.READ_DERBY_CLOBS_FULLY_LABEL);
    
    
    /**
     * Construct a new PreferencesPanel.
     * @param prefs
     * @param databaseName
     */
    public DerbyPluginPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr) 
    {
        super(prefsMgr, "Derby");
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel#createTopPanel()
     */
    @Override
    protected JPanel createTopPanel() {
        JPanel result = super.createTopPanel();
        int lastY = super.lastY;
        addReadClobsFullyCheckBox(result, 0, lastY++);
        return result;
    }

    private void addReadClobsFullyCheckBox(JPanel result, int col, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.gridwidth = 2;  // Span across two columns
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,5,0,0);
        readClobsFullyCheckBox.setToolTipText(i18n.READ_DERBY_CLOBS_FULLY_TT);
        result.add(readClobsFullyCheckBox, c);        
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel#loadData()
     */
    @Override
    protected void loadData() {
        super.loadData();
        IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
        DerbyPreferenceBean derbyPrefs = (DerbyPreferenceBean)prefs;
        readClobsFullyCheckBox.setSelected(derbyPrefs.isReadClobsFully());
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel#save()
     */
    @Override
    protected void save() {
        IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
        DerbyPreferenceBean derbyPrefs = (DerbyPreferenceBean)prefs;
        derbyPrefs.setReadClobsFully(readClobsFullyCheckBox.isSelected());
        super.save();
    }
    
    
        
    
}
