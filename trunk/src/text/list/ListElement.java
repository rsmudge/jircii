package text.list;

import text.*;

public class ListElement
{
   protected AttributedString data;
   protected boolean          selected = false;
   protected Object           source = null;   // source object

   public ListElement()
   {

   }

   public Object getSource()
   {
      return source;
   }

   public void setSource(Object o)
   {
      source = o;
   }

   public ListElement(String text)
   {
      setString(text);
   }

   public void setString(String text)
   {
      data = AttributedString.CreateAttributedString(text);
      data.assignWidths();
   }

   public void setSelected(boolean _selected)
   {
      selected = _selected;
   }

   public AttributedText getAttributedText()
   {
      return data.getAttributedText();
   }

   public String getText()
   {
      return data.getText();
   }

   public boolean isSelected()
   {
      return selected;
   }
}
