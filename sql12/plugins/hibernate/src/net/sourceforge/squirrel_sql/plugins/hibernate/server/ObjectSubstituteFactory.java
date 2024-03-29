package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


/**
 * Needed to remove lazy loading non collection proxies. These proxies caused serialization
 * problems when transferred from the external hibernate process.
 */
public class ObjectSubstituteFactory
{
   private ClassLoader _cl;
   private ReflectionCaller _rc;

   public ObjectSubstituteFactory(ClassLoader cl)
   {
      _cl = cl;
      _rc = new ReflectionCaller();
   }


   public ArrayList<ObjectSubstituteRoot> replaceObjectsWithSubstitutes(Collection col, HashMap<String, MappedClassInfoData> infoDataByClassName)
   {
      try
      {
         HashMap<Object, ObjectSubstitute> doneObjs = new HashMap<Object, ObjectSubstitute>();

         if (null == col)
         {
            return null;
         }


         ArrayList<ObjectSubstituteRoot> ret = new ArrayList<ObjectSubstituteRoot>();
         for (Object o : col)
         {
            ObjectSubstituteRoot buf;

            if(o instanceof Object[])
            {
               ArrayList<ObjectSubstitute> arrBuf = new ArrayList<ObjectSubstitute>();
               ArrayList<Object> plainValueArrBuf = new ArrayList<Object>();
               Object[] arr = (Object[]) o;
               for (Object entry : arr)
               {
                  if(isMappedObject(entry, infoDataByClassName))
                  {
                     arrBuf.add(_prepareObjectSubstitute(entry, infoDataByClassName, null ,doneObjs));
                  }
                  else
                  {
                     plainValueArrBuf.add(entry);
                  }
               }
               
               if(0 < plainValueArrBuf.size())
               {
                  if (0 < arrBuf.size())
                  {
                     // result row is a mixture of plain values and mapped objects
                     arrBuf.add(new ObjectSubstitute(_cl, plainValueArrBuf));
                     buf = new ObjectSubstituteRoot(arrBuf);
                  }
                  else
                  {
                     // result row consists only of  plain values
                     buf = new ObjectSubstituteRoot(new ObjectSubstitute(_cl, plainValueArrBuf));
                  }
               }
               else
               {
                  // result row consists only of mapped objects
                  buf = new ObjectSubstituteRoot(arrBuf);
               }
            }
            else
            {
               if(isMappedObject(o, infoDataByClassName))
               {
                  buf = new ObjectSubstituteRoot(_prepareObjectSubstitute(o, infoDataByClassName, null,doneObjs));
               }
               else
               {
                  ArrayList<Object> plainValueArrBuf = new ArrayList<Object>();
                  plainValueArrBuf.add(o);
                  buf = new ObjectSubstituteRoot(new ObjectSubstitute(_cl, plainValueArrBuf));
               }
            }

            ret.add(buf);
         }
         return ret;
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }

   }

   private boolean isMappedObject(Object entry, HashMap<String, MappedClassInfoData> infoDataByClassName)
   {
      return null != findMappedClassInfoData(entry, infoDataByClassName, null);
   }

   private ArrayList<ObjectSubstitute> _prepareObjectSubstitutesForCollection(Collection col, HashMap<String, MappedClassInfoData> infoDataByClassName, HashMap<Object, ObjectSubstitute> doneObjs)
   {
      try
      {

         ArrayList<ObjectSubstitute> ret = new ArrayList<>();
         if (isInitialized(col))
         {
            for (Object o : col)
            {
               ret.add(_prepareObjectSubstitute(o, infoDataByClassName, null, doneObjs));
            }
         }
         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private ObjectSubstitute _prepareObjectSubstitute(Object o, HashMap<String, MappedClassInfoData> infoDataByClassName, MappedClassInfoData infoDataFromProperty, HashMap<Object, ObjectSubstitute> doneObjs) throws IllegalAccessException
   {
      if (null == o)
      {
         return null;
      }

      ObjectSubstitute ret = doneObjs.get(o);
      if (null != ret)
      {
         return ret;
      }

      MappedClassInfoData mappedClassInfoData = findMappedClassInfoData(o, infoDataByClassName, infoDataFromProperty);

      ret = new ObjectSubstitute(mappedClassInfoData, _toString(o));
      doneObjs.put(o, ret);

      PropertyAccessor pkAccessor = PropertyAccessor.createAccessor(o.getClass(), mappedClassInfoData.getIndentifierHibernatePropertyInfo().getPropertyName());
      PropertySubstitute pkPropertySubstitute = new PropertySubstitute(mappedClassInfoData.getIndentifierHibernatePropertyInfo(), pkAccessor.get(o), true);
      ret.putSubstituteValueByPropertyName(mappedClassInfoData.getIndentifierHibernatePropertyInfo().getPropertyName(), pkPropertySubstitute);



      for (HibernatePropertyInfo hibernatePropertyInfo : mappedClassInfoData.getHibernatePropertyInfos())
      {
         PropertyAccessor accessor = PropertyAccessor.createAccessor(o.getClass(), hibernatePropertyInfo.getPropertyName());

         if( null == hibernatePropertyInfo.getCollectionClassName() )
         {

            MappedClassInfoData propMappedClassInfoData = infoDataByClassName.get(hibernatePropertyInfo.getClassName());
            if(null == propMappedClassInfoData)
            {
               PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, accessor.get(o), true);
               ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
            }
            else
            {
               if(isInitialized(accessor.get(o)))
               {
                  ObjectSubstitute objectSubstitute = _prepareObjectSubstitute(accessor.get(o), infoDataByClassName, propMappedClassInfoData,doneObjs);
                  PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, objectSubstitute, true);
                  ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
               }
               else
               {
                  PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, (ObjectSubstitute)null, false);
                  ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
               }
            }
         }
         else
         {
            ArrayList<ObjectSubstitute> objectSubstituteCollection = new ArrayList<>();

            if( accessor.get(o) instanceof Collection )
            {
               Collection col = (Collection) accessor.get(o);
               if (isInitialized(col))
               {
                  if(isPersistentBasicTypeCollection(col, infoDataByClassName))
                  {
                     objectSubstituteCollection = new ArrayList<>();

                     // new ArrayList<>(col) is needed to move the entries of col
                     // from a Hibernate persistent collection into a Java standard ArrayList
                     objectSubstituteCollection.add(new ObjectSubstitute(_cl, new ArrayList<>(col)));
                  }
                  else
                  {
                     objectSubstituteCollection = _prepareObjectSubstitutesForCollection(col, infoDataByClassName, doneObjs);
                  }
               }

               PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, objectSubstituteCollection, isInitialized(col));
               ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
            }
            else
            {
               // Dummy PropertySubstitute
               PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo,
                                                                     "<noInstanceOf_java.util.Collection_notLoaded>",
                                                                     false);

               ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
            }
         }
      }

      return ret;
   }

   private boolean isPersistentBasicTypeCollection(Collection collectionToCheck, HashMap<String, MappedClassInfoData> infoDataByClassName)
   {
      if(null == collectionToCheck || collectionToCheck.isEmpty())
      {
         return false;
      }

      MappedClassInfoData mappedClassInfoData =
            findMappedClassInfoData(collectionToCheck.stream().findAny().get(), infoDataByClassName, null);

      return null == mappedClassInfoData;
   }


   private String _toString(Object o)
   {
      String ret = "<unknownError>";

      try
      {
         ret = o.toString();
      }
      catch(Throwable t)
      {
         try
         {
            ret = t.toString();
         }
         catch(Exception e)
         {
            // Remains "<unknownError>"
         }
      }

      return ret;
   }

   private MappedClassInfoData findMappedClassInfoData(Object o, HashMap<String, MappedClassInfoData> infoDataByClassName, MappedClassInfoData infoDataFromProperty)
   {
      MappedClassInfoData mappedClassInfoData = null;

      if (null != infoDataFromProperty)
      {
         mappedClassInfoData = infoDataFromProperty;
      }
      else if(null != o)
      {
         mappedClassInfoData = infoDataByClassName.get(o.getClass().getName());

         if(null == mappedClassInfoData)
         {
            int javaAssisNamePartBegin = o.getClass().getName().indexOf("_$$_");
            if (0 < javaAssisNamePartBegin)
            {
               String className = o.getClass().getName().substring(0, javaAssisNamePartBegin);
               mappedClassInfoData = infoDataByClassName.get(className);
            }
         }
      }

//      if(null == mappedClassInfoData)
//      {
//         throw new IllegalStateException("Could not find mapping infos for class: " + o.getClass().getName());
//      }

      return mappedClassInfoData;
   }

   private Boolean isInitialized(Object obj)
   {
      return HibernateServerUtil.isInitialized(_cl, _rc, obj);
   }
}
