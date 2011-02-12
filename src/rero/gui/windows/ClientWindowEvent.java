package rero.gui.windows;

public class ClientWindowEvent
{
   protected ClientWindow source;

   public ClientWindowEvent(ClientWindow src)
   {
       source = src;
   }

   public ClientWindow getSource()
   {
       return source;
   }
}

