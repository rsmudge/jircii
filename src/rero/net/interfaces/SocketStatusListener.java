package rero.net.interfaces;

import rero.net.SocketEvent;

public interface SocketStatusListener
{
    public void socketStatusChanged(SocketEvent ev);
}
