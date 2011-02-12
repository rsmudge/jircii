package rero.client;

import java.util.*;

public abstract class Feature
{
   protected Capabilities abilities;

   public void installCapabilities(Capabilities c)
   {
       abilities = c;
   }

   public Capabilities getCapabilities()
   {
       return abilities;
   }

   public void storeDataStructures(WeakHashMap data)
   {
       // does nothing by default.
   }

   public abstract void init();

   /** execute any cleanup that needs to be done so we don't have memory leaks */
   public void cleanup()
   {
   }
} 
