package rero.bridges.menu;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import rero.config.*;

public class MenuBridge implements Environment, Function, Loadable
{
    protected static String WINDOW_MENU = "&Window";
    protected static String HELP_MENU   = "&Help";

    //
    // Static code for keeping track of menu parents (truth is only one popup menu will be showing at once)
    //
    protected static Stack ParentMenu = new Stack();

    public static void SetParent(MenuBridgeParent m)
    {
        ParentMenu.push(m);
    }

    public static MenuBridgeParent GetParent()
    {
        return (MenuBridgeParent)ParentMenu.peek();
    }

    public static void FinishParent()
    {
        ParentMenu.pop();
    }

    protected LinkedHashMap menubarMenus;
    protected HashMap       menus;

    public MenuBridge()
    {
        menubarMenus = new LinkedHashMap(10, .75f, false); // initial capacity of 10 items, load factor of .75, and most 
                                                           // importantly access order (last param) of false meaning internal 
                                                           // order is based on when element was inserted not on access.

        menus = new HashMap();
    }

    /** returns true if the specified menu name is one of the top level menus we use in the client */
    protected static boolean isTopLevel(String name)
    {
        name = name.toLowerCase();

        if (name.length() > 2 && name.substring(0, 2).equals("__"))
              return true;

        return (name.equals("list") || name.equals("dcc") || name.equals("switchbar") || name.equals("nicklist") || name.equals("status") || name.equals("channel") || name.equals("query") || name.equals("input") || name.equals("chat") || name.equals("tab") || name.equals("background"));
    }

    protected static boolean isSpecialMenu(String name)
    {
        return (name.equals(WINDOW_MENU) || name.equals(HELP_MENU));
    }

    public void bindFunction(ScriptInstance si, String type, String description, Block code)
    {
       //
       // Setup "menubar" menus (i.e. the menus at the top of the application)
       //
       if (type.equals("menubar"))
       {
          if (menubarMenus.containsKey(description))
          {
             ((ScriptedMenu)menubarMenus.get(description)).installCode(si, code);
          } 
          else if (isSpecialMenu(description))
          {
             if (menus.containsKey(description))
             {
                ((ScriptedMenu)menus.get(description)).installCode(si, code);
             }
             else
             {
                menus.put(description, new ScriptedMenu(si, description, code));             
             }
          }
          else
          {
             menubarMenus.put(description, new ScriptedMenu(si, description, code));
          }
       }

       if (type.equals("menu"))
       {
          //
          // A top level menu is what we are in business for, consists of stuff like nicklist, status, channel, query etc.
          //
          if (isTopLevel(description.toUpperCase()))
          {
             if (menus.containsKey(description.toUpperCase()))
             {
                ((ScriptedPopupMenu)menus.get(description.toUpperCase())).installCode(si, code);
             }
             else
             {
                menus.put(description.toUpperCase(), new ScriptedPopupMenu(si, code));
             }
          }
          else
          {
             GetParent().add(new ScriptedMenu(si, description, code));
          }
       }
       
       if (type.equals("item"))
       {
          GetParent().add(new ScriptedItem(si, description, code));
       }
    }

    public Scalar evaluate(String function, ScriptInstance script, Stack locals)
    {
       if (function.equals("&addSeparator"))
       {
          GetParent().addSeparator();
       }
       else if (function.equals("&addItem") && locals.size() == 2)
       {
          String name = ((Scalar)locals.pop()).stringValue();
          String func = ((Scalar)locals.pop()).stringValue();

          GetParent().add(new SimpleItem(script, name, func));
       }
       else if (function.equals("&removeMenubarItem") && locals.size() == 1)
       {
          String name = ((Scalar)locals.pop()).stringValue();
          menus.remove(name);
       }

       return null;
    }

    public void scriptLoaded(ScriptInstance si)
    {
       Hashtable env = si.getScriptEnvironment().getEnvironment();
       env.put("item", this); 
       env.put("menu", this);
       env.put("menubar", this);

       env.put("&addItem", this);
       env.put("&addSeparator", this);
       env.put("&removeMenubarItem", this);
    }
 
    public JPopupMenu getPopupMenu(String description, HashMap data)
    {
       if (!menus.containsKey(description.toUpperCase()))
       {
          return null;
       }

       ScriptedPopupMenu.SetMenuData(data);

       JPopupMenu temp = (JPopupMenu)menus.get(description.toUpperCase());
       return temp;
    }

    public JPopupMenu getPrimaryPopup(String description)
    {
       if (!menubarMenus.containsKey(description))
       {
          return null;
       }

       ScriptedPopupMenu.SetMenuData(new HashMap());

       ScriptedMenu temp = (ScriptedMenu)menubarMenus.get(description);

       return temp.getScriptedPopupMenu();
    }

    public void installMenubar(JMenuBar bar)
    {
       Iterator i = menubarMenus.values().iterator();
       while (i.hasNext())
       {
          ScriptedMenu temp = (ScriptedMenu)i.next();
          if (temp.isValidCode())
          {
             bar.add(temp);
          }
          else
          {
             i.remove();
          }
       }

       if (menus.containsKey(WINDOW_MENU))
       {
          bar.add((JMenu)menus.get(WINDOW_MENU));
       }

       if (menus.containsKey(HELP_MENU))
       {
          bar.add((JMenu)menus.get(HELP_MENU));
       }
    }

    public void scriptUnloaded(ScriptInstance si)
    {
    }
}
