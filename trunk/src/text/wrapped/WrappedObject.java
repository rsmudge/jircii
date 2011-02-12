package text.wrapped;

import java.awt.*;

public class WrappedObject
{
   public String text;
   public Object returnValue;

   public WrappedObject(String _text, Object _returnValue)
   {
      text = _text;
      returnValue = _returnValue;
   } 

   public Object getObject()
   {
      return returnValue;
   }

   public String getText()
   {
      return text;
   }
}
