package net.sourceforge.squirrel_sql.client.gui.session.catalogscombo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class CatalogsPanel extends JPanel 
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CatalogsPanel.class);

	private static final ILogger s_log = LoggerController.createLogger(CatalogsPanel.class);

	private ISession _session;
	private JComponent _parent;
	private SQLCatalogsComboBox _catalogsCmb;
	private PropertyChangeListener _connectionPropetryListener;

	public CatalogsPanel(ISession session, JComponent parent)
	{
		_session = session;
		_parent = parent;

		_connectionPropetryListener = evt -> onConnectionPropertyChanged(evt);

		setVisible(false);

		initInBackground();
	}

	private void onConnectionPropertyChanged(PropertyChangeEvent evt)
	{
		try
		{
			final String propName = evt.getPropertyName();
			if (propName == null ||
				propName.equals(ISQLConnection.IPropertyNames.CATALOG))
			{
				if (_catalogsCmb != null)
				{
					final ISQLConnection conn = _session.getSQLConnection();
					if (!StringUtils.equals(conn.getCatalog(), _catalogsCmb.getSelectedCatalog()))
					{
						_catalogsCmb.setSelectedCatalog(conn.getCatalog());
					}
				}
			}
		}
		catch (SQLException e)
		{
			s_log.error("Error processing Property ChangeEvent", e);
		}
	}


	private void initInBackground()
	{
		try
		{
			if(false == _session.getSQLConnection().getSQLMetaData().supportsCatalogs())
			{
				return;
			}

			final String[] catalogs = _session.getSQLConnection().getSQLMetaData().getCatalogs();

			if(null == catalogs || 0 == catalogs.length)
			{
				return;
			}

			final String selected = _session.getSQLConnection().getCatalog();

			_session.getSQLConnection().removePropertyChangeListener(_connectionPropetryListener);
			_session.getSQLConnection().addPropertyChangeListener(_connectionPropetryListener);

			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					initGuiInForeground(catalogs, selected);
				}
			});


		}
		catch (SQLException e)
		{
			s_log.error(s_stringMgr.getString("CatalogsPanel.error.retrievecatalog"), e);
		}
	}

	private void initGuiInForeground(String[] catalogs, String selected)
	{
		setLayout(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
		JLabel lblCatalogs = new JLabel(s_stringMgr.getString("CatalogsPanel.catalog"));
		add(lblCatalogs, gbc);


		_catalogsCmb = new SQLCatalogsComboBox();
		gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
		add(_catalogsCmb, gbc);

		_catalogsCmb.setCatalogs(catalogs, selected);

		Dimension prefSize = getPreferredSize();
		prefSize.width = lblCatalogs.getPreferredSize().width + _catalogsCmb.getPreferredSize().width + 20;
		setPreferredSize(prefSize);
		setMaximumSize(prefSize);

		GUIUtils.inheritBackground(this);

		setVisible(true);

		_parent.validate();

		_catalogsCmb.addActionListener(new CatalogsComboListener(_session, this));
	}

	public void refreshCatalogs()
	{
		removeAll();

		_session.getApplication().getThreadPool().addTask(() -> initInBackground());
	}

	public String getSelectedCatalog()
	{
		if (   null == _catalogsCmb
			 || false == _catalogsCmb.getSelectedItem() instanceof String) // Happens when instanceof SQLCatalogsComboBox.NoCatalogPlaceHolder. Perhaps NoCatalogPlaceHolder should be removed when it causes more trouble.
		{
			return null;
		}
		else
		{
			return (String) _catalogsCmb.getSelectedItem();
		}
	}

}
