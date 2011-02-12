package rero.net.interfaces;

import rero.net.SocketEvent;

public interface SocketDataListener
{
    public void socketDataRead(SocketEvent ev);
}
