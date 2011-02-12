package rero.util;

import javax.swing.SwingUtilities;

public class TimedEvent
{
   protected TimerListener listener;
   protected Runnable executeMe;
   protected long lastTouched;
   protected int  repeats;

   protected long waitTime;

   public TimedEvent(TimerListener _listener)
   {
       this(_listener, 60000, -1);
   }

   public TimedEvent(TimerListener _listener, long _waitTime)
   {
       this(_listener, _waitTime, -1);
   }

   public TimedEvent(TimerListener _listener, long _waitTime, int _repeats)
   {
       listener = _listener;
       lastTouched = System.currentTimeMillis();
       repeats = _repeats;
       waitTime = _waitTime;

       executeMe = new Runnable() {
           public void run()
           {
              listener.timerExecute();
           }
       };
   }

   public TimerListener getListener()
   {
       return listener;
   }

   public void finish()
   {
       repeats = 0;
   }

   public boolean isValid()
   {
      return (repeats != 0);
   }

   public boolean isReady()
   {
      return (System.currentTimeMillis() - lastTouched) >= waitTime;
   }

   public void timerExecute()
   {
      SwingUtilities.invokeLater(executeMe);

      lastTouched = System.currentTimeMillis();

      if (repeats > 0)
      {
         repeats--; 
      }
   }
}
