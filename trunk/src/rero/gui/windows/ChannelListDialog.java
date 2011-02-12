package rero.gui.windows;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import rero.util.*;

import contrib.javapro.*;  // sorted JTable code...

import rero.ircfw.interfaces.*;

import rero.config.*;
import rero.client.*;

import rero.gui.*;
import rero.gui.windows.*;
import rero.gui.toolkit.*;

import text.*;

public class ChannelListDialog extends GeneralListDialog implements ActionListener
{
   protected JTextField        search;
   protected JLabel            label;
   protected JSortTable        table;

   public void actionPerformed(ActionEvent ev)
   {
      ((ChannelTableModel)model).search(search.getText());
   }

   public void init()  // overwriting the init in the other one...
   {
      capabilities.addTemporaryListener((ChannelTableModel)model);
   }

   public void processMouseEvent (MouseEvent ev, int row)
   {
      ChannelTableModel temp = (ChannelTableModel)model;
      SessionManager.getGlobalCapabilities().getActiveSession().executeCommand("/JOIN " + ((LChannel)temp.getData().get(row)).getChannel());
   }

   public ChannelListDialog()
   {
      super("Channel List", "list", new ChannelTableModel());

      JPanel top = new JPanel();
      top.setLayout(new BorderLayout());
      top.setOpaque(true);

      JPanel righttop = new JPanel();
      righttop.setLayout(new FlowLayout(FlowLayout.RIGHT));
      righttop.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

      search = new JTextField();
      search.setColumns(12);
      search.addActionListener(this);

      JButton    doit   = new JButton("Search");
      doit.addActionListener(this);

      righttop.add(search);
      righttop.add(doit);

      top.add(righttop, BorderLayout.EAST);

      label = new JLabel("Waiting for the storm...");

      ((ChannelTableModel)model).setLabel(label);

      top.add(label, BorderLayout.CENTER);
      
      add(top, BorderLayout.NORTH);
   }

   private static class LChannel
   {
      private String name, topic;
      private int    users;

      public LChannel(String _name, int _users, String _topic)
      {
         name  = _name;
         users = _users;
         topic = _topic;

         if (topic.equals(":"))
         {
             topic = "";
         }
      }

      public String getChannel()
      {
         return name;
      }

      public String getTopic()
      {
         return topic;
      }

      private AttributedString topicattrs, nameattrs, userattrs;

      public AttributedString getTopicAttributed()
      {
         if (topicattrs != null)
           return topicattrs;

         topicattrs = AttributedString.CreateAttributedString(topic);
         topicattrs.assignWidths();

         return topicattrs;
      }

      public AttributedString getChannelAttributed()
      {
         if (nameattrs != null)
           return nameattrs;

         nameattrs = AttributedString.CreateAttributedString(name);
         nameattrs.assignWidths();

         return nameattrs;
      }

      public AttributedString getUsersAttributed()
      {
         if (userattrs != null)
           return userattrs;

         userattrs = AttributedString.CreateAttributedString(users + "");
         userattrs.assignWidths();

         return userattrs;
      }

      public int getUsers()
      {
         return users;
      }

      public boolean matches(String criteria)
      {
         if (criteria.indexOf('*') > -1 || criteria.indexOf('?') > -1)
            return StringUtils.iswm(criteria.toUpperCase(), (getChannel() + " " + StringUtils.strip(getTopic())).toUpperCase());

         return (getChannel() + " " + StringUtils.strip(getTopic())).toUpperCase().indexOf(criteria.toUpperCase()) > -1;
      }
   }
 
   private static class ChannelComparator implements Comparator
   {
      private int     criteria;
      private boolean reverse;

      public ChannelComparator(int _criteria, boolean _reverse)
      {
         criteria = _criteria;
         reverse  = _reverse;
      }

      public int compare(Object one, Object two)
      {
         LChannel a = (LChannel)one;
         LChannel b = (LChannel)two;

         if (reverse)
         {
            LChannel c = b;
            b = a;
            a = c;      // swap the values we're comparing if we're sorting in ascending mode...
         }

         switch (criteria)
         {
            case 0:
              return a.getChannel().compareTo(b.getChannel());

            case 1:
              if (a.getUsers() < b.getUsers())
                 return 1;

              if (a.getUsers() > b.getUsers())
                 return -1;

              return 0;

            case 2:
              return a.getTopic().compareTo(b.getTopic());
         }

         return 0;
      }
   }
 
   private static class ChannelTableModel extends GeneralListModel implements ChatListener, FrameworkConstants
   {
      protected ArrayList filter   = null;
      protected ArrayList channels;
      protected int        x = 0;
      protected JLabel  label;

      public HashMap getEventHashMap (int row)
      {  
         LChannel channel = (LChannel)getData().get(row);
         return ClientUtils.getEventHashMap(channel.getChannel(), channel.getUsers() + " " + channel.getTopic());
      }

      public void setLabel(JLabel l) { label = l; }

      public boolean isChatEvent(String id, HashMap data)
      {
         return (id.equals("321") || id.equals("322") || id.equals("323"));
      }

      public void search(String criteria)
      {
         if (criteria.equals(""))
         {
            filter = null;
            fireTableDataChanged();
            return;
         }

         filter = new ArrayList();
         Iterator i = channels.iterator();
         while (i.hasNext())
         {
            LChannel temp = (LChannel)i.next();
            if (temp.matches(criteria))
            {
               filter.add(temp);
            }
         }

         fireTableDataChanged();
      }
 
      private ArrayList getData()
      {
         if (filter != null)
            return filter;

         return channels;
      }

      public int fireChatEvent(HashMap data)
      {
         String event = (String)data.get($EVENT$);

         if (event.equals("321"))
         {
            label.setText("... Listing Channels ...");

            channels.clear();
            filter = null;

            fireTableDataChanged();
         }

         if (event.equals("322"))
         {
            TokenizedString text = new TokenizedString(data.get($PARMS$).toString());
            text.tokenize(" ");

            channels.add(new LChannel(text.getToken(0), Integer.parseInt(text.getToken(1)), text.getTokenFrom(2)));

            if ((x % 1000) == 0)
            {
               fireTableDataChanged();
            }

            if (x > 10000 && x < 11000)
            { 
               label.setText("Go grab a beer, this is gonna be awhile...");
            }
            else if (x > 20000 && x < 21000)
            { 
               label.setText("I bet we're not even halfway there yet");
            }
            else if (x > 25000 && x < 26000)
            { 
               label.setText("So what do you do for a living?");
            }
            else if (x > 26000 && x < 26500)
            { 
               label.setText("Really, does that make you gay?");
            }
            else if (x > 26500 && x < 27500)
            { 
               label.setText("Don't worry, I don't judge");
            }
            else if (x > 27500 && x < 28500)
            { 
               label.setText("I'm just an irc client... or am I?");
            }
            else if (x > 35000 && x < 36000)
            { 
               label.setText("I love you.  You love me.  We're a happy...");
            }
            else if (x > 36000 && x < 36500)
            { 
               label.setText("... family?");
            }
            else if (x > 43000 && x < 44000)
            { 
               label.setText("How many freaking channels are on this network?");
            }
            else if ((x % 175) == 0)
            {
               label.setText(".x. Listing Channels .x.: " + channels.size());           
            }
            else if ((x % 175) == 43)
            {
               label.setText("..x Listing Channels ..x: " + channels.size());           
            }
            else if ((x % 175) == 81)
            {
               label.setText(".x. Listing Channels .x.: " + channels.size());           
            }
            else if ((x % 175) == 127)
            {
               label.setText("x.. Listing Channels x..: " + channels.size());           
            }

            x++;
         }

         if (event.equals("323"))
         {
            label.setText("Channel /list complete: " + channels.size() + " channels");

            x = 0;
            fireTableDataChanged();
            return REMOVE_LISTENER | EVENT_DONE;
         }


         return EVENT_HALT;
      }

      public ChannelTableModel()
      {
         channels = new ArrayList(10000);
      }

      public void sortColumn(int col, boolean ascending) 
      {
         Collections.sort(getData(), new ChannelComparator(col, ascending));
         fireTableDataChanged();
      }

      public int getRowCount()
      {
         return getData().size();
      }

      public int getColumnCount()
      {
         return 3;
      }

      public int getColumnWidth(int col)
      {
         if (col == 0)
             return 150;

         if (col == 1)
             return 75;

         return -1;
      }

      public String getColumnName(int col)
      {
         switch (col)
         {
            case 0:
              return "#Channel";

            case 1:
              return "Users";

            case 2:
              return "Current Topic";
         }

         return "Unknown";
      }

      public Object getValueAt(int row, int col)
      {
         if (row >= getData().size()) return null;

         switch (col)
         {
            case 0:
              return ((LChannel)getData().get(row)).getChannelAttributed();

            case 1:
              return ((LChannel)getData().get(row)).getUsersAttributed();

            case 2:
              return ((LChannel)getData().get(row)).getTopicAttributed();
         }

         return "Unknown";
      }
   }

   public String getWindowType()
   {
      return "ListDialog";
   }
}
