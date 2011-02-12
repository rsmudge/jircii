package rero.net;

public class SocketEvent
{
   public String            message;
   public SocketConnection  socket;
   public SocketInformation data;
   public boolean           valid = true; // flag to say message should continue to be processed...
}
