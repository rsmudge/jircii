package rero.dcc;

import java.util.*;
import rero.config.*;
import rero.dialogs.dcc.*;

import rero.client.*;
import rero.gui.*;

public class DCCUtilities
{
   /** Resume related options *from* the resume dialog box */

   public static final int RESUME_OPTION_SELECTED    = 0;  // these have to be in order due to the way
   public static final int RENAME_OPTION_SELECTED    = 1;  // the combobox works in the DCK for dialogs
   public static final int OVERWRITE_OPTION_SELECTED = 2;  

   /** Normal DCC Options from the DCC configuration setup */
   
   public static final int ASK_OPTION_SET         = 0;
   public static final int AUTO_ACCEPT_OPTION_SET = 1;
   public static final int IGNORE_OPTION_SET      = 2;

   /** Resume related DCC Options from the DCC configuration setup */

   public static final int ASK_RESUME_OPTION_SET       = 0;
   public static final int OVERWRITE_RESUME_OPTION_SET = 1;
   public static final int RESUME_RESUME_OPTION_SET    = 2;
   public static final int IGNORE_RESUME_OPTION_SET    = 3;    

   /** returns one of RESUME_OPTION_SELECTED, RENAME_OPTION_SELECTED, or OVERWRITE_OPTION_SELECTED */
   public static final int DetermineResumeOption(Capabilities c, ConnectDCC connect)
   {
      int resumeOption = -1;
      int selectedOption = ClientState.getClientState().getInteger("dcc.exists", ClientDefaults.dcc_accept);

      switch (selectedOption)
      {
         case ASK_RESUME_OPTION_SET:
           resumeOption = ResumeRequest.showDialog(c.getGlobalCapabilities().getFrame(), connect); // lets user choose a full range of resume options :)
           break;
         case OVERWRITE_RESUME_OPTION_SET:
           resumeOption = OVERWRITE_OPTION_SELECTED;
           break;
         case RESUME_RESUME_OPTION_SET:
           resumeOption = RESUME_OPTION_SELECTED;
           break;
         case IGNORE_RESUME_OPTION_SET:
           resumeOption = -1;              // do nothing basically
           break;
      }

      return resumeOption;
   }
}


