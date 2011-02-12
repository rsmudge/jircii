package rero.client.functions;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;

import rero.util.*;
import rero.client.*;
import rero.config.*;
import java.util.*;
import java.text.*;

import text.AttributedString;

import java.awt.*; // for colormap and font apis

import text.TextSource; // for colormap manipulation stuff

import java.io.*;
import java.net.*;

import javax.swing.*;

public class UtilOperators extends Feature implements Loadable
{
   public void init()
   {
      getCapabilities().getScriptCore().addBridge(this);
   }

   public void scriptLoaded(ScriptInstance script)
   {
      script.getScriptEnvironment().getEnvironment().put("&formatBytes",    new formatBytes());

      script.getScriptEnvironment().getEnvironment().put("&duration",     new duration());
      script.getScriptEnvironment().getEnvironment().put("&formatTime",     new formatTime());
      script.getScriptEnvironment().getEnvironment().put("&formatTime2",     new formatTime2());
      script.getScriptEnvironment().getEnvironment().put("&formatTime3",    new formatTime3());
      script.getScriptEnvironment().getEnvironment().put("&formatDecimal",  new formatDecimal());
      script.getScriptEnvironment().getEnvironment().put("&longip",         new longip());

      // time date functions
      script.getScriptEnvironment().getEnvironment().put("&ctime",          new ctime());
      script.getScriptEnvironment().getEnvironment().put("&timeDateStamp",  new timeDateStamp());
      script.getScriptEnvironment().getEnvironment().put("&timeStamp",      new timeStamp());

      script.getScriptEnvironment().getEnvironment().put("&showInputDialog", new showInputDialog());
      script.getScriptEnvironment().getEnvironment().put("&showFileDialog", new showChooserDialog(JFileChooser.FILES_ONLY));
      script.getScriptEnvironment().getEnvironment().put("&showDirectoryDialog", new showChooserDialog(JFileChooser.DIRECTORIES_ONLY));

      script.getScriptEnvironment().getEnvironment().put("&setMappedColor", new setMappedColor());
      script.getScriptEnvironment().getEnvironment().put("&getMappedColor", new getMappedColor());
      script.getScriptEnvironment().getEnvironment().put("&saveColorMap",   new saveColorMap());

      script.getScriptEnvironment().getEnvironment().put("&generateThemeScript",       new generateTheme());

      script.getScriptEnvironment().getEnvironment().put("&loadFont",       new loadFont());

      script.getScriptEnvironment().getEnvironment().put("&getScriptPath",       new getScriptPath());
      script.getScriptEnvironment().getEnvironment().put("&getScriptResource",       new getScriptResource());

      script.getScriptEnvironment().getEnvironment().put("&strip",       new strip());
      script.getScriptEnvironment().getEnvironment().put("&strwidth",    new strwidth());
      script.getScriptEnvironment().getEnvironment().put("&versionString",       new versionString());

      script.getScriptEnvironment().getEnvironment().put("&exit", new exit());
      script.getScriptEnvironment().getEnvironment().put("&use", new f2_use());

      script.getScriptEnvironment().getEnvironment().put("-ischannel", new isChannel());

      script.getScriptEnvironment().getEnvironment().put("&groupNicks", new groupUsers());
      script.getScriptEnvironment().getEnvironment().put("&fileCompleteAll", new fileCompleteAll());

      script.getScriptEnvironment().getEnvironment().put("&buildCP437String", new buildString());
   }

   private static class strwidth implements Function
   {
      public Scalar evaluate(String name, ScriptInstance script, Stack locals)
      {
          return SleepUtils.getScalar(TextSource.fontMetrics.stringWidth(BridgeUtilities.getString(locals, "")));
      }
   }
  
    private static HashMap bridges = new HashMap();

    private static class f2_use implements Function
    {
       public Scalar evaluate(String n, ScriptInstance si, Stack l)
       {
          File   parent;
          String className;

          if (l.size() == 2)
          {
             parent    = BridgeUtilities.getFile(l, si);
             className = BridgeUtilities.getString(l, "");
          }
          else
          {
             File a    = BridgeUtilities.getFile(l, si);
             parent    = a.getParentFile();
             className = a.getName();
          }

          Class bridge;

          try
          {
             if (parent != null)
             {
                URLClassLoader loader = new URLClassLoader(new URL[] { parent.toURL() });
                bridge = Class.forName(className, true, loader);
             }
             else
             {
                bridge = Class.forName(className);
             }

             Loadable temp;

             if (bridges.get(bridge.toString()) == null)
             {
                temp = (Loadable)bridge.newInstance();
                bridges.put(bridge.toString(), temp);
             }
             else
             {
                temp = (Loadable)bridges.get(bridge.toString());
              }

             temp.scriptLoaded(si);
          }
          catch (Exception ex)
          {
             si.getScriptEnvironment().flagError(ex.toString());
          }

          return SleepUtils.getEmptyScalar();
       }
    }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   private static class buildString implements Function
   {
      public Scalar evaluate(String name, ScriptInstance si, Stack locals)
      {
          String temp = BridgeUtilities.getString(locals, "");

          return SleepUtils.getScalar(ClientUtils.BuildCP437String(temp));
      }
   }

   private static class getScriptPath implements Function
   {
      public Scalar evaluate(String name, ScriptInstance si, Stack locals)
      {
         if (locals.size() < 1)
             return SleepUtils.getEmptyScalar();

         File path = _getScriptPath(locals.pop().toString());

         if (path == null)
             return SleepUtils.getEmptyScalar();

         return SleepUtils.getScalar(path.getAbsolutePath());
      }
   }

   private static File _getScriptPath(String script)
   {
      StringList temp = ClientState.getClientState().getStringList("script.files");
      Iterator i      = temp.getList().iterator();

      while (i.hasNext())
      {
         File f = new File(i.next().toString());

         if (f.getName().equals(script) || f.getAbsolutePath().equals(script))
         {
             return f.getParentFile();
         }
      }

      return null;
   }

   private static class getScriptResource implements Function
   {
      public Scalar evaluate(String name, ScriptInstance si, Stack locals)
      {
         if (locals.size() < 2)
             return SleepUtils.getEmptyScalar();

         File path = _getScriptPath(locals.pop().toString());

         while (!locals.isEmpty())
         {
            path = new File(path, locals.pop().toString());
         }

         return SleepUtils.getScalar(path.getAbsolutePath());
      }
   }

   private static class strip implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getScalar(ClientUtils.strip(BridgeUtilities.getString(locals, "")));
      }
   }

   private static class generateTheme implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         if (locals.size() >= 1)
         {
            ClientUtils.generateThemeScript(locals.pop().toString());
         }
         else
         {
            ClientUtils.generateThemeScript(null);
         }

         return SleepUtils.getEmptyScalar();
      }
   }

   private static class versionString implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getScalar(ClientState.getClientState().getString("version.string", ClientDefaults.version_string));
      }
   }

   private static class loadFont implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String a = locals.pop().toString();

         try
         {
             Font font = Font.createFont(Font.TRUETYPE_FONT, ClientState.getClientState().getResourceAsStream(a));
             return SleepUtils.getScalar(ClientUtils.encodeFont(font));
         }
         catch (Exception ex) { si.getScriptEnvironment().flagError(ex.getMessage()); }

         return SleepUtils.getEmptyScalar();
      }
   }

   private static class formatBytes implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long a = BridgeUtilities.getLong(locals);
         return SleepUtils.getScalar(ClientUtils.formatBytes(a));
      }
   }

   private static class formatTime implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long a = BridgeUtilities.getLong(locals);
         return SleepUtils.getScalar(ClientUtils.formatTime(a));
      }
   }

   private static class formatTime2 implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long a = BridgeUtilities.getLong(locals);
         return SleepUtils.getScalar(ClientUtils.formatTime2(a));
      }
   }

    private static class formatTime3 implements Function
    {
        public Scalar evaluate(String f, ScriptInstance si, Stack locals)
        {
            long a = BridgeUtilities.getLong(locals);
            return SleepUtils.getScalar(ClientUtils.formatTime3(a));
        }
    }

   private static class timeDateStamp implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long a = BridgeUtilities.getLong(locals);
         return SleepUtils.getScalar(ClientUtils.TimeDateStamp(a));
      }
   }

   private static class longip implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String a = locals.pop().toString();
         return SleepUtils.getScalar(ClientUtils.longip(a));
      }
   }

   private class exit implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         getCapabilities().getGlobalCapabilities().QuitClient();
         return null;
      }
   }

   private class showInputDialog implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String message   = "Your answer?";

         if (!locals.isEmpty())
            message = locals.pop().toString();

         String title   = "Input Requested";

         if (!locals.isEmpty())
             title = locals.pop().toString();

         String a = JOptionPane.showInputDialog(getCapabilities().getGlobalCapabilities().getFrame(), message, title, JOptionPane.QUESTION_MESSAGE);

         if (a == null)
             return SleepUtils.getEmptyScalar();

         return SleepUtils.getScalar(a);
      }
   }

   private class showChooserDialog implements Function
   {
      private int          dirChooser;
      private JFileChooser chooser = null;

      public showChooserDialog(int dir) { dirChooser = dir; }

      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         if (chooser == null)
            chooser = new JFileChooser();

         chooser.setFileSelectionMode(dirChooser);

         String title = "Select File";

         if (!locals.isEmpty())
            title = locals.pop().toString();

         chooser.setDialogTitle(title);

         if (!locals.isEmpty())
            chooser.setCurrentDirectory(new File(locals.pop().toString()));

         if (!locals.isEmpty())
            chooser.setApproveButtonText(locals.pop().toString());

         int returnVal = chooser.showOpenDialog(getCapabilities().getGlobalCapabilities().getFrame());

         if (returnVal == JFileChooser.APPROVE_OPTION)
             return SleepUtils.getScalar(chooser.getSelectedFile().getAbsolutePath());

         return SleepUtils.getEmptyScalar();
      }
   }

   private static class ctime implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getScalar(ClientUtils.ctime());
      }
   }

   private static class formatDecimal implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long temp = BridgeUtilities.getLong(locals);
         return SleepUtils.getScalar(ClientUtils.formatLongAsDecimal(temp));
      }
   }

   private static class timeStamp implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getScalar(ClientUtils.TimeStamp());
      }
   }

   private static class setMappedColor implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         int    a = BridgeUtilities.getInt(locals);
         String b = locals.pop().toString();

         if (a < TextSource.colorTable.length && a >= 0)
            TextSource.colorTable[a] = Color.decode(b);

         return SleepUtils.getEmptyScalar();
      }
   }

   private static class getMappedColor implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         int    a = BridgeUtilities.getInt(locals);

         if (a < TextSource.colorTable.length && a > 0)
            return SleepUtils.getScalar(TextSource.colorTable[a].getRGB());

         return SleepUtils.getEmptyScalar();
      }
   }

   private static class saveColorMap implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         TextSource.saveColorMap();
         return SleepUtils.getEmptyScalar();
      }
   }

   private static class fileCompleteAll implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         Scalar value = SleepUtils.getArrayScalar();
         Iterator i = ClientUtils.fileCompleteAll(BridgeUtilities.getString(locals, "")).iterator();
         while (i.hasNext())
         {
            value.getArray().push(SleepUtils.getScalar(i.next().toString()));
         }

         return value;
      }
   }

   private static class groupUsers implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         ScalarArray users = BridgeUtilities.getArray(locals);
         int         count = BridgeUtilities.getInt(locals, 4);
         Scalar      value  = SleepUtils.getArrayScalar();

         StringBuffer rv = new StringBuffer();

         int x = 1;
         Iterator i = users.scalarIterator();
         while (i.hasNext())
         {
            String temp = i.next().toString();
            rv.append(temp);
            if ((x % count) == 0 && x > 1) { rv.append("="); }
            else { rv.append(","); }

            x++;
         }

         if (rv.toString().length() > 1)
         {
            String groups[] = rv.toString().substring(0, rv.toString().length() - 1).split("=");

            for (x = 0; x < groups.length; x++)
            {
               value.getArray().push(SleepUtils.getScalar(groups[x]));
            }
         }

         return value;
      }
   }

   private static class isChannel implements Predicate
   {
      public boolean decide(String pred, ScriptInstance si, Stack locals)
      {
         return ClientUtils.isChannel(BridgeUtilities.getString(locals, " "));
      }
   }

  private static class duration implements Function
  {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long seconds = BridgeUtilities.getLong(locals, 0L);
         StringBuffer rv = new StringBuffer();

         long minutes = 0, hours = 0, days = 0;

         days     = seconds / (60 * 60 * 24);
         hours    = seconds / (60 * 60) % 24;
         minutes  = (seconds / 60) % 60;
         seconds  = seconds % 60;

         if (days > 0) {
             rv.append(days);

             if (days == 1) { rv.append("day, "); }
             else { rv.append("days, "); }
         }

         if (hours > 0) {
             rv.append(hours);

             if (hours == 1) { rv.append("hour, "); }
             else { rv.append("hours, "); }
         }

         if (minutes > 0) {
             rv.append(minutes);

             if (minutes == 1) { rv.append("minute, "); }
             else { rv.append("minutes, "); }
         }

         rv.append(seconds);

         if (seconds == 1) { rv.append("second"); }
         else { rv.append("seconds"); }

         return SleepUtils.getScalar(rv.toString());
      }
   }
}
