package rero.gui.input;

public class UserInputEvent
{
   public InputField source;
   public String     text;

   public boolean    consumed = false;

   public void consume() { consumed = true; }

   public boolean isConsumed() { return consumed; }

   public void reset() { consumed = false; }
}
