package rero.util;

import java.io.*;
import java.util.*;

public class TimerUtil implements Runnable
{
   protected long resolution = 15 * 1000; // 15 second resolution by default... good enough for government work eh

//   protected List timers = Collections.synchronizedList(new LinkedList());
   protected List timers = new LinkedList();

   public void setResolution(long _resolution)
   {
      if (_resolution == 0 && resolution > 1000)
      {
         resolution = 1000;
      }
      else
      {
         resolution = _resolution;
      }
   }
   
   public void start()
   {
      Thread timerThread = new Thread(this);
      timerThread.setName("jIRCii TIMER Thread");
      timerThread.setDaemon(true); // we don't want the virtual machine staying alive on our account.
      timerThread.setPriority(Thread.MIN_PRIORITY); 
      timerThread.start();
   }

   public void run()
   {
      while (true)
      {
         try
         {
             List goodToGo = new LinkedList();

             /* clean up the internal timer list and find which timers are ready to execute */

             Iterator i = timers.iterator();
             while (i.hasNext())
             {
                TimedEvent temp = (TimedEvent)i.next();
                if (temp.isValid())
                {
                     if (temp.isReady())
                     { 
                        goodToGo.add(temp); 
                     }
                }
                else
                {
                     i.remove();
                }
             }

             /* execute the timers that are ready */

             i = goodToGo.iterator();
             while (i.hasNext())
             {
                TimedEvent temp = (TimedEvent)i.next();
                temp.timerExecute();
             }

             /* sleep for the prescribed resolution */

            Thread.sleep(resolution);
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }       
   }

   public void addTimer(TimerListener l, long delay, int repeat)
   {
      if (delay < resolution)
      {
         setResolution(delay);
      }
      addTimer(new TimedEvent(l, delay, repeat));
   }

   public void addTimer(TimerListener l, long delay)
   {
      if (delay < resolution)
      {
         setResolution(delay);
      }
      addTimer(new TimedEvent(l, delay));
   }

   public void stopTimer(TimerListener l)
   {
         Set removeMe = new HashSet();

         ListIterator i = timers.listIterator();
         while (i.hasNext())
         {
            TimedEvent temp = (TimedEvent)i.next();
            if (temp.getListener() == l)
            {
               temp.finish();
            }
         }
   }

   public void addTimer(TimedEvent temp)
   {
         timers.add(temp);       
   }
}
