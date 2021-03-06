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
package net.sourceforge.squirrel_sql.plugins.oracle.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * A factory that creates OracleXmlTypeDataTypeComponents for rendering 
 * SYS.xmlltype columns.
 * 
 * @author manningr
 *
 */
public class OracleXmlTypeDataTypeComponentFactory implements
        IDataTypeComponentFactory {

    /**
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#constructDataTypeComponent()
    */
   @Override
public IDataTypeComponent constructDataTypeComponent() {
        return new OracleXmlTypeDataTypeComponent();
    }

   /**
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#getDialectType()
    */
   @Override
public DialectType getDialectType() {
      return DialectType.ORACLE;
   }
   
   

	@Override
	public boolean matches(DialectType dialectType, int sqlType,
			String sqlTypeName) {
		return new EqualsBuilder().append(getDialectType(), dialectType)
				.append(2007, sqlType)
				.append("SYS.XMLTYPE", sqlTypeName).isEquals();
	}
   
}
