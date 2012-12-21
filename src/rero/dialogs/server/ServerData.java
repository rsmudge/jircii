package rero.dialogs.server;

import java.util.*;
import java.io.*;

import rero.util.*;
import rero.config.*;

import javax.swing.*;

public class ServerData
{
   protected static ServerData data = null;

   protected ServerGroup   allServers    = new ServerGroup("All Servers");

   protected TreeMap       groups     = new TreeMap();
   protected ArrayList     groupModel = new ArrayList();

   protected ServerGroup   active;

   protected ServerData()
   {
      load();
   }

   public Server getServerByName(String host)
   {
      return allServers.getServerByName(host);
   }

   public static ServerData getServerData()
   {
      if (data == null)
         data = new ServerData();

      return data;
   }

   public void setActive(ServerGroup a)
   {
      active = a;

      if (active == allServers || active == null)
      {
         buildGroupList();
         return;
      }

      active.clear();
      groups.clear();

      active = getGroup(active.getName()); // initializes active as a new group...

      Iterator i = allServers.getServers().iterator();
      while (i.hasNext())
      {
         Server temp = (Server)i.next();
         getGroup(temp.getNetwork()).addServer(temp);
      }

      buildGroupList();
   }   

   public void update()
   {
      setActive(active);
   }

   public ArrayList getGroups()
   {
       return groupModel;
   }

   public ArrayList getAllServers()
   {
       return allServers.getServers();
   }

   public ArrayList getServers()
   {
      if (active != null)
      {
         return active.getServers();
      }

      return allServers.getServers();
   }

   public ServerGroup getGroup(String name)
   {
      ServerGroup temp = (ServerGroup)groups.get(name.toUpperCase());

      if (temp == null)
      {
         temp = new ServerGroup(name);
         groups.put(name.toUpperCase(), temp);
      }

      return temp;
   }


   public void addServer(Server server)
   {
      getGroup(server.getNetwork()).addServer(server);
      allServers.addServer(server);
   }

   public void sort()
   {
      Collections.sort(allServers.getServers());
   }

   public void removeServer(Server server)
   {
      allServers.removeServer(server);
      getGroup(server.getNetwork()).removeServer(server);
   }

   public void buildGroupList()
   {
      groupModel.clear();
   
      groupModel.add(allServers);
      allServers.setNumber(0);

      int x = 1;

      Iterator i = groups.values().iterator();
      while (i.hasNext())
      {
         ServerGroup temp = (ServerGroup)i.next();
         if (temp.isValid())
         {
            groupModel.add(temp);
            temp.setNumber(x);
            x++;
         }
      }
   }

   public void load()
   {
      groups.clear();
      allServers    = new ServerGroup("All Servers");

      try
      {
         BufferedReader in = new BufferedReader(new InputStreamReader(ClientState.getClientState().getResourceAsStream("servers.ini")));

         String data = in.readLine();
         while (data != null)
         {
            Server temp = Server.decode(data);

            if (temp != null)
            {
               addServer(temp);
            }

            data = in.readLine();
         }
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }

      setActive(allServers);
      buildGroupList();
   }

   // Generate the servers.ini file
   public void save()
   {
      try
      {
         PrintWriter out = new PrintWriter(new FileOutputStream(ClientState.getClientState().getFile("servers.ini"), false));
      
         int x = 0;

         out.println("; Who thinks mIRC sucks?");
	 out.println("; I do! :D    - Brandon");
         out.println("[servers]");

         Iterator i = allServers.getServers().iterator();
         while (i.hasNext())
         {
            Server temp = (Server)i.next();
            out.println(temp.toString(x));
            x++;
         }

         out.flush();
         out.close();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }    
   }

   public static void main(String args[])
   {
      ServerData temp = new ServerData();
      temp.load();
   }
}
