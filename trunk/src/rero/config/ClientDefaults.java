package rero.config;

import java.util.*;
import java.awt.*;

import rero.client.dcc.LocalInfo;

import java.io.File;

/* nifty client defaults y0 */
public class ClientDefaults
{
   public static final String  ident_userid  = System.getProperty("user.name");
   public static final String  ident_system  = System.getProperty("os.name").replaceAll(" ", ""); 
   public static final int     ident_port    = 113;
   public static final boolean ident_enabled = (System.getProperty("os.name").indexOf("Window") > -1) ? true : false; // by default enable ident on Windows, disable it everywher else though..

   public static final int 	max_history   = 30;
   public static final String  dcc_saveto     = System.getProperty("user.home");
   public static final int     dcc_low        = 4096;
   public static final int     dcc_high       = 8192;
   public static final int     dcc_accept     = 0;    // 0 => ask 1=> auto accept 2=> ignore (/dcc accept is still an option)
   public static final boolean dcc_fillspaces = true;  /** fill spaces when sending a file */
   public static final String  dcc_localinfo  = LocalInfo.RESOLVE_FROM_SERVER;

   public static final String  current_theme  = "jIRCii Default";

   public static final int     listbox_width  = 10; // 10 characters...

   public static final boolean auto_option   = true; /** auto /window default value(s) */
   public static final boolean active_option = true; /** echo to active option */

   public static final boolean option_showmotd  = true;
   public static final boolean option_reconnect = true;
   public static final boolean option_timestamp = false;
   public static final boolean dclick_links     = true; // open links with double click

   public static       boolean ui_sdi        = true;
   public static       boolean ui_native     = false; 

   public static       String  ui_openfiles;

   public static final int     ui_sbarlines   = 1;    // number of statusbar lines by default
   public static final Color   ui_editcolor   = Color.lightGray;
   public static       Font    ui_font        = new Font("Courier New", Font.BOLD, 16);
   public static final boolean ui_usetoolbar  = true;
   public static final boolean ui_showtabs    = true;
   public static final boolean ui_showbar     = true;
   public static final boolean ui_showrestart = true; // show warning dialog notifying a user restart is required for options to take effect

   public static final int     ui_buffersize  = 2000; // max size of scrollback buffer...

   public static final int     notabs_border  = 1; // size of app border when server tabs are turned off

   public static       boolean ui_antialias   = true; // enable/disable text anti-aliasing

   public static final boolean script_ignoreWarnings = false;
   public static final boolean script_verboseLoad    = true;

   public static final boolean switchbar_fixed    = false; // tell the switchbar buttons to be a fixed width
   public static final int     switchbar_position = 0; // top
   public static final Color   switchbar_color    = Color.red;
   public static final boolean switchbar_sort     = false; // sort switchbar buttons alphabetically...
   public static final boolean switchbar_hilight  = true; // determine if switchbar hilighting is on/off

   public static final boolean client_stripcodes  = false; // strip colors from server?

   public static final boolean log_enabled   = false;
   public static final String  log_saveto    = new File(System.getProperty("user.home"), "irc_logs").getAbsolutePath();
   public static final boolean log_strip     = true;
   public static final boolean log_timestamp = true;

   public static final String  proxy_server   = System.getProperty("socksProxyHost", "");
   public static final String  proxy_port     = System.getProperty("socksProxyPort", "1080");
   public static final String  proxy_userid   = System.getProperty("java.net.socks.username", "");
   public static final String  proxy_password = System.getProperty("java.net.socks.password", "");
   public static final boolean proxy_enabled  = proxy_server != null; 

   public static final int     reconnect_time = 5;

   public static final boolean update_ial     = true;

   // Attention / notification values
   // OS X
   public static final boolean attention_osx_bouncedock_msg		= true;
   public static final boolean attention_osx_bouncedock_notice		= true;
   public static final boolean attention_osx_bouncedock_channelchat	= true;
   public static final boolean attention_osx_bouncedock_actions		= true;
   public static final boolean attention_osx_bouncedock_repeat		= true;
   // TODO: Windows, Linux
   
   public static final String  version_string = "07.20.11";	// Full version string

   // This would build out to: 0.90(-revision)(+extra)
   public static final String  version_major  = "0";
   public static final String  version_minor  = "90";
   public static final String  version_rev    = "rev26";
   public static final String  version_extra  = "";
}
