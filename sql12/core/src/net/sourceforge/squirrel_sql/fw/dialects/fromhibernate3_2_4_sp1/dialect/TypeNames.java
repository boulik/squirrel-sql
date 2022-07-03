//$Id: TypeNames.java 6254 2005-03-30 18:01:41Z oneovthafew $
package net.sourceforge.squirrel_sql.fw.dialects.fromhibernate3_2_4_sp1.dialect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class maps a type to names. Associations
 * may be marked with a capacity. Calling the get()
 * method with a type and actual size n will return
 * the associated name with smallest capacity >= n,
 * if available and an unmarked default type otherwise.
 * Eg, setting
 * <pre>
 *	names.put(type,        "TEXT" );
 *	names.put(type,   255, "VARCHAR($l)" );
 *	names.put(type, 65534, "LONGVARCHAR($l)" );
 * </pre>
 * will give you back the following:
 * <pre>
 *  names.get(type)         // --> "TEXT" (default)
 *  names.get(type,    100) // --> "VARCHAR(100)" (100 is in [0:255])
 *  names.get(type,   1000) // --> "LONGVARCHAR(1000)" (1000 is in [256:65534])
 *  names.get(type, 100000) // --> "TEXT" (default)
 * </pre>
 * On the other hand, simply putting
 * <pre>
 *	names.put(type, "VARCHAR($l)" );
 * </pre>
 * would result in
 * <pre>
 *  names.get(type)        // --> "VARCHAR($l)" (will cause trouble)
 *  names.get(type, 100)   // --> "VARCHAR(100)"
 *  names.get(type, 10000) // --> "VARCHAR(10000)"
 * </pre>
 *
 * @author Christoph Beck
 */
public class TypeNames {

	private HashMap weighted = new HashMap();
	private HashMap defaults = new HashMap();

	/**
	 * get default type name for specified type
	 * @param typecode the type key
	 * @return the default type name associated with specified key
	 */
	public String get(int typecode)  {
		String result = (String) defaults.get( new Integer(typecode) );
		if (result==null) throw new IllegalStateException("No Dialect mapping for JDBC type: " + typecode);
		return result;
	}

	/**
	 * get type name for specified type and size
	 * @param typecode the type key
	 * @param size the SQL length
	 * @param scale the SQL scale
	 * @param precision the SQL precision
	 * @return the associated name with smallest capacity >= size,
	 * if available and the default type name otherwise
	 */
	public String get(int typecode, int size, int precision, int scale) {
		Map map = (Map) weighted.get( new Integer(typecode) );
		if ( map!=null && map.size()>0 ) {
			// iterate entries ordered by capacity to find first fit
			Iterator entries = map.entrySet().iterator();
			while ( entries.hasNext() ) {
				Map.Entry entry = (Map.Entry)entries.next();
				if ( size <= ( (Integer) entry.getKey() ).intValue() ) {
					return replace( (String) entry.getValue(), size, precision, scale );
				}
			}
		}
		return replace( get(typecode), size, precision, scale );
	}
	
	private static String replace(String type, int size, int precision, int scale) {
		type = replaceOnce(type, "$s", Integer.toString(scale) );
		type = replaceOnce(type, "$l", Integer.toString(size) );
		return replaceOnce(type, "$p", Integer.toString(precision) );
	}

	/**
	 * set a type name for specified type key and capacity
	 * @param typecode the type key
	 */
	public void put(int typecode, int capacity, String value) {
		TreeMap map = (TreeMap)weighted.get( new Integer(typecode) );
		if (map == null) {// add new ordered map
			map = new TreeMap();
			weighted.put( new Integer(typecode), map );
		}
		map.put(new Integer(capacity), value);
	}

	/**
	 * set a default type name for specified type key
	 * @param typecode the type key
	 */
	public void put(int typecode, String value) {
		defaults.put( new Integer(typecode), value );
	}

	private static String replaceOnce(String template, String placeholder, String replacement) {
		int loc = template == null ? -1 : template.indexOf( placeholder );
		if ( loc < 0 ) {
			return template;
		}
		else {
			return new StringBuffer( template.substring( 0, loc ) )
					.append( replacement )
					.append( template.substring( loc + placeholder.length() ) )
					.toString();
		}
	}

}





