package rero.config;

import java.awt.image.*;
import javax.swing.*;
import java.awt.*;

import java.io.*;
import java.net.URL;

import java.util.*;

import java.lang.ref.*;

public class ClientState
{
   protected static HashMap listeners = new HashMap();
   // ^-- container for the listeners for property changes.  This way
   // components won't be checking themselves all the time, they
   // can be notified when something happens.
   // listeners{"property.name"} = LinkedList ( WeakReference(listener1),
   //                                           WeakReference(null),
   //                                           WeakReference(listener2))
   // The weak references are for a server connection that gets closed,
   // this way those can be garbage collected.  Trying to make jIRC more
   // memory friendly.

   // related to backup and restore procedures
   protected Set        changes; // keeps track of changes made to this version of the "state"
   protected Properties backup;  // backup of the client state.


   protected static File    baseDirectory = new File(System.getProperty("user.home"), ".jIRC");

   protected Properties state;   // all of the properties we're going to load from the jerk.cfg file.

   public static void setBaseDirectory(String directory)
   {
      baseDirectory = new File(directory);
   }

   public void fireChange(String property)
   {
      fireChange(property, null);
   }

   public void addClientStateListener(String property, ClientStateListener l)
   {
      LinkedList temp = (LinkedList)listeners.get(property);
      if (temp == null)
      {
         temp = new LinkedList();
         listeners.put(property, temp);
      }

      temp.add(new WeakReference(l));
   }

   public void fireChange(String property, String parameter)
   {
      if (listeners.get(property) == null)
      {
         return; // ain't no thang sh'bang.
      }

      Iterator i = ((LinkedList)listeners.get(property)).iterator();
      while (i.hasNext())
      {
         WeakReference temp = (WeakReference)i.next();
         if (temp.get() == null)
         {
             i.remove();
         }
         else
         {
             ClientStateListener l = (ClientStateListener)temp.get();
             l.propertyChanged(property, parameter);
         }
      }
   }

   public static InputStreamReader getProperInputStream(InputStream stream)
   {
      if (ClientState.getClientState().getString("client.encoding", rero.dck.items.CharsetInput.DEFAULT_CHARSET).equals(rero.dck.items.CharsetInput.DEFAULT_CHARSET))
      {
         return new InputStreamReader(stream);
      }
      else
      {
         try
         {
            return new InputStreamReader(stream, ClientState.getClientState().getString("client.encoding", rero.dck.items.CharsetInput.DEFAULT_CHARSET));
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            return new InputStreamReader(stream);
         }
      }
   }

   public static PrintStream getProperPrintStream(OutputStream stream)
   {
      if (ClientState.getClientState().getString("client.encoding", rero.dck.items.CharsetInput.DEFAULT_CHARSET).equals(rero.dck.items.CharsetInput.DEFAULT_CHARSET))
      {
         return new PrintStream(stream, true);
      }
      else
      {
         try
         {
            return new PrintStream(stream, true, ClientState.getClientState().getString("client.encoding", rero.dck.items.CharsetInput.DEFAULT_CHARSET));
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            return new PrintStream(stream, true);
         }
      }
   }

   public static File getBaseDirectory()
   {
      if (!baseDirectory.exists() || !baseDirectory.isDirectory())
      {
         baseDirectory.delete();
         baseDirectory.mkdirs();
      }

      return baseDirectory;
   }

   protected static ClientState clientState = null;

   public static ClientState getClientState()
   {
      if (clientState == null)
      {
         clientState = new ClientState();
      }

      return clientState;
   }

   public ClientState()
   {
      state = new Properties();
      try
      {
          FileInputStream istream = new FileInputStream(new File(getBaseDirectory(), "jirc.prop"));
          state.load(istream);
          istream.close();
      }
      catch (Exception ex) { }

      changes = new HashSet();
   }

   /** performs a backup of the properties before making changes,  it is the responsability of the changing class to call this function */
   public void backup()
   {
      backup = (Properties)(state.clone());
      changes.clear();
   }

   /** performs a restore of the properties effectively undoing everything since the last backup, it is the responsability of the changing class to call this function */
   public void restore()
   {
      state = backup;
      sync();

      Iterator i = changes.iterator();

      while (i.hasNext())
      {
         String temp = (String)i.next();
         fireChange(temp);
      }
   }

   /** sync the file system config file with the current client state */
   public void sync()
   {
      try
      {

         FileOutputStream ostream = new FileOutputStream(new File(getBaseDirectory(), "jirc.prop"));
         state.save(ostream, "Java IRC Configuration");
         ostream.close();
      }
      catch (Exception ex) { ex.printStackTrace(); }
   }

   public Properties getProperties()
   {
      return state;
   }

   public void setString(String key, String value)
   {
      state.setProperty(key, value);
      fireChange(key);
   }

   public String getString(String key, String defaultValue)
   {
      String temp = state.getProperty(key);

      if (temp == null || temp.length() == 0)
      {
         return defaultValue;
      }

      return temp;
   }

   public Rectangle getBounds(String key, Dimension areaSize, Dimension mySize)
   {
      String temp = state.getProperty(key);

      if (temp == null)
      {
         int x = (int)(areaSize.getWidth() - mySize.getWidth()) / 2;
         int y = (int)(areaSize.getHeight() - mySize.getHeight()) / 2;

         if (x <= 0 || y <= 0)
         {
            x = 0;
            y = 0;
         }

         return new Rectangle(x, y, (int)mySize.getWidth(), (int)mySize.getHeight());
      }

      String[] values = temp.split("x");

      Rectangle tempr = new Rectangle(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
      return tempr;
   }

   public float getFloat(String key, float defaultValue)
   {
      String temp = state.getProperty(key);

      if (temp == null || temp.length() == 0)
      {
         return defaultValue;
      }

      try
      {
         return Float.parseFloat(temp);
      }
      catch (Exception ex)
      {
         return defaultValue;
      }
   }

   public void setFloat(String key, float value)
   {
      setString(key, value+"");
   }

   public int getInteger(String key, int defaultValue)
   {
      String temp = state.getProperty(key);

      if (temp == null || temp.length() == 0)
      {
         return defaultValue;
      }
      
      try
      {
         return Integer.parseInt(temp);
      }
      catch (Exception ex)
      {
         return defaultValue;
      }
   }

   public void setInteger(String key, int value)
   {
      setString(key, value+"");
   }

   public void setOption(String key, boolean value)
   {
      if (value)
      {
         setString(key, "true");
      }
      else
      {
         setString(key, "false");
      }
      fireChange(key);
   }

   public boolean isOption(String key, boolean defaultBoolean)
   {
      String temp = getString(key, null);

      if (temp == null)
      {
         return defaultBoolean;
      }

      if (temp.equals("true"))
      {
         return true;
      }

      return false;
   }

   public Color getColor(String key, Color defaultColor)
   {
      String temp = getString(key, null);

      if (temp == null)
      {
         return defaultColor;
      }

      return Color.decode(temp);
   }

   public void setColor(String key, Color color)
   {
/*      long value = 0;
      value = (color.getRed() << 16) | value;
      value = (color.getGreen() << 8) | value;
      value = (color.getBlue() << 0) | value; */

      setString(key, color.getRGB()+"");
   }

   public StringList getStringList(String key)
   {
      return new StringList(key);
   }

   public boolean isValue(String key, String item)
   {
      return getStringList(key).isValue(item);
   }

   public static File getFile(String filename)
   {
      return new File(getBaseDirectory(), filename);
   }

   public URL getResource(String fileName)
   {
      return getPackagedResource(fileName, "resource");
//      return this.getClass().getResource("/resource/"+fileName);
   }

   public String getHelpString(String topic)
   {
      topic = topic.replaceAll("\\'", "").replaceAll("\\?", "").replaceAll(" ", "_");

      try
      {
         URL url = getPackagedResource(topic, "help");

         if (url == null) { return null; }

         BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

         StringBuffer temp = new StringBuffer();

         String text;
         while ((text = in.readLine()) != null)
         {
            temp.append(text);
            temp.append("\n");
         }

         return temp.toString();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }

      return null;
   }

   public URL getPackagedResource(String fileName, String subDir)
   {
      try
      {
         File check = new File(getBaseDirectory(), fileName);
         if (check.exists())
         {
            return check.toURL();
         }
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }

      return this.getClass().getResource("/"+subDir+"/"+fileName);
   }

   public InputStream getResourceAsStream(String fileName)
   {
      try
      {
         File realf = new File(fileName);
         if (realf.exists())
         {
            return realf.toURL().openStream();
         }

         File check = new File(getBaseDirectory(), fileName);
         if (check.exists())
         {
            return check.toURL().openStream();
         }
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }

      URL temp = getResource(fileName);
      if (temp == null)
      {
         return null;
      }

      try
      {
         return temp.openStream();
      }
      catch (Exception ex) { ex.printStackTrace(); }

      return null;
   }

   public Font getFont(String key, Font defaultValue)
   {
      String fname = getString(key, null);

      if (fname == null)
      {
         return defaultValue;
      }

      return Font.decode(fname);
   }

   public void setFont(String key, Font value)
   {
      setString(key, rero.util.ClientUtils.encodeFont(value));
   }

   public ImageIcon getIcon(String key, String defaultResource)
   {
      String temp = getString(key, null);

      if (temp == null)
      {
         return new ImageIcon(getResource(defaultResource));
      }
      else
      {
         return new ImageIcon(Toolkit.getDefaultToolkit().getImage(temp));
      }
   }

   public void setBounds(String key, Rectangle value)
   {
      StringBuffer bounds = new StringBuffer();
      bounds.append((int)value.getX());
      bounds.append('x');
      bounds.append((int)value.getY());
      bounds.append('x');
      bounds.append((int)value.getWidth());
      bounds.append('x');
      bounds.append((int)value.getHeight());

      setString(key, bounds.toString());
   }

}

