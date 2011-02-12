package rero.dialogs.toolkit;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class PlainLabel extends JTextField
{ 
   public PlainLabel(String text)
   {
      setBorder(null);
      setEditable(false);
      setOpaque(false);
      setText(text); 
   }
}
