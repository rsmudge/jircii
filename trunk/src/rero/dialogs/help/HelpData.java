package rero.dialogs.help;

import java.io.*;
import rero.config.*;
import java.util.*;

import rero.util.*;
import java.net.*;

public class HelpData
{
   protected LinkedList aliases = new LinkedList();
   protected HashMap    data    = new HashMap();

   public Object[] getData()
   {
      return aliases.toArray();
   }

   public LinkedList getAliases()
   {
      return aliases;
   } 

   public String getCommand(String command)
   {
      return ((HelpCommand)data.get(command)).toString(); 
   }

   public boolean isCommand(String command)
   {
      return data.containsKey(command);
   }

   public HelpData()
   {
      URL url = ClientState.getClientState().getPackagedResource("aliases", "help");
    
      if (url != null)
      {
         try
         {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String text;
            while ((text = in.readLine()) != null)
            {
               String[] temp = text.split("\\:\\:");
               aliases.add(temp[0]);
               data.put(temp[0], new HelpCommand(temp));
            }
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }          

   }
}
