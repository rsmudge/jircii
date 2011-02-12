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

import text.*;

import rero.gui.toolkit.*;

public class GeneralListDialog extends EmptyWindow
{
   protected GeneralListModel  model;
   protected JSortTable        table;
   protected String            popupHook;
   protected String            name;

   public GeneralListDialog(String _name, String _hook, GeneralListModel _model)
   {
      name = _name;
      popupHook = _hook;

      this.model = _model;

      setLayout(new BorderLayout());

      table = new JSortTable(model);
      table.setOpaque(false);
      table.setRowSelectionAllowed(true);
      table.setColumnSelectionAllowed(false);
      table.setDefaultRenderer((new Object()).getClass(), new MyRenderer());
      table.setShowGrid(false);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.setRowHeight(TextSource.fontMetrics.getHeight() + 2);

      JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroller.setOpaque(false);
      scroller.getViewport().setOpaque(false);
      scroller.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());

      TableColumn tempcol;
      int         x = 0;

      tempcol = table.getColumnModel().getColumn(x);
      tempcol.setMinWidth(1);
      tempcol.setMaxWidth(model.getColumnWidth(x) * 3);
      tempcol.setPreferredWidth(model.getColumnWidth(x));

      for (x = 1; x < model.getColumnCount() - 1; x++)
      {
         tempcol = table.getColumnModel().getColumn(x);
         tempcol.setMinWidth(1);
         tempcol.setMaxWidth(model.getColumnWidth(x) * 3);
         tempcol.sizeWidthToFit();
      }

      table.setRowSelectionAllowed(true);

      scroller.setViewportBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0)); // a small tweak to make the sorted list dialogs look alright

      ToolTipManager.sharedInstance().unregisterComponent(table); // performance enhancement, disable the tooltip for the elements
      ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

      add(scroller);

      table.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p); // This is the view column!

            maybeShowPopup(e, model.getEventHashMap(row));
         }

         public void mouseClicked(MouseEvent e)
         {
            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p); // This is the view column!

            maybeShowPopup(e, model.getEventHashMap(row));

            if (e.getClickCount() == 2 && !e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed())
            {
               processMouseEvent(e, row);
               e.consume();
            }

         }

         public void mouseReleased(MouseEvent e)
         {
            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p); // This is the view column!

            maybeShowPopup(e, model.getEventHashMap(row));
         }
       });
   }

   public void init() { }

   protected void maybeShowPopup(MouseEvent ev, HashMap data)
   {
      JPopupMenu menu = getPopupMenu(popupHook, data);

      if (ev.isPopupTrigger() && menu != null)
      {
         menu.show((JComponent)ev.getComponent(), ev.getX(), ev.getY());
         ev.consume();
      }
   }

   public String getName()
   {
      return name;
   }

   public ImageIcon getImageIcon()
   {
      if (icon == null)
      {
         icon = new ImageIcon(ClientState.getClientState().getResource("jsmall.gif"));
      }

      return icon;
   }

   public void processMouseEvent(MouseEvent ev, int row)
   {
      fireClickEvent(row+"", ev);
   }

   private static class MyRenderer implements TableCellRenderer
   {
      private JLabel          select = new JLabel();
      private AttributedLabel labeln = new AttributedLabel();
      private AttributedLabel labels = new AttributedLabel();

      public MyRenderer()
      {
         select.setOpaque(true);

         select.setLayout(new BorderLayout());
         select.setBorder(BorderFactory.createEmptyBorder(0, TextSource.UNIVERSAL_TWEAK, 0, 0));
         select.add(labels);
      }

      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
         if (value == null)
            return new JLabel();

         AttributedString attrs = (AttributedString)value;

         if (isSelected)
         {
            select.setFont(TextSource.clientFont);
            select.setBackground(table.getSelectionBackground());
            select.setForeground(table.getSelectionForeground());
            select.setText(attrs.getText());
            return select;
         }

         labeln.setText(attrs);
         return labeln;
      }
   }
}
