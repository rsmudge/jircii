package rero.gui.windows;

public interface ClientWindowListener
{
   public void onOpen(ClientWindowEvent ev);
   public void onClose(ClientWindowEvent ev);
   public void onActive(ClientWindowEvent ev);
   public void onInactive(ClientWindowEvent ev);
   public void onMinimize(ClientWindowEvent ev);
}
