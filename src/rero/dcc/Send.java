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

public class Send extends ProtocolDCC
{   
    protected static int PACKET_SIZE = 4096;

    protected File               dumpFrom;
    protected FileInputStream    fileStream;

    protected long               sentSize;
    protected long               ackSize;
    protected long		 finalSize;
    protected long               startSize;  // an offset of where we started from, so resumes don't throw off the download stats

    public boolean resume(long size)
    {
       if (size < dumpFrom.length())
       {
          sentSize  = size;
          startSize = size;

          return true;
       }

       return false;
    }

    /**
     * Create a ready to rock and roll receive socket.  It is assumed that the File object is either cleared
     * or ready to be resumed.  An overwrite command should delete the file first and then start dumping to it.
     * Hopefully this is a safe way to go.  Eh?
     */
    public Send(String _nickname, File _dumpFrom)
    {
       nickname     = _nickname;
       dumpFrom     = _dumpFrom;

       finalSize    = _dumpFrom.length();
       ackSize      = 0;
       sentSize     = 0;

       startSize    = 0;

       eventData    = new HashMap();
    }

    /** return the total number of bytes that have been sent */
    public long getBytesSent()
    {
       return sentSize;
    }

    /** return the total number of bytes that have been acknowledged by the user */
    public long getAcknowledgedSize()
    {
       return ackSize;
    }

    /** return the File we are sending to the user */
    public File getFile()
    {
       return dumpFrom;
    } 

    /** return the number of bytes that we started out with (0 normally, however in the case of a resume this number could be
        anything from 0 up to nearly the size of the file */
    public long getStartOffset()
    {
       return startSize;
    }

    /** return the estimated time remaning in seconds */
    public long getTimeRemaining()
    {
       long totalSizeLeft = getFile().length() - getBytesSent();
       long transferRate  = (long)getTransferRate();

       if (transferRate == 0)
           transferRate = 1000;

       return totalSizeLeft / transferRate;
    }

    /** return the number of bytes transferred per second */
    public int getTransferRate()
    {
       if (getTotalTime() < 1000)
            return 1000;

       return (int)( (getBytesSent() - getStartOffset()) / (getTotalTime() / 1000));
    }

    public int getTypeOfDCC()
    {
       return DCC_SEND;
    }

    /** provided by nanaki@gmail.com using http://findbugs.sourceforge.net/ */
    private static void skipFully(InputStream in, long nBytes) throws IOException
    {
       long remaining = nBytes;
       while (remaining != 0)
       {
          long skipped = in.skip(remaining);
          if (skipped == 0) throw new EOFException();
          remaining -= skipped;
       }
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

       fireEvent("SEND_START", null);

//       dispatcher.dispatchEvent(eventData);

       DataInputStream  istream = null;
       OutputStream     ostream = null;

       byte[] data      = new byte[PACKET_SIZE]; // send packets in 4k chunks

       try
       {
          fileStream = new FileInputStream(dumpFrom);
          skipFully(fileStream, sentSize);

          istream = new DataInputStream(socket.getInputStream());
          ostream = socket.getOutputStream();
   
          //
          // pump the file out as fast as we can, check the acknowledgements when we're done.
          //

          int thisRead;

          while (socket.isConnected() && sentSize < finalSize)
          {
             if ( (finalSize - sentSize) < PACKET_SIZE )
             {
                thisRead = fileStream.read(data, 0, (int) (finalSize - sentSize) );
             }
             else
             {
                thisRead = fileStream.read(data, 0, PACKET_SIZE);
             }

             if (thisRead > 0)
             {
                ostream.write(data, 0, thisRead);
                ostream.flush();
                sentSize += thisRead;

                // System.out.println("Sent: " + sentSize + " bytes of " + finalSize); 
             }  

             if (istream.available() >= 4)
             {
                ackSize = 0;
                ackSize += istream.readUnsignedByte() << 24L;
                ackSize += istream.readUnsignedByte() << 16L;
                ackSize += istream.readUnsignedByte() <<  8L;
                ackSize += istream.readUnsignedByte() <<  0L;

                // System.out.println("Ackd: " + ackSize + " bytes");

                idleTime = System.currentTimeMillis();
             }
          }

          while (socket.isConnected() && ackSize < finalSize && getIdleTime() < 10000)
          { 
             if (istream.available() >= 4)
             {
                ackSize = 0;
                ackSize += istream.readUnsignedByte() << 24L;
                ackSize += istream.readUnsignedByte() << 16L;
                ackSize += istream.readUnsignedByte() <<  8L;
                ackSize += istream.readUnsignedByte() <<  0L;

                // System.out.println("Ackd: " + ackSize + " bytes");

                idleTime = System.currentTimeMillis();
             }
             else
             {
                Thread.sleep(500);
             }
          }
       }
       catch (Exception ex)
       {
          if (sentSize != finalSize)
          {          
             ex.printStackTrace();
             fireError(ex.getMessage());
             return;
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

       if (sentSize == finalSize)
       {
          fireEvent("SEND_COMPLETE", null);
       }
       else
       {
          fireError("incomplete");
       }
    }

    public void fireError(String description)
    {
       eventData.put(FrameworkConstants.$NICK$,   getNickname());
       eventData.put(FrameworkConstants.$EVENT$, "SEND_FAILED");
       eventData.put(FrameworkConstants.$DATA$,  getNickname() + " " + description);
       eventData.put(FrameworkConstants.$PARMS$, description);
       eventData.put("$this", this.toString());

       dispatcher.dispatchEvent(eventData);
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
}
