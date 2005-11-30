package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
/**
 * This sheet allows the user to maintain global preferences.
 * JASON: Rename to GlobalPreferencesInternalFrame
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GlobalPreferencesSheet extends BaseInternalFrame
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GlobalPreferencesSheet.class);

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(GlobalPreferencesSheet.class);

	/** Singleton instance of this class. */
	private static GlobalPreferencesSheet s_instance;

	/** Application API. */
	private IApplication _app;

	/**
	 * List of all the panels (instances of
	 * <TT>IGlobalPreferencesPanel</TT> objects in shhet.
	 */
	private List _panels = new ArrayList();

	/** Sheet title. */
	private JLabel _titleLbl = new JLabel();

	public static final String PREF_KEY_GLOBAL_PREFS_SHEET_WIDTH = "Squirrel.globalPrefsSheetWidth";
	public static final String PREF_KEY_GLOBAL_PREFS_SHEET_HEIGHT = "Squirrel.globalPrefsSheetHeight";


	/**
	 * Ctor specifying the application API.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication passed.
	 */
	private GlobalPreferencesSheet(IApplication app)
	{
		super(s_stringMgr.getString("GlobalPreferencesSheet.title"), true);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		createGUI();

		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel) it.next();
			try
			{
				pnl.initialize(_app);
			}
			catch (Throwable th)
			{
				final String msg = s_stringMgr.getString("GlobalPreferencesSheet.error.loading", pnl.getTitle());
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
		setSize(getDimension());

		app.getMainFrame().addInternalFrame(this, true, null);
		GUIUtils.centerWithinDesktop(this);
		setVisible(true);

	}

	private Dimension getDimension()
	{
		return new Dimension(
			Preferences.userRoot().getInt(PREF_KEY_GLOBAL_PREFS_SHEET_WIDTH, 650),
			Preferences.userRoot().getInt(PREF_KEY_GLOBAL_PREFS_SHEET_HEIGHT, 600)
		);
	}


	/**
	 * Show the Preferences dialog
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT> object passed.
	 */
	public static synchronized void showSheet(IApplication app)
	{
		if (s_instance == null)
		{
			s_instance = new GlobalPreferencesSheet(app);
		}
		else
		{
			s_instance.moveToFront();
		}
	}

	public void dispose()
	{
		Dimension size = getSize();
		Preferences.userRoot().putInt(PREF_KEY_GLOBAL_PREFS_SHEET_WIDTH, size.width);
		Preferences.userRoot().putInt(PREF_KEY_GLOBAL_PREFS_SHEET_HEIGHT, size.height);

		for (Iterator it = _panels.iterator(); it.hasNext();)
      {
         IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel) it.next();
         pnl.uninitialize(_app);
      }

		synchronized (getClass())
		{
			s_instance = null;
		}
		super.dispose();
	}


	/**
	 * Set title of this frame. Ensure that the title label
	 * matches the frame title.
	 *
	 * @param	title	New title text.
	 */
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	/**
	 * Close this sheet.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed so save changes.
	 */
	private void performOk()
	{
		CursorChanger cursorChg = new CursorChanger(_app.getMainFrame());
		cursorChg.show();
		try
		{
			final boolean isDebug = s_log.isDebugEnabled();
			long start = 0;
			for (Iterator it = _panels.iterator(); it.hasNext();)
			{
				if (isDebug)
				{
					start = System.currentTimeMillis();
				}
				IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel)it.next();
				try
				{
					pnl.applyChanges();
				}
				catch (Throwable th)
				{
					final String msg = s_stringMgr.getString("GlobalPreferencesSheet.error.saving", pnl.getTitle());
					s_log.error(msg, th);
					_app.showErrorDialog(msg, th);
				}
				if (isDebug)
				{
					s_log.debug("Panel " + pnl.getTitle()
								+ " applied changes in "
								+ (System.currentTimeMillis() - start) + "ms");
				}
			}
		}
		finally
		{
			cursorChg.restore();
		}

		dispose();
	}

    private void performSave() {
        CursorChanger cursorChg = new CursorChanger(_app.getMainFrame());
        cursorChg.show();
        try
        {
            final boolean isDebug = s_log.isDebugEnabled();
            long start = 0;
            for (Iterator it = _panels.iterator(); it.hasNext();)
            {
                if (isDebug)
                {
                    start = System.currentTimeMillis();
                }
                IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel)it.next();
                try
                {
                    pnl.applyChanges();
                }
                catch (Throwable th)
                {
                    final String msg = s_stringMgr.getString("GlobalPreferencesSheet.error.saving", pnl.getTitle());
                    s_log.error(msg, th);
                    _app.showErrorDialog(msg, th);
                }
                if (isDebug)
                {
                    s_log.debug("Panel " + pnl.getTitle()
                                + " applied changes in "
                                + (System.currentTimeMillis() - start) + "ms");
                }
            }
        }
        finally
        {
            _app.getSquirrelPreferences().save();
            cursorChg.restore();
        }
    }
    
	/**
	 * Create user interface.
	 */
	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		// Add panels for core Squirrel functionality.
		_panels.add(new GeneralPreferencesPanel());
		_panels.add(new SQLPreferencesPanel(_app.getMainFrame()));
		_panels.add(new ProxyPreferencesPanel());
		_panels.add(new DataTypePreferencesPanel());

		// Go thru all loaded plugins asking for panels.
		PluginInfo[] plugins = _app.getPluginManager().getPluginInformation();
		for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx)
		{
			PluginInfo pi = plugins[plugIdx];
			if (pi.isLoaded())
			{
				IGlobalPreferencesPanel[] pnls = pi.getPlugin().getGlobalPreferencePanels();
				if (pnls != null && pnls.length > 0)
				{
					for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx)
					{
						_panels.add(pnls[pnlIdx]);
					}
				}
			}
		}

		// Add all panels to the tabbed pane.
		JTabbedPane tabPane = UIFactory.getInstance().createTabbedPane();
		for (Iterator it = _panels.iterator(); it.hasNext();)
		{
			IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel) it.next();
			String pnlTitle = pnl.getTitle();
			String hint = pnl.getHint();
			tabPane.addTab(pnlTitle, null, pnl.getPanelComponent(), hint);
		}

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		contentPane.add(_titleLbl, gbc);

		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(tabPane, gbc);

		++gbc.gridy;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);
	}

	/**
	 * Create panel at bottom containing the buttons.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("GlobalPreferencesSheet.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
        JButton saveBtn = new JButton(s_stringMgr.getString("GlobalPreferencesSheet.save"));
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                performSave();
            }
        });
		JButton closeBtn = new JButton(s_stringMgr.getString("GlobalPreferencesSheet.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });

		pnl.add(okBtn);
        pnl.add(saveBtn);
		pnl.add(closeBtn);

		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
}
