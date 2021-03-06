package net.sourceforge.squirrel_sql.plugins.db2.exp;
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.db2.sql.DB2Sql;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an expander for the schema nodes. It will add Sequence and UDF Object 
 * Type nodes to the schema node.
 *
 * @author manningr
 */
public class SchemaExpander implements INodeExpander
{

	/** Object that contains methods for retrieving SQL that works for each DB2 platform */
	private final DB2Sql db2Sql;
    
	/**
	 * Ctor.
	 * 
	 * @param db2Sql
	 *           Object that contains methods for retrieving SQL that works for each DB2 platform
	 */
	public SchemaExpander(DB2Sql db2Sql)
	{
		super();
		this.db2Sql = db2Sql;
	}

	/**
	 * Create the child nodes for the passed parent node and return them. Note
	 * that this method should <B>not</B> actually add the child nodes to the
	 * parent node as this is taken care of in the caller.
	 *
	 * @param	session	Current session.
	 * @param	node	Node to be expanded.
	 *
	 * @return	A list of <TT>ObjectTreeNode</TT> objects representing the child
	 *			nodes for the passed node.
	 */
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo seqInfo = new DatabaseObjectInfo(catalogName, schemaName, "SEQUENCE", DatabaseObjectType.SEQUENCE_TYPE_DBO, md);
		ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
		node.addExpander(new SequenceParentExpander(db2Sql));
		childNodes.add(node);

		IDatabaseObjectInfo udfInfo = new DatabaseObjectInfo(catalogName, schemaName, "UDF", DatabaseObjectType.UDF_TYPE_DBO, md);
		ObjectTreeNode udfnode = new ObjectTreeNode(session, udfInfo);
		udfnode.addExpander(new UDFParentExpander(db2Sql));
		childNodes.add(udfnode);

		return childNodes;
	}

}
