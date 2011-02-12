package rero.ircfw.interfaces;

public interface FrameworkConstants
{
   //
   // Event variables..
   //

   /** the type of event */
   public static String $EVENT$   = "$event";
   public static String $NUMERIC$ = "$numeric";

   /** information regarding the source of the event */
   public static String $SOURCE$  = "$source";
      public static String $NICK$    = "$nick";
      public static String $ADDRESS$ = "$address";
         public static String $USER$    = "$user";
         public static String $HOST$    = "$host";

      public static String $SERVER$  = "$server";

   /** the target of the event, who is the event "to" */
   public static String $TARGET$  = "$target";

   /** parameters from the event */
   public static String $DATA$    = "$data";
   public static String $RAW$     = "$raw";
   public static String $PARMS$   = "$parms";

   /** type of CTCP request (for CTCP's ONLY) */
   public static String $TYPE$    = "$type";
}
