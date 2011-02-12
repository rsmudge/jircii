package rero.ircfw;

import rero.net.SocketEvent;
import rero.net.interfaces.SocketDataListener;

import rero.ircfw.data.FwDataHandler;

import rero.ircfw.interfaces.FrameworkConstants;

import java.util.HashMap;

public class ProtocolHandler implements SocketDataListener, FrameworkConstants
{
   Parsed1459 rawProtocolParser  = new Parsed1459();    
   CTCPParser ctcpProtocolParser = new CTCPParser();
   FwDataHandler frameworkData   = new FwDataHandler();
   ProtocolDispatcher dispatcher = new ProtocolDispatcher();

   public void socketDataRead(SocketEvent ev)
   {
        handleProtocol(ev.message);
   }

   public InternalDataList getDataList()
   {
        return frameworkData.getDataList();
   }

   public ProtocolDispatcher getProtocolDispatcher()
   {
        return dispatcher;
   } 

   public void handleProtocol(String message)
   {
        // pass to parsed1459
        
        HashMap eventInfo = rawProtocolParser.parseString(message);
 
        // alter event as needed
       
        eventInfo = ctcpProtocolParser.parseEvent(eventInfo);

        // pass to data structure handler

        eventInfo = frameworkData.parseEvent(eventInfo);  

        // pass to event dispatcher 

        dispatcher.dispatchEvent(eventInfo);
   }
}
