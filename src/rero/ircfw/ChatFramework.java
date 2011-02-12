/* 
       .;:: Chat Framework ::;. 
 
    Exports Structures, capabilities, and event wiring for the socket 
    subsystem.  Fun, eh?

*/                   

package rero.ircfw;

import java.util.WeakHashMap;
import rero.ircfw.interfaces.ChatListener;

public class ChatFramework
{
   protected ProtocolHandler  protocol;

   public ChatFramework()
   {
      protocol = new ProtocolHandler();
   }
   
   public ProtocolHandler getProtocolHandler()
   {
      return protocol;
   }

   public ProtocolDispatcher getProtocolDispatcher()
   {
      return protocol.getProtocolDispatcher();
   }

   // === Export Data Structures ========================================================================================

   public void storeDataStructures(WeakHashMap centralDataRepository)
   {
      centralDataRepository.put("clientInformation", protocol.getDataList());
   }

   // === Export Capabilities ============================================================================================

         // (note: capabilities are exported in this manner solely to
         //  facilitate a quick and easy way to access them.  For stable
         //  API's capability proxies should just access the reference
         //  with the API directly to avoid the extra function call overhead).


   /** Injects a string into the data stream as if it came from the server.  parsed in the same 
     manner.  format should be :nick!user@host EVENT_NAME target :parameters just like the irc protocol.
     Since jIRC handles everything in a generic way, client events will be more uniform with this system */

   public void injectEvent(String data)
   {
       getProtocolHandler().handleProtocol(data);
   }


   // === Export Events ==================================================================================================

   public void addTemporaryListener(ChatListener l)
   { 
       getProtocolHandler().getProtocolDispatcher().addTemporaryListener(l);
   }

   public void addChatListener(ChatListener l)
   {
       getProtocolHandler().getProtocolDispatcher().addChatListener(l);
   }
}
