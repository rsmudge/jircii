package rero.config;

public interface ClientStateListener
{
   /** when the property gets changed you will be notified! */
   public void propertyChanged(String property, String parameter);
}
