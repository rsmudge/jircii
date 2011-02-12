package rero.dialogs.dcc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;

import rero.dialogs.toolkit.*;
import rero.dcc.*;

import rero.util.*;

public class SendRequest extends APanel
{
    protected FileField fileField;
    protected Receive   receive;   // the *receiving* end - *uNF*
 
    public static boolean showDialog(Component component, ConnectDCC connect)
    {
       long start = System.currentTimeMillis();
       SendRequest request = new SendRequest();
       request.setupDialog(connect);

       ADialog dialog = new ADialog(component, "DCC Send Request", request, null);
       dialog.pack();
       return (dialog.showDialog(component) != null);
    }

    public void setupDialog(Object value)
    {
       JPanel space = new JPanel();
       space.setPreferredSize(new Dimension(0, 15));

       JPanel space2 = new JPanel();
       space2.setPreferredSize(new Dimension(0, 15));

       LabelGroup labels = new LabelGroup();
       JLabel user, file, size, saveas, host, blank;

       user   = new JLabel("User: ");
       host   = new JLabel("Host: ");
       file   = new JLabel("File: ");
       size   = new JLabel("Size: ");
       saveas = new JLabel("Save As: ");
       blank  = new JLabel("");

       labels.addLabel(user); labels.addLabel(file); labels.addLabel(size); labels.addLabel(saveas); labels.addLabel(blank); labels.addLabel(host);
       labels.sync(); // lines the labels up

       ConnectDCC info1 = (ConnectDCC)value;
       Receive    info2 = (Receive)info1.getImplementation();

       receive = info2;

       PlainLabel iuser, ifile, isize, ihost;

       iuser = new PlainLabel(info2.getNickname());
       ihost = new PlainLabel(info1.getHost() + ":" + info1.getPort());
       ifile = new PlainLabel(info2.getFile().getName());
       isize = new PlainLabel(ClientUtils.formatBytes(info2.getExpectedSize()));

       addComponent(new PlainLabel("A user is attempting to send you a file"));

       addComponent(space2);

       addComponent(mergeComponents(user,   iuser));
       addComponent(mergeComponents(host,   ihost));
       addComponent(mergeComponents(file,   ifile));
       addComponent(mergeComponents(size,   isize));

       addComponent(space);

       fileField = new FileField(info2.getFile(), false);
       addComponent(mergeComponents(saveas, fileField, 15));
    }
 
    public Object getValue(Object defvalue)
    {
       receive.setFile(fileField.getSelectedFile());  // getValue() only called when we have a confirmed acceptance       
       return "";
    }
}
