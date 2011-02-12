/* 
    jerk.irc.dcc.ChatClient [ created - 1/2/02 ]

    Author: Raphael Mudge (rsmudge@mtu.edu)
    
    Description: 
    handles the details of talking in a DCC connection,  nothing to do
    with actually establishing the connection.  Talks to a JerkEngine
    reference.

    Documentation: 
    n/a

    Changelog:
       
*/

package rero.dcc;

import java.util.*;

import java.net.*;
import java.io.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

public class Receive extends ProtocolDCC
{   
    protected static int PACKET_SIZE = 4096;

    protected File               dumpTo;
    protected FileOutputStream   fileStream;

    protected long               receivedSize;
    protected long		 finalSize;
    protected long               startSize; // number of bytes we started out with, so resumes don't throw off rate calculations

    /**
     * Create a ready to rock and roll receive socket.  It is assumed that the File object is either cleared
     * or ready to be resumed.  An overwrite command should delete the file first and then start dumping to it.
     * Hopefully this is a safe way to go.  Eh?
     */
    public Receive(String _nickname, File _dumpTo, long _finalSize)
    {
       nickname     = _nickname;
       dumpTo       = _dumpTo;
       finalSize    = _finalSize;

       receivedSize = 0;
       startSize    = 0;
    }

    /** return the number of bytes that have been received thus far */
    public long getBytesReceived()
    {
       return receivedSize;
    }

    /** return the expected size of the file we are receiving */
    public long getExpectedSize()
    {
       return finalSize;
    }
 
    /** return the object pointing to the File we are downloading */
    public File getFile()
    {
       return dumpTo;
    }

    /** return the estimated time remaning in seconds */
    public long getTimeRemaining()
    {
       long toGo         = getExpectedSize() - getFile().length();
       long transferRate = (long)getTransferRate();

       if (transferRate == 0)
           transferRate = 1000;

       return toGo / transferRate;
    }

    public void setFile(File f)
    {
       dumpTo = f;
    }

    public void pleaseResume()
    {
       receivedSize = dumpTo.length();
       startSize    = dumpTo.length();
    }

    /** return the number of bytes that we started out with (0 normally, however in the case of a resume this number could be 
        anything from 0 up to nearly the size of the file */
    public long getStartOffset()
    {
       return startSize;
    }

    /** return the number of bytes transferred per second */
    public int getTransferRate()
    {
       if (getTotalTime() < 1000)
          return 1000;

       return (int)( (getBytesReceived() - getStartOffset()) / (getTotalTime() / 1000));
    }

    public int getTypeOfDCC()
    {
       return DCC_RECEIVE;
    }

    public void run()
    {
       if (socket == null || !socket.isConnected())
       {
          return;
       }

       try
       {
          socket.setSoTimeout(DCC_TIMEOUT);
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       // fire event that the receive has started..

       fireEvent("RECEIVE_START", null);


       startTime = System.currentTimeMillis();

       InputStream  istream = null;
       OutputStream ostream = null;

       byte[] data      = new byte[PACKET_SIZE]; // read packets in 4k chunks
       byte[] ackPacket = new byte[4];

       Exception transferError = null;

       try
       {
          boolean appendToFile = startSize != 0;

          if (!dumpTo.getParentFile().exists())
          {
             dumpTo.getParentFile().mkdirs(); /* create the dcc receive directory if it does not exist */
          }
 
          fileStream = new FileOutputStream(dumpTo, appendToFile);

          istream = socket.getInputStream();
          ostream = socket.getOutputStream();
   
          while (receivedSize < finalSize && socket.isConnected())
          {
              int thisRead;
       
              if ( (finalSize - receivedSize) < PACKET_SIZE )
              {
                 thisRead = istream.read(data, 0, (int) (finalSize - receivedSize) );
              }
              else
              {
                 thisRead = istream.read(data, 0, PACKET_SIZE);
              }

              if (thisRead > 0)
              {
                 fileStream.write(data, 0, thisRead);
             
                 receivedSize += thisRead;
  
               //  System.out.println("Read in: " + thisRead + " bytes"); 

                 //
                 // write acknowledgement packet to the output stream (for fun and profit)
                 //
                 ackPacket[0] = (byte) ( ((receivedSize & 0xff000000) >> 24) | 0 );
                 ackPacket[1] = (byte) ( ((receivedSize & 0x00ff0000) >> 16) | 0 );
                 ackPacket[2] = (byte) ( ((receivedSize & 0x0000ff00) >> 8 ) | 0 );
                 ackPacket[3] = (byte) ( ((receivedSize & 0x000000ff) >> 0 ) | 0 );
               
                // System.out.println("Writing ACK Packet: " + ackPacket);
                // System.out.println("Received Size     : " + receivedSize);

                 ostream.write(ackPacket, 0, 4);
                 ostream.flush();
              }
          }
       }
       catch (Exception ex)
       {
          if (receivedSize != finalSize)
          {
              ex.printStackTrace();
              transferError = ex;
          }
       }

       try
       {
          fileStream.close();
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       if (receivedSize == finalSize)
       {
          //
          // dispatch the send succeeded event
          //
          fireEvent("RECEIVE_COMPLETE", null);
       }
       else if (transferError != null)
       {
          fireError(transferError.getMessage());
       }
       else
       {
          fireError("incomplete");
       }
    }

    public void fireEvent(String event, String description)
    {
       eventData.put(FrameworkConstants.$NICK$,   getNickname());
       eventData.put(FrameworkConstants.$EVENT$,  event);
       eventData.put(FrameworkConstants.$DATA$,  getNickname() + " " + description);
       eventData.put(FrameworkConstants.$PARMS$, description);
       eventData.put("$this", this.toString());

       dispatcher.dispatchEvent(eventData);
    }

    public void fireError(String description)
    {
       eventData.put(FrameworkConstants.$NICK$,   getNickname());
       eventData.put(FrameworkConstants.$EVENT$,  "RECEIVE_FAILED");
       eventData.put(FrameworkConstants.$DATA$,  getNickname() + " " + description);
       eventData.put(FrameworkConstants.$PARMS$, description);
       eventData.put("$this", this.toString());

       dispatcher.dispatchEvent(eventData);
    }
}
