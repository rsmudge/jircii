package apple;

import com.apple.eawt.*;

import rero.gui.*;

/** A class for handling some OS X specific stuff.  This class is built separately of the jIRC source code.
    This code is meant to hook jIRC into the "jIRC" menu in the Apple menubar at the top of the screen. */
public class OSXAdapter extends ApplicationAdapter 
{
    private static com.apple.eawt.Application theApplication;

    public void handleAbout(ApplicationEvent ae) 
    {
        ae.setHandled(true);
        SessionManager.getGlobalCapabilities().showAboutDialog();
    }
	
    public void handlePreferences(ApplicationEvent ae) 
    {
	ae.setHandled(true);
        SessionManager.getGlobalCapabilities().showOptionDialog("");
    }
	
    public void handleQuit(ApplicationEvent ae) 
    {
	ae.setHandled(false);
        SessionManager.getGlobalCapabilities().QuitClient();
    }
    
/*    public void handleOpenFile(ApplicationEvent ev)
    {
//        ev.setHandled(true);
        System.out.println("OpenFile: Asked to open " + ev.getFilename() + ", " + ev);
    }

    public void handleOpenApplication(ApplicationEvent ev)
    {
//        ev.setHandled(true);
        System.out.println("Open Application: Asked to open " + ev.getFilename() + ", " + ev);
    }

    public void handleReOpenApplication(ApplicationEvent ev)
    {
//        ev.setHandled(true);
        System.out.println("ReOpen Application: Asked to open " + ev.getFilename() + ", " + ev);
    } */
  
    public static void registerMacOSXApplication() 
    {
	if (theApplication == null) 
        {
	    theApplication = new com.apple.eawt.Application();
	    theApplication.setEnabledPreferencesMenu(true);
        }			
		
        theApplication.addApplicationListener(new OSXAdapter());
    }
}
