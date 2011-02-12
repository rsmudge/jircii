package rero.gui.script;

import rero.gui.*;
import rero.gui.windows.*;
import rero.gui.input.*;
import rero.ircfw.*;

import sleep.engine.*; 
import sleep.runtime.*; 
import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;

import java.awt.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import java.util.*;

import text.*;
import text.list.*;

import rero.util.ClientUtils;

public class WindowOperators implements Predicate, Function, Loadable
{
   protected IRCSession session;

   public WindowOperators(IRCSession _session)
   {
      session = _session;
   }

   public void scriptLoaded(ScriptInstance script)
   {
      String[] contents = new String[] { 

          "&setWindowPrompt",
          "&getWindowPrompt",

          "&setWindowTitle",
          "&getWindowTitle",

          "&getWindowSize",

          "&renameWindow",

          "&getSelectedText",
          "&cutSelectedText",
          "&copySelectedText",
          "&pasteText",
          "&replaceSelectedText",

          "&setInputText",
          "&getInputText",

          "&getSelectedUsers",
          "&getSelectedUser",

          "&getClipboardText",
          "&setClipboardText",

          "&scrollWindow",

          "-iswindow",
          "-isspecial",
          "&refreshWindow"
      };

      for (int x = 0; x < contents.length; x++)
      {
         script.getScriptEnvironment().getEnvironment().put(contents[x], this);
      }       

      script.getScriptEnvironment().getEnvironment().put("&getCursorPosition", new getCursorPosition());
      script.getScriptEnvironment().getEnvironment().put("&setCursorPosition", new setCursorPosition());

      script.getScriptEnvironment().getEnvironment().put("&setButtonColor", new setButtonColor());
      script.getScriptEnvironment().getEnvironment().put("&getButtonColor", new getButtonColor());
   }

   private class getButtonColor implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String window = BridgeUtilities.getString(locals, "");
         if (session.getWindow(window) != null)
             return SleepUtils.getScalar(session.getWindow(window).getButton().getForeground().getRGB());

         return SleepUtils.getEmptyScalar();             
      }
   }

   private class setButtonColor implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String window = BridgeUtilities.getString(locals, "");
         if (session.getWindow(window) != null)
             session.getWindow(window).getButton().setForeground(Color.decode(locals.pop().toString()));

         return SleepUtils.getEmptyScalar();             
      }
   }

   private class getCursorPosition implements Function
   {
      public Scalar evaluate(String function, ScriptInstance script, Stack locals)
      {
         String window = BridgeUtilities.getString(locals, "");
         if (session.getWindow(window) != null && session.getWindow(window).isLegalWindow())
         {
            return SleepUtils.getScalar(session.getWindow(window).getInput().getCaretPosition());
         }

         return SleepUtils.getEmptyScalar();
      }
   }

   private class setCursorPosition implements Function
   {
      public Scalar evaluate(String function, ScriptInstance script, Stack locals)
      {
         String window = BridgeUtilities.getString(locals, "");
         if (session.getWindow(window) != null && session.getWindow(window).isLegalWindow())
         {
            session.getWindow(window).getInput().setCaretPosition(  BridgeUtilities.getInt(locals, 0)  );
         }

         return SleepUtils.getEmptyScalar();
      }
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   public Scalar evaluate(final String function, final ScriptInstance script, Stack locals)
   {
      if (function.equals("&getSelectedText"))
      {
         return SleepUtils.getScalar(session.getActiveWindow().getInput().getSelectedText());
      }
      else if (function.equals("&renameWindow") && locals.size() == 2)  
      {
         String a = locals.pop().toString();
         String b = locals.pop().toString();

         session.renameWindow(a, b);

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&scrollWindow") && locals.size() == 2)
      {
         String a = locals.pop().toString();
         int b    = BridgeUtilities.getInt(locals, 0);

         if (session.getWindow(a) != null && session.getWindow(a).isLegalWindow())
         {
             session.getWindow(a).getDisplay().scroll(b);
         }
      }
      else if (function.equals("&getWindowSize") && locals.size() == 1)
      {
         String window = locals.pop().toString();
         
         if (session.getWindow(window) != null)
         {
            return SleepUtils.getScalar(session.getWindow(window).getWidth());
         }

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&getWindowTitle") && locals.size() == 1)
      {
         String window = locals.pop().toString();
         
         if (session.getWindow(window) != null)
         {
            return SleepUtils.getScalar(session.getWindow(window).getTitle());
         }

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&getWindowTitle") && locals.size() == 0)
      {
         return SleepUtils.getScalar(SessionManager.getGlobalCapabilities().getFrame().getTitle());
      }
      else if (function.equals("&getWindowPrompt") && locals.size() == 1)
      {
         String window = locals.pop().toString();

         if (session.getWindow(window) != null)
         {
            return SleepUtils.getScalar(session.getWindow(window).getInput().getIndent());
         }
         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&getInputText") && locals.size() == 1)
      {
         String window = locals.pop().toString();
         if (session.getWindow(window) != null)
         {
            return SleepUtils.getScalar(session.getWindow(window).getInput().getText());
         }
      }
      else if (function.equals("&getSelectedUser") && locals.size() == 1)
      {
         String window = locals.pop().toString();
         if (session.getWindow(window) != null)
         {
            ListElement element = ((ChannelWindow)session.getWindow(window)).getListbox().getSelectedElement();
            if (element != null)
            {
               User user = (User)element.getSource();
               return SleepUtils.getScalar(user.getNick());
            }
         }

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&getSelectedUsers") && locals.size() == 1)
      {
         String window = locals.pop().toString();
         if (session.getWindow(window) != null && session.getWindow(window) instanceof ChannelWindow)
         {
            Set returnValue = new HashSet();

            Iterator list = ((ChannelWindow)session.getWindow(window)).getListbox().getSelectedElements().iterator();
            while (list.hasNext())
            {
               ListElement element = (ListElement)list.next();

               User user = (User)element.getSource();
               returnValue.add(user.getNick());
            }

            return SleepUtils.getArrayWrapper(returnValue);
         }

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&getClipboardText"))
      {
         Clipboard cb = null;

         if (Toolkit.getDefaultToolkit().getSystemSelection() != null)
         {
            cb = Toolkit.getDefaultToolkit().getSystemSelection();
         }
         else if (Toolkit.getDefaultToolkit().getSystemClipboard() != null)
         {
            cb = Toolkit.getDefaultToolkit().getSystemClipboard();
         }

         try
         {
            if (cb != null)
               return SleepUtils.getScalar(cb.getContents(this).getTransferData(DataFlavor.stringFlavor).toString());
         }
         catch (Exception ex) { ex.printStackTrace(); }

         return SleepUtils.getEmptyScalar();
      }
      else if (function.equals("&setClipboardText"))
      {
         String sel = BridgeUtilities.getString(locals, "");

         StringSelection selected = new StringSelection(sel);

         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selected, selected);

         if (Toolkit.getDefaultToolkit().getSystemSelection() != null)
         {
            Toolkit.getDefaultToolkit().getSystemSelection().setContents(selected, selected);
         }
      }
      else
      {
         final Stack tempLocals = new Stack();

         while (!locals.isEmpty())
         {
            tempLocals.push(locals.pop().toString());
         }

         ClientUtils.invokeLater(new Runnable()
         {
            public void run()
            {
               safeEvaluate(function, script, tempLocals);
            }
         });
      }

      return SleepUtils.getEmptyScalar();
   }
    
   public void safeEvaluate(String function, ScriptInstance script, Stack locals)
   {
      String text = "", window = "";

      if (locals.size() == 1)
      {
         text = locals.pop().toString();
      }
      else if (locals.size() == 2)
      {
         text   = locals.pop().toString();
         window = locals.pop().toString();
      }

      if (function.equals("&cutSelectedText"))
      {
         session.getActiveWindow().getInput().cut();
      }
      else if (function.equals("&copySelectedText"))
      {
         session.getActiveWindow().getInput().copy();
      }
      else if (function.equals("&pasteText"))
      {
         session.getActiveWindow().getInput().paste();
      }
      else if (function.equals("&replaceSelectedText"))
      {
         session.getActiveWindow().getInput().replaceSelection(text);
      }
      else if (function.equals("&setWindowTitle"))
      {
         if (window.length() > 0 && session.getWindow(window) != null)
         {
            session.getWindow(window).setTitle(text);
         }
         else
         {
            SessionManager.getGlobalCapabilities().getFrame().setTitle(text);
         }
      }
      else if (function.equals("&setWindowPrompt"))
      {
         if (session.getWindow(window) != null)
         {
            session.getWindow(window).getInput().setIndent(text);
         }
      }
      else if (function.equals("&refreshWindow"))
      {
         if (session.getWindow(text) != null)
         {
            session.getWindow(text).touch();
         }
      }
      else if (function.equals("&setInputText"))
      {
         if (session.getWindow(window) != null)
         {
            session.getWindow(window).getInput().setText(text);
         }
      }
   }

   public boolean decide(String predicate, ScriptInstance script, Stack terms)
   {
      if (terms.size() != 1)
      {
         return false;
      }

      String channel = ((Scalar)terms.pop()).getValue().toString();
   
      if (predicate.equals("-iswindow"))
      {
         return session.isWindow(channel);
      }

      if (predicate.equals("-isspecial"))
      {
         return !session.getWindow(channel).isLegalWindow();
      }

      return false;
   }
}
