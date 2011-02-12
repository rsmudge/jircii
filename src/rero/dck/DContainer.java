package rero.dck;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import rero.dck.items.*;

import java.util.*;

public abstract class DContainer
{
    protected LinkedList itemList;
    protected JComponent dialog;      // the dialog (we're caching it, cuz we're cool like that)

    protected GridBagConstraints constraints = new GridBagConstraints();
    protected DCapabilities capabilities;

    public DContainer()
    {
       itemList = new LinkedList();

       constraints.gridwidth = GridBagConstraints.REMAINDER;
       constraints.fill      = GridBagConstraints.BOTH;
       constraints.weightx   = 1.0;
       constraints.insets    = new Insets(0, 0, 2, 0);
    }
 
    public void installCapabilities(DCapabilities c)
    {
       capabilities = c;
    }

    public DCapabilities getCapabilities()
    {
       return capabilities;
    }


    public JComponent getDialog()
    {
       if (dialog != null)
       {
          return dialog;
       }

       dialog = new JPanel();

       // setup the initial layout for the dialog (different based on the type of container this is)

       JComponent child = setupLayout(dialog);

       // add the items to this container

       setupDialog();

       // handle the "alignment" for each of the items... my favorite part, isn't it yours?

       int maxWidth = 0;

       Iterator i = itemList.iterator();
       while (i.hasNext())
       {
          DItem temp = (DItem)i.next();

          if (temp.getEstimatedWidth() > maxWidth)
          {
             maxWidth = temp.getEstimatedWidth();
          }
       }

       GridBagLayout layout = new GridBagLayout();
       child.setLayout(layout);

       i = itemList.iterator();
       while (i.hasNext())
       {
          DItem temp = (DItem)i.next();
          temp.setAlignWidth(maxWidth);

          JComponent blah = temp.getComponent();

          layout.setConstraints(blah, constraints);
          child.add(blah);
       }

       return dialog;
    }

    public void setEnabled(boolean b)
    {
       Iterator i = itemList.iterator();
       while (i.hasNext())
       {
          DItem temp = (DItem)i.next();
          temp.setEnabled(b);
       }

       dialog.setEnabled(b);
    }

    public void setParent(DParent parent)
    {
       Iterator i = itemList.iterator();
       while (i.hasNext())
       {
          DItem temp = (DItem)i.next();
          temp.setParent(parent);
       }
    }

    public void save()
    {
       Iterator i = itemList.iterator();
       while (i.hasNext())
       {
          DItem temp = (DItem)i.next();
          temp.save();
       }
    }

    public void refresh()
    {
       Iterator i = itemList.iterator();
       while (i.hasNext())
       {
          DItem temp = (DItem)i.next();
          temp.refresh();
       }
    }

    public abstract JComponent setupLayout(JComponent component);
    public abstract void       setupDialog();
    public abstract String     getTitle();

    public StringInput addStringInput(String var, String defValue, String label, char Mn)
    {
       StringInput temp = new StringInput(var, defValue, label, 0, Mn); 
       itemList.add(temp);
       return temp;
    }

    public StringInput addStringInput(String var, String defValue, String label, char Mn, int gap)
    {
       StringInput temp = new StringInput(var, defValue, label, gap, Mn); 
       itemList.add(temp);
       return temp;
    }

    public CheckboxInput addCheckboxInput(String variable, boolean defValue, String label, char Mn)
    {
       CheckboxInput temp = new CheckboxInput(variable, defValue, label, Mn); 
       itemList.add(temp);
       return temp;
    }

    public CheckboxInput addCheckboxInput(String variable, boolean defValue, String label, char Mn, int alignment)
    {
       CheckboxInput temp = new CheckboxInput(variable, defValue, label, Mn, alignment); 
       itemList.add(temp);
       return temp;
    }

    public FileInput addFileInput(String variable, String defValue, String label, char Mn, int inset)
    {
       FileInput temp = new FileInput(variable, defValue, label, Mn, false, inset); 
       itemList.add(temp);
       return temp;
    }

    public DirectoryInput addDirectoryInput(String variable, String defValue, String label, char Mn, int inset)
    {
       DirectoryInput temp = new DirectoryInput(variable, defValue, label, Mn, inset); 
       itemList.add(temp);
       return temp;
    }

    public ListInput addListInput(String variable, String title, String desc, int width, int height)
    {
       ListInput temp = new ListInput(variable, title, desc, width, height); 
       itemList.add(temp);
       return temp;
    }

    public FontInput addFontInput(String variable, Font defaultf)
    {
       FontInput temp = new FontInput(variable, defaultf);
       itemList.add(temp);
       return temp;
    }

    public CharsetInput addCharsetInput(String variable, String label, char Mnemonic, int gap)
    {
       CharsetInput temp = new CharsetInput(variable, label, Mnemonic, gap);
       itemList.add(temp);
       return temp;
    }

    public SelectInput addSelectInput(String variable, int defaultV, String[] values, String label, char Mnemonic, int gap)
    {
       SelectInput temp = new SelectInput(variable, defaultV, values, label, Mnemonic, gap);
       itemList.add(temp);
       return temp;
    }

    public OptionInput addOptionInput(String variable, String defaultV, String[] values, String label, char Mnemonic, int gap)
    {
       OptionInput temp = new OptionInput(variable, defaultV, values, label, Mnemonic, gap);
       itemList.add(temp);
       return temp;
    }

    public NetworkSelect addNetworkSelector(String variable, String variable2)
    {
       NetworkSelect temp = new NetworkSelect(variable, variable2);
       itemList.add(temp);
       return temp;
    }

    public TextInput addTextInput(String variable, int gap)
    {
       TextInput temp = new TextInput(variable, gap);
       itemList.add(temp);
       return temp;
    }
  
    public ColorInput addColorInput(String variable, Color defaultColor, String text, char mnemonic)
    {
       ColorInput temp = new ColorInput(variable, defaultColor, text, mnemonic);
       itemList.add(temp);
       return temp;
    }

    public FloatInput addFloatInput(String variable, float defaultf, String text)
    {
       FloatInput temp = new FloatInput(variable, defaultf, text);
       itemList.add(temp);
       return temp;
    }

    public DGroup addDialogGroup(DGroup g)
    {
       itemList.add(g);
       return g;
    }

    public BlankInput addBlankSpace()
    {
       BlankInput temp = new BlankInput(); 
       itemList.add(temp);
       return temp;
    }

    public LabelInput addLabel(String text, int gap)
    {
       LabelInput temp = new LabelInput(text, gap); 
       itemList.add(temp);
       return temp;
    }

    public NormalInput addLabelNormal(String text, int align)
    {
       NormalInput temp = new NormalInput(text, align); 
       itemList.add(temp);
       return temp;
    }

    public DItem addComponent(JComponent component)
    {
       OtherInput temp = new OtherInput(component);
       itemList.add(temp);
       return temp;
    }

    public TabbedInput addTabbedInput()
    {
       TabbedInput temp = new TabbedInput();
       itemList.add(temp);
       return temp;
    }

    public DItem addOther(DItem item)
    {
       itemList.add(item);
       return item;
    }
}
