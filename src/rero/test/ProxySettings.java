package rero.test;

import rero.config.*;

import java.lang.ref.WeakReference;

public class ProxySettings implements ClientStateListener
{
    private static ProxySettings settings;

    public static void initialize()
    {
       settings = new ProxySettings();
    }

    public void propertyChanged(String key, String value)
    {
       setup();
    }

    public ProxySettings()
    {
       ClientState.getClientState().addClientStateListener("proxy.enabled", this);
       setup();
    }

    public void setup()
    {
       if (ClientState.getClientState().isOption("proxy.enabled", false))
       {
          System.setProperty("socksProxyHost", ClientState.getClientState().getString("proxy.server", ClientDefaults.proxy_server));
          System.setProperty("socksProxyPort", ClientState.getClientState().getString("proxy.port", ClientDefaults.proxy_port));
          System.setProperty("java.net.socks.username", ClientState.getClientState().getString("proxy.userid", ClientDefaults.proxy_userid));
          System.setProperty("java.net.socks.password", ClientState.getClientState().getString("proxy.password", ClientDefaults.proxy_password));
       }
       else 
       {
          System.setProperty("socksProxyHost", "");
          System.setProperty("socksProxyPort", "");
          System.setProperty("java.net.socks.username", "");
          System.setProperty("java.net.socks.password", "");
       }
    } 
}

