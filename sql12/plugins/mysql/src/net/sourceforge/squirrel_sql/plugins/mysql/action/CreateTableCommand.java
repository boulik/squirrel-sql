package net.sourceforge.squirrel_sql.plugins.mysql.action;

/*
 * Copyright (C) 2003 Arun Kapilan.P
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mysql.util.FieldDetails;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * CreateTableCommand.java Created on June 15, 2003, 2:44 PM
 * 
 * @author Arun Kapilan.P
 */
public class CreateTableCommand extends JDialog implements ICommand
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CreateTableCommand.class);

	private javax.swing.JButton btAdd;

	private javax.swing.JButton btRemove;

	private javax.swing.JButton btCancel;

	private javax.swing.JButton btCreateTable;

	private javax.swing.JComboBox cbFieldType;

	private javax.swing.JComboBox cbTableType;

	private javax.swing.JCheckBox chAuto;

	private javax.swing.JCheckBox chBinary;

	private javax.swing.JCheckBox chIndex;

	private javax.swing.JCheckBox chNotNull;

	private javax.swing.JCheckBox chPrimary;

	private javax.swing.JCheckBox chUnique;

	private javax.swing.JCheckBox chUnsigned;

	private javax.swing.JCheckBox chZeroFill;

	private javax.swing.JList listFields;

	private javax.swing.JLabel lbDefault;

	private javax.swing.JLabel lbFieldLength;

	private javax.swing.JLabel lbFieldProp;

	private javax.swing.JLabel lbFieldType;

	private javax.swing.JLabel lbFields;

	private javax.swing.JLabel lbTableName;

	private javax.swing.JLabel lbTableType;

	private javax.swing.JTextField tfDefault;

	private javax.swing.JTextField tfFieldLength;

	private javax.swing.JTextField tfFieldName;

	private javax.swing.JTextField tfTableName;

	private javax.swing.JDialog jd;

	// private DBUtils dbUtils;
	protected String SQLCommandRoot = "CREATE TABLE ";

	protected String SQLCommand = "";

	/** Logger for this class. */
	// private final static ILogger s_log =
	// LoggerController.createLogger(CreateMysqlTableScriptCommand.class);
	/** Current session. */
	private ISession _session;

	/** Current plugin. */

	private final MysqlPlugin _plugin;

	public CreateTableCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	public void execute()
	{
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this
	 * code. The content of this method is always regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		lbTableName = new javax.swing.JLabel();
		tfTableName = new javax.swing.JTextField();
		lbTableType = new javax.swing.JLabel();
		cbTableType = new javax.swing.JComboBox();
		lbFields = new javax.swing.JLabel();
		tfFieldName = new javax.swing.JTextField();
		btAdd = new javax.swing.JButton();
		lbFieldProp = new javax.swing.JLabel();
		lbFieldType = new javax.swing.JLabel();
		cbFieldType = new javax.swing.JComboBox();
		lbFieldLength = new javax.swing.JLabel();
		tfFieldLength = new javax.swing.JTextField();
		lbDefault = new javax.swing.JLabel();
		tfDefault = new javax.swing.JTextField();
		chPrimary = new javax.swing.JCheckBox();
		chIndex = new javax.swing.JCheckBox();
		chUnique = new javax.swing.JCheckBox();
		chBinary = new javax.swing.JCheckBox();
		chNotNull = new javax.swing.JCheckBox();
		chUnsigned = new javax.swing.JCheckBox();
		chAuto = new javax.swing.JCheckBox();
		chZeroFill = new javax.swing.JCheckBox();
		listFields = new javax.swing.JList(new DefaultListModel());
		btCreateTable = new javax.swing.JButton();
		btRemove = new javax.swing.JButton();
		btCancel = new javax.swing.JButton();
		jd = new JDialog(_session.getApplication().getMainFrame(),
		// i18n[mysql.createTableComm=Create Table...]
			s_stringMgr.getString("mysql.createTableComm"));
		jd.getContentPane().setLayout(null);

		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		// i18n[mysql.tableNamelbl=Table Name:]
		lbTableName.setText(s_stringMgr.getString("mysql.tableNamelbl"));
		jd.getContentPane().add(lbTableName);
		lbTableName.setBounds(20, 30, 70, 16);

		tfTableName.setText("TableName");
		jd.getContentPane().add(tfTableName);
		tfTableName.setBounds(120, 30, 100, 20);

		lbTableType.setFont(new java.awt.Font("Dialog", 0, 12));
		lbTableType.setText("Table Type:");
		jd.getContentPane().add(lbTableType);
		lbTableType.setBounds(20, 60, 70, 16);

		cbTableType.setFont(new java.awt.Font("Dialog", 0, 12));
		cbTableType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Automatic>", "ISAM",
				"MyISAM", "MERGE", "InnoDb", "HEAP", "BDB" }));
		jd.getContentPane().add(cbTableType);
		cbTableType.setBounds(120, 60, 100, 20);

		lbFields.setText("Fields:");
		lbFields.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
		jd.getContentPane().add(lbFields);
		lbFields.setBounds(10, 120, 40, 18);

		tfFieldName.setText("FieldName");
		tfFieldName.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusGained(java.awt.event.FocusEvent evt)
			{
				tfFieldNameFocusGained(evt);
			}
		});
		jd.getContentPane().add(tfFieldName);
		tfFieldName.setBounds(10, 150, 110, 20);

		btAdd.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.createAdd=Add]
		btAdd.setText(s_stringMgr.getString("mysql.createAdd"));
		btAdd.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btAddActionPerformed(evt);
			}
		});

		jd.getContentPane().add(btAdd);
		btAdd.setBounds(130, 150, 80, 26);

		// i18n[mysql.fieldProps=Field Properties:]
		lbFieldProp.setText(s_stringMgr.getString("mysql.fieldProps"));
		lbFieldProp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
		jd.getContentPane().add(lbFieldProp);
		lbFieldProp.setBounds(220, 150, 100, 20);

		lbFieldType.setFont(new java.awt.Font("Dialog", 0, 12));
		lbFieldType.setText("Type");
		jd.getContentPane().add(lbFieldType);
		lbFieldType.setBounds(220, 190, 41, 16);

		cbFieldType.setFont(new java.awt.Font("Dialog", 0, 12));
		cbFieldType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TINYINT", "SMALLINT",
				"MEDIUMINT", "INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "DATE", "DATETIME", "TIMESTAMP",
				"TIME", "YEAR", "CHAR", "VARCHAR", "TINYBLOB", "TINYTEXT", "TEXT", "BLOB", "MEDIUMBLOB",
				"MEDIUMTEXT", "LONGBLOB", "LONGTEXT", "ENUM", "SET" }));
		cbFieldType.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				cbFieldTypeActionPerformed(evt);
			}
		});
		jd.getContentPane().add(cbFieldType);
		cbFieldType.setBounds(300, 190, 110, 20);

		lbFieldLength.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.lengtSet=Length/Set]
		lbFieldLength.setText(s_stringMgr.getString("mysql.lengtSet"));
		jd.getContentPane().add(lbFieldLength);
		lbFieldLength.setBounds(220, 220, 70, 16);

		jd.getContentPane().add(tfFieldLength);
		tfFieldLength.setBounds(300, 220, 110, 20);

		lbDefault.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.defaultValue=Default Value]
		lbDefault.setText(s_stringMgr.getString("mysql.defaultValue"));
		jd.getContentPane().add(lbDefault);
		lbDefault.setBounds(220, 250, 80, 16);

		jd.getContentPane().add(tfDefault);
		tfDefault.setBounds(300, 250, 110, 20);

		chPrimary.setFont(new java.awt.Font("Dialog", 0, 12));
		chPrimary.setText("Primary");
		chPrimary.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				chPrimaryItemStateChanged(evt);
			}
		});
		jd.getContentPane().add(chPrimary);
		chPrimary.setBounds(220, 290, 67, 20);

		chIndex.setFont(new java.awt.Font("Dialog", 0, 12));
		chIndex.setText("Index");
		jd.getContentPane().add(chIndex);
		chIndex.setBounds(290, 290, 54, 20);

		chUnique.setFont(new java.awt.Font("Dialog", 0, 12));
		chUnique.setText("Unique");
		jd.getContentPane().add(chUnique);
		chUnique.setBounds(350, 290, 65, 20);

		chBinary.setFont(new java.awt.Font("Dialog", 0, 12));
		chBinary.setText("Binary");
		jd.getContentPane().add(chBinary);
		chBinary.setBounds(220, 320, 70, 20);

		chNotNull.setFont(new java.awt.Font("Dialog", 0, 12));
		chNotNull.setText("Not Null");
		jd.getContentPane().add(chNotNull);
		chNotNull.setBounds(290, 320, 70, 20);

		chUnsigned.setFont(new java.awt.Font("Dialog", 0, 12));
		chUnsigned.setText("Unsigned");
		jd.getContentPane().add(chUnsigned);
		chUnsigned.setBounds(360, 320, 80, 20);

		chAuto.setFont(new java.awt.Font("Dialog", 0, 12));
		chAuto.setText("Auto Increment");
		jd.getContentPane().add(chAuto);
		chAuto.setBounds(220, 350, 110, 20);

		chZeroFill.setFont(new java.awt.Font("Dialog", 0, 12));
		chZeroFill.setText("Zero Fill");
		jd.getContentPane().add(chZeroFill);
		chZeroFill.setBounds(330, 350, 69, 20);

		listFields.addListSelectionListener(new javax.swing.event.ListSelectionListener()
		{
			public void valueChanged(javax.swing.event.ListSelectionEvent evt)
			{
				listFieldsValueChanged(evt);
			}
		});
		JScrollPane scrollPane = new JScrollPane(listFields);
		jd.getContentPane().add(scrollPane);
		scrollPane.setBounds(10, 190, 110, 150);

		btCreateTable.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.createTableBtn=Create table]
		btCreateTable.setText(s_stringMgr.getString("mysql.createTableBtn"));
		btCreateTable.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btCreateTableActionPerformed(evt);
			}
		});
		jd.getContentPane().add(btCreateTable);

		btCreateTable.setBounds(221, 380, 110, 26);
		btRemove.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.btRemove=Remove]
		btRemove.setText(s_stringMgr.getString("mysql.btRemove"));
		btRemove.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btRemoveActionPerformed(evt);
			}
		});
		jd.getContentPane().add(btRemove);
		btRemove.setBounds(130, 190, 80, 26);

		btCancel.setFont(new java.awt.Font("Dialog", 0, 12));
		// i18n[mysql.btCancel=Cancel]
		btCancel.setText(s_stringMgr.getString("mysql.btCancel"));
		btCancel.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				btCancelActionPerformed(evt);
			}
		});

		jd.getContentPane().add(btCancel);
		btCancel.setBounds(340, 380, 73, 26);

		jd.pack();
		jd.setSize(450, 450);
		jd.setLocation(100, 100);
		jd.setVisible(true);

	}

	// Set the attributes for the fields in the class FieldDetails
	private void btAddActionPerformed(java.awt.event.ActionEvent evt)
	{
		FieldDetails fd = new FieldDetails();

		fd.setFieldName(tfFieldName.getText());
		fd.setFieldLength(tfFieldLength.getText());
		fd.setFieldType((String) cbFieldType.getSelectedItem());
		fd.setDefault(tfDefault.getText());
		fd.setPrimary(chPrimary.isSelected());
		fd.setUnique(chUnique.isSelected());
		fd.setIndex(chIndex.isSelected());
		fd.setBinary(chBinary.isSelected());
		fd.setNotNull(chNotNull.isSelected());
		fd.setUnisigned(chUnsigned.isSelected());
		fd.setAutoIncrement(chAuto.isSelected());
		fd.setZeroFill(chZeroFill.isSelected());
		DefaultListModel listModel = (DefaultListModel) listFields.getModel();
		listModel.addElement(fd);

	}

	// Display the field attributes when selected in the list
	private void listFieldsValueChanged(

	javax.swing.event.ListSelectionEvent evt)
	{

		int index = listFields.getSelectedIndex();
		DefaultListModel listModel = (DefaultListModel) listFields.getModel();
		FieldDetails fd = (FieldDetails) listModel.elementAt(index);

		tfFieldName.setText(fd.getFieldName());
		cbFieldType.setSelectedItem(fd.getFieldType());
		tfFieldLength.setText(fd.getFieldLength());
		tfDefault.setText(fd.getDefault());
		chPrimary.setSelected(fd.IsPrimary());
		chUnique.setSelected(fd.IsUnique());
		chIndex.setSelected(fd.IsIndex());
		chBinary.setSelected(fd.IsBinary());
		chNotNull.setSelected(fd.IsNotNull());
		chUnsigned.setSelected(fd.IsUnisigned());
		chAuto.setSelected(fd.IsAutoIncrement());
		chZeroFill.setSelected(fd.IsZeroFill());

	}

	private void btCreateTableActionPerformed(java.awt.event.ActionEvent evt)
	{
		ISQLConnection con = _session.getSQLConnection();
		String query = getQuery();
		Statement stmt = null;
		try
		{
			stmt = con.createStatement();
			stmt.execute(query);
			_session.getSessionInternalFrame().getObjectTreeAPI().refreshTree();
			jd.setVisible(false);
			jd.dispose();
			JOptionPane.showMessageDialog(null,
			// i18n[mysql.msgTableCreated=Table {0} created]
				s_stringMgr.getString("mysql.msgTableCreated", tfTableName.getText()));
		}
		catch (SQLException ex)
		{
			_session.showErrorMessage(ex);

		} finally {
			SQLUtilities.closeStatement(stmt);
		}
	}

	private void chPrimaryItemStateChanged(java.awt.event.ItemEvent evt)
	{
		if (chPrimary.isSelected()) chNotNull.setSelected(true);

	}

	private void cbFieldTypeActionPerformed(java.awt.event.ActionEvent evt)
	{
		tfFieldLength.setText("5");

		if (cbFieldType.getSelectedItem().equals("VARCHAR"))
		{
			chBinary.setEnabled(true);
			chUnsigned.setEnabled(false);
			chZeroFill.setEnabled(false);
		}
		else
		{
			chUnsigned.setEnabled(false);
			chZeroFill.setEnabled(false);
		}
		if (cbFieldType.getSelectedItem().equals("INT"))
		{
			chBinary.setEnabled(false);
			chUnsigned.setEnabled(true);
			chZeroFill.setEnabled(true);
		}
	}

	// Reset the checkbox selected index false when fieldname gains focus
	private void tfFieldNameFocusGained(java.awt.event.FocusEvent evt)
	{
		cbFieldType.setSelectedIndex(0);
		chAuto.setSelected(false);
		chBinary.setSelected(false);
		chIndex.setSelected(false);
		chNotNull.setSelected(false);
		chPrimary.setSelected(false);
		chUnique.setSelected(false);
		chUnsigned.setSelected(false);
		chZeroFill.setSelected(false);
	}

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)
	{
		setVisible(false);
		dispose();
	}

	// Remove the Fields added to the list
	private void btRemoveActionPerformed(java.awt.event.ActionEvent evt)
	{
		int index = listFields.getSelectedIndex();
		DefaultListModel listModel = (DefaultListModel) listFields.getModel();
		listModel.remove(index);
		listFields.invalidate();
	}

	private void btCancelActionPerformed(java.awt.event.ActionEvent evt)
	{
		jd.setVisible(false);
		jd.dispose();
		jd.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public String getQuery()
	{

		DefaultListModel listModel = (DefaultListModel) listFields.getModel();
		SQLCommand = "(";
		String[] rowData = new String[listModel.getSize()];
		for (int i = 0; i < rowData.length; i++)
			rowData[i] = "";
		for (int i = 0; i < rowData.length; i++)
		{
			FieldDetails fd = (FieldDetails) listModel.elementAt(i);
			rowData[i] += fd.getFieldName();
			rowData[i] += " ";
			rowData[i] += fd.getFieldType();
			rowData[i] += "(";
			rowData[i] += fd.getFieldLength();
			rowData[i] += ")";
			if (fd.IsUnisigned()) rowData[i] += " UNSIGNED ";
			if (fd.IsBinary()) rowData[i] += " BINARY ";
			if (fd.IsZeroFill()) rowData[i] += " ZEROFILL ";
			if (fd.getDefault().length() > 0) rowData[i] += " DEFAULT '" + fd.getDefault() + "'";
			if (fd.IsNotNull()) rowData[i] += " NOT NULL ";
			if (fd.IsAutoIncrement()) rowData[i] += "AUTO_INCREMENT ";
			if (fd.IsPrimary()) rowData[i] += ", PRIMARY KEY(" + fd.getFieldName() + ")";
			if (fd.IsUnique()) rowData[i] += ", UNIQUE(" + fd.getFieldName() + ")";
			if (fd.IsIndex()) rowData[i] += ", INDEX(" + fd.getFieldName() + ")";

		}
		for (int i = 0; i < rowData.length; i++)
		{
			SQLCommand += rowData[i];
			if (i < (rowData.length - 1)) SQLCommand += ", ";

		}
		SQLCommand += ")";
		if (cbTableType.getSelectedIndex() > 0) SQLCommand += " TYPE = " + cbTableType.getSelectedItem() + " ;";
		SQLCommandRoot += tfTableName.getText();
		SQLCommand = SQLCommandRoot + SQLCommand;
		return (SQLCommand);
	}

}
