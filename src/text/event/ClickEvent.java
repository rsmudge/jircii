package text.event;

import java.awt.event.MouseEvent;

public class ClickEvent
{
   protected boolean consumed = false;

   /** consumes (i.e. halt) the event */
   public void consume()
   {
      consumed = true;
   }

   /** says wether or not this event has been consumed */
   public boolean isConsumed()
   {
      return consumed;
   }

   public boolean isAcknowledged()
   {
      return ack;
   }

   public void acknowledge()
   {
      ack = true;
   }

   protected boolean ack;
   protected String context;
   protected String word;
   protected MouseEvent event;

   public MouseEvent getEvent()
   {
      return event;
   }

   public ClickEvent(String _word, String _context, MouseEvent _event)
   {
      context = _context;
      word    = _word;
      event   = _event;
   }

   /** returns the specific word/token that the user clicked on */
   public String getClickedText()
   {
      return word;
   }

   /** returns the entire line of text that contains the clicked word */
   public String getContext()
   {
      return context;
   }
}
