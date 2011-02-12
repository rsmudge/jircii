package rero.dcc;

import java.util.*;

public class DataDCC
{
   protected static List    dccList;
   protected static HashMap resumeData;

   public DataDCC()
   {
      if (dccList == null)
      {
         dccList = Collections.synchronizedList(new LinkedList());
      }

      if (resumeData == null)
      {
         resumeData = new HashMap();
      }
   }   
 
   /** used by the resume stuff */
   public void addConnection(String port, GenericDCC dcc)
   {
      dccList.add(dcc);
      resumeData.put(port, dcc);
   }

   public LinkedList getConnections(int type, int state)
   {
      return getConnections(dccList, type, state);
   }

   public LinkedList getConnections(Collection dccCollection, int type, int state)
   {
      LinkedList rv = new LinkedList();

      Iterator iter = dccCollection.iterator();
      while (iter.hasNext())
      {
          GenericDCC temp = (GenericDCC)iter.next();
          if ((type == -1 || temp.getTypeOfDCC() == type) && (state == -1 || temp.getState() == state))
              rv.add(temp);
      }

      return rv;
   }

   public List getAllConnections()
   {
      return dccList;
   }

   public GenericDCC getConnectionFromHash(String hash)
   {
      Iterator i = getAllConnections().iterator();
      while (i.hasNext())
      {
         GenericDCC temp = (GenericDCC)i.next();
         if (temp.getImplementation().toString().equals(hash))
           return temp;
      } 

      return null;
   }

   public Iterator getActiveConnections()
   {
      return getConnections(-1, ProtocolDCC.STATE_OPEN).iterator();
   }

   public Iterator getWaitingConnections()
   {
      return getConnections(-1, ProtocolDCC.STATE_WAIT).iterator();
   }

   public Iterator getClosedConnections()
   {
      return getConnections(-1, ProtocolDCC.STATE_CLOSED).iterator();
   }

   public GenericDCC getUserConnection(Collection dccCollection, String nickname)
   {
      Iterator i = dccCollection.iterator();
      while (i.hasNext())
      {
         GenericDCC temp = (GenericDCC)i.next();
         if (temp.getNickname().equals(nickname))
         {
            return temp;
         }
      }

      return null;
   }
 
   public GenericDCC getConnection(String port)
   {
      return (GenericDCC)resumeData.get(port);
   }

   public GenericDCC getConnectionToAccept(String nickname)
   {
      return getUserConnection(getConnections(-1, ProtocolDCC.STATE_WAIT), nickname);
   }

   public void closeConnection(String nickname)
   {
      closeConnection(nickname, -1);
   }

   public void closeConnection(String nickname, int type)
   {
      GenericDCC temp = getUserConnection(getConnections(type, ProtocolDCC.STATE_OPEN), nickname);
      if (temp != null)
         temp.getImplementation().close();
   }

   public void closeChat(String nickname)
   {
      ProtocolDCC temp = getSpecificConnection(nickname, ProtocolDCC.DCC_CHAT);
      if (temp != null)
         temp.close();
   }

   public void removeConnection(GenericDCC dcc)
   {
      dccList.remove(dcc.getImplementation());
      connectionCache.remove(dcc.getNickname());
   }

   protected HashMap connectionCache = new HashMap();

   public ProtocolDCC getSpecificConnection(String nickname, int type)
   {
      if (connectionCache.containsKey(nickname))
      {
         ProtocolDCC connection = (ProtocolDCC)connectionCache.get(nickname);

         if (connection.getState() == ProtocolDCC.STATE_OPEN && connection.getTypeOfDCC() == type)
         {
            return connection;
         }

         connectionCache.remove(nickname);
      }

      Iterator i = getActiveConnections();
      while (i.hasNext())
      {
         ProtocolDCC connection = ((GenericDCC)i.next()).getImplementation();
         if (connection.getTypeOfDCC() == type && connection.getNickname().equals(nickname))
         {
             connectionCache.put(nickname, connection);
             return connection;
         }
      }

      return null;
   }
}
