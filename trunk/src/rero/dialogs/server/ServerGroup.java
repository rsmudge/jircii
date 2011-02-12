package rero.dialogs.server;

import java.util.*;

public class ServerGroup implements Comparable
{
   protected String    name;
   protected ArrayList servers;
   protected int       number;

   public ServerGroup(String n)
   {
      this(n, new ArrayList());
   }

   public void setNumber(int x)
   {
      number = x;
   }

   public int getNumber() { return number; }

   public ServerGroup(String n, ArrayList _servers)
   {
      name    = n;
      servers = _servers; 
   }

   public void clear() { servers.clear(); }

   public Server getServerByName(String name)
   {
      Iterator i = servers.iterator();
      while (i.hasNext())
      {
         Server temp = (Server)i.next();
         if (temp.getHost().equals(name))
         {
            return temp;
         }
      }

      return null;
   }

   public ArrayList getServers()
   {
      return servers;
   }

   public String getName()
   {
      return name;
   }

   public void addServer(Server server)
   {
      servers.add(server);
   }

   public void removeServer(Server server)
   {
      servers.remove(server);
   }

   public String toString()
   {
      return name;
   }

   public boolean isValid()
   {
      return !servers.isEmpty();
   }

   public int compareTo(Object o)
   {
      ServerGroup arg = (ServerGroup)o;

      return name.toUpperCase().compareTo(arg.getName().toUpperCase());
   }
}
