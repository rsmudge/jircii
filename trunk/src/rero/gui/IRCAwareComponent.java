package rero.gui;

import rero.client.Capabilities;

public interface IRCAwareComponent
{
   /** install these capabilities and propagate them to any children IRCAwareComponents */
   public void installCapabilities(Capabilities c);
}
