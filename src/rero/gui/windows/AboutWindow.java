/** an easter egg... I'm not going to bother hiding the code for this one event though it is one of my favorites */

package rero.gui.windows;

import rero.gui.*;
import rero.gui.input.*;

import text.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.config.*;

public class AboutWindow extends StatusWindow implements Runnable
{
    Random  random     = new Random(System.currentTimeMillis());
    boolean showKow    = false; 
    Color   background = null;
    String  me         = ClientState.getClientState().getString("user.nick", "lamer");
//    String  nnn        = "14\u038715\u00F810\u0387";
    String  nnn        = "04\u202216\u00F804\u2022";
    String  name       = "";  

    boolean delay      = true;

    public AboutWindow()
    {
       showKow    = random.nextBoolean();

       if (showKow) { background = Color.white; name = "#Floods"; }
       else { background = Color.black; name = "@mIRCii"; }
    }

    public String getName()
    {
       return name;
    }

    public void run()
    {
       if (showKow)
       {
          bkow();
       }
       else
       {
          mircii();
       }
    }

    private static class UserModel extends AbstractListModel
    {
       String[] users = new String[] { "@Bass", "@BLaHSTeR", "@G-dAwG", "@JakieChan", "@LiquidIQ", "@misfits", "@NiN-PLoP", "@rUINER", "@Terra-", "@vicadin", "@`butane", "@|ChIcKeN|", "funbox" };

       public void setUsers(String[] _users) { users = _users; fireContentsChanged(this, 0, users.length); }

       public Object getElementAt(int index)
       {
           return users[index];
       }

       public int getSize()
       {
           return users.length;
       }
    }

    public void bkow()
    {
      UserModel users = new UserModel();

      JList lusers = new JList(users);
      lusers.setBorder(null);

      lusers.setFont(ClientState.getClientState().getFont("ui.font", ClientDefaults.ui_font));
      JScrollPane scroller = new JScrollPane(lusers, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scroller.setBorder(null);

      add(scroller, BorderLayout.EAST);
      revalidate();

      eline("3*** Now talking in #floods");
      delay = false;
      eline("9Topic for #Floods [PLOP]");
      eline("9Topic set by |ChIcKeN| on [Tue Jul 15 02:57:00 1997]");
      eline("9@Bass @BLaHSTeR @G-dAwG @JakieChan @LiquidIQ @misfits @NiN-PLoP");
      delay = true;
      eline("9@rUINER @Terra- @vicadin @`butane @|ChIcKeN| funbox");

      try { Thread.sleep(1250); } catch (Exception ex) { }

      eline("<`butane> hey guys im going to make a script that will revolutionize mIRC.. anyone want to help?");
      eline("<vicadin> umm i would but i umm have to umm go walk the dog..");
      eline("<misfits> fuck `butane i would love to help but blind is 5 months overdue");
      eline("<funbox> butane isnt this the 6th time you started a script that was going to revolutionize mIRC");
      eline("<`butane> box if you dont shut up im going to kick you you lame newbie");
      eline("<funbox> butane i have owners on the eggs your using dumbass");
      eline("<funbox> hey chanman Voltron knows your home address and hes going to come to your house and kick your ass");
      eline("<JakieChan> no he doesnt you fucking liar");
      eline("<funbox> do i have to read it to you??");
      eline("<JakieChan> yah i dare you");
      eline("<funbox> u sure?");
      eline("<`butane> dont worry chan if he does anything ill get some of my aol friends and we'll make sure your safe");

      users.setUsers(new String[] { "@Bass", "@BLaHSTeR", "@G-dAwG", "@JakieChan", "@LiquidIQ", "@misfits", "@NiN-PLoP", "@rUINER", "@Terra-", "@vicadin", "@`butane", "@|ChIcKeN|", "funbox", "Voltron" });
      eline("3*** Voltron (~Nachoes@p09.hwts12.loop.net) has joined #floods");

      eline("<Voltron> why am I here? Im missing Voltron for this you know...");
      eline("<funbox> JakieChan has had a memory collapse and wants to know his home address");
      eline("<Voltron> oh does he still not believe im going to come over to his house and kick the living shit out of him?");
      eline("<JakieChan> Yeah I dare you to come to my house.");
      eline("<JakieChan> Besides we all know you are just a stupid faggot that eats nachoes all day.");
      eline("<Voltron> Hey JakieChan wouldn't it suck if I happened to be going to the store and I passed by Granada Hl, San Fernando,CA 91344");
      eline("<JakieChan> ttas nto my addressss...asdf");
      eline("<Voltron> are you sure? because i called (818)360-2843 and asked for Peter Arsoff and they said hold on.. I hung up real fast");
      eline("<JakieChan> `butane what the fuck you said that there was no way he could get my address you fucking liar!!!");
      eline("<`butane> thats not your real address though ;)");
      eline("<JakieChan> I know that but umm err.. how is your script coming along?");
      eline("<`butane> all this fighting is bad for my scripting!! Im revolutionizing mIRC you know..");
      eline("<funbox> whatever happened to all the scripts before that where going to revolutionize mIRC?");
      eline("<`butane> they are on hold so that i can work on this");
      eline("<vicadin> what happened to that killer script that was going to own htew0?");
      eline("<`butane> wraithX is a dumbass he didnt fully appreciate my work");
      eline("<funbox> really? he told me that you cant script for shit and that you didnt make any pop ups..");
      eline("<`butane> thats cuz hes a newbie that cant understand that the /cumjizm alias was for the away and that /orgasim was for back...");
      eline("<vicadin> oh jeeze how could he not know that? i thought it was common knowledge....");
      eline("<LiquidIQ> hey FRoZeN is making fun of me on EFnet again");
      eline("<`butane> why what did you do to him?");
      eline("<LiquidIQ> I flooded him! heeeeheee this MaD KoW stuff is l33t ");
      eline("<`butane> i know im making a new script with this new technology called an echo flood");
      eline("<LiquidIQ> oh shit!!! i cant fucking wait man");
      eline("<funbox> snoop dawgie do you want me to get on the eggs and rearrange #floods again??");
      eline("<JakieChan> FUCK QUICK KB THE BOTS!!!");
      eline("3*** JakieChan sets mode: +b *!*@undertow.net");

      delay = false;
      users.setUsers(new String[] { "@Bass", "@G-dAwG", "@JakieChan", "@LiquidIQ", "@misfits", "@NiN-PLoP", "@Terra-", "@vicadin", "@`butane", "@|ChIcKeN|", "funbox", "Voltron" });
      eline("3*** BLaHSTeR was kicked by JakieChan (AAAA)");
      delay = true;
      eline("3*** rUINER was kicked by JakieChan (ALFJDFJ)");

      eline("<funbox> oh well i tried to be nice but you asked for it");

      delay = false;
      eline("3*** Terra- sets mode +oo funbox Voltron");
      eline("3*** funbox sets mode -oooooo Bass G-dAwG JakieChan LiquidIQ misfits NiN-PLoP");
      users.setUsers(new String[] { "@funbox", "@Terra-", "@Voltron", "Bass", "G-dAwG", "JakieChan", "LiquidIQ", "misfits", "NiN-PLoP", "vicadin", "`butane", "|ChIcKeN|" });
      delay = true;
      eline("3*** funbox sets mode -ooo vicadin `butane |CHiCKeN|");

      setTitle("#Floods [+tn]: kkow, we're stupid, we know it, but fuck man what can we do??");
      eline("2*** funbox changes topic to \"kkow, we're stupid, we know it, but fuck man what can we do??\"");

      eline("<funbox> how many times does this have to happen before you guys stop being rude to me");
      eline("<Voltron> hahahahahahha you people are stupid.. im going to go watch the rest of Voltron");

      users.setUsers(new String[] { "@funbox", "@Terra-", "Bass", "G-dAwG", "JakieChan", "LiquidIQ", "misfits", "NiN-PLoP", "vicadin", "`butane", "|ChIcKeN|" });
      eline("3*** Voltron (~Nachoes@p09.hwts12.loop.net) has left #floods");

      eline("<Terra-> fun your crazy :P");

      users.setUsers(new String[] { "@funbox", "Bass", "G-dAwG", "JakieChan", "LiquidIQ", "misfits", "NiN-PLoP", "vicadin", "`butane", "|ChIcKeN|" });
      eline("3*** Terra- (peace@port28.pitton.com) has left #floods");

      eline("<`butane> give it back box.. be mature");
      eline("<|CHiCKeN|>`butane should i start icmp'ing him from my t3???");
      eline("<`butane> give him a chance to leave and be mature");
      eline("<funbox> `butane told me to do it ");

      users.setUsers(new String[] { "Bass", "G-dAwG", "JakieChan", "LiquidIQ", "misfits", "NiN-PLoP", "vicadin", "`butane", "|ChIcKeN|" });
      eline("3*** funbox (people@ppp4.respool1.phila.microserve.com) has left #floods");

      eline("<misfits> W00t i just smoked weed!!!!!!!!!!!!!!!!!!! it was cool i smoked it through a needle");
      eline("<LiquidIQ> Im going to flood funbox");
      eline("<misfits> yah ill help you that guy is a fag");
      eline("<`butane> man im going to go work on my revolutionary script.. i havent even come up with a name yet");
      eline("<`butane> I wuv BobsKC");
    }
    
    public void mircii()
    {

       eline("12<15`butane12>15 god damned it, another lamer loaded mircii");
       eline("12<15`butane12>15 why the hell do I even make it public...");

       if (background != Color.black)
       { 
          eline("12<15`butane12>15 jesus, the pussy doesn't even have a black background");
       }

       type("hello?");
       eline("6<15"+me+"6>15 hello?");
       eline("12<15`butane12>15 hi..");
       type("this your addon?");
       eline("6<15"+me+"6>15 this your addon?");
       type("/sv");
       eline("6<15"+me+"6>15 mIRC 5.31^16m15ircii%fINAL+CLONE by butane");
       eline("12<15`butane12>15 heh...");
       eline("12<15`butane12>15 yeah of course its my addon");
       eline("12<15`butane12>15 why?");
       type("well like...  I just wanted to like tell you that ");       
       eline("6<15"+me+"6>15 well like...  I just wanted to like tell you that ");

       if (random.nextBoolean())
       { 
          type("like it well... it just sucks.");
          eline("6<15"+me+"6>15 like it well... it just sucks."); 
       }
       else 
       {   
          type("c-scripts mserver is 1000x better. plus its christain!");
          eline("6<15"+me+"6>15 c-scripts mserver is 1000x better. plus its christain!");
       }

       eline("12<15`butane12>15 you came all the way from dalnet to tell me that?");
       eline(nnn+" 11madgoat 14[10khaled@mardam.demon.co.uk14]15 has joined #addons");
       eline("12<15madgoat12>15 hi.. ");

       if (random.nextBoolean())
       { 
           eline("12<15madgoat12>15 I got some pics of me and my stuffed animal getting it on!");
       }
       else 
       { 
           eline("12<15madgoat12>15 I got some pics of me and tjerk getting it on!");
       }

       if (random.nextBoolean())
       { 
           eline("12<15madgoat12>15 I'll trade them for a copy of pIRCh!@"); 
       }
       else 
       { 
           eline("12<15madgoat12>15 I'll trade them for a copy of xircon!@"); 
       }

       type("hrm... thats not to bad of a deal...");
       eline("6<15"+me+"6>15 hrm... thats not to bad of a deal...");
       type("but all I got is this half assed mirc addon.");
       eline("6<15"+me+"6>15 but all I got is this half assed mirc addon.");
       type("/sv");
       eline("6<15"+me+"6>15 mIRC 5.31^16m15ircii%fINAL+CLONE by butane");
       eline("12<15madgoat12>15 hmmm...  thats lame.");
       eline("12<15madgoat12>15 16m15ircII 2.8.b WIN* :this is a bug free clone.  honest");
       eline("12<15madgoat12>15 now thats the REAL shit.");
       eline(nnn+" 11myn 14[10mn@barbi.whore.infinet.net14]15 has joined #addons");
       eline("12<15myn12>15 oh shit! khaled's here");
       eline("12<15myn12>15 I've always wanted to try this...");
       eline("12<15myn12>15 99999999999999999999999999999999999999 * 999999999999999999999999999999");
       delay = false;
       eline(nnn+" 16hellish 15SignOff: #addons 14(15conection reset by peer14)");
       delay = true;
       eline(nnn+" 16madgoat 15SignOff: #addons 14(15conection reset by peer14)");
       eline(nnn+" 11madgoat 14[10khaled@mardam.demon.co.uk14]15 has joined #addons");
       eline("12<15madgoat12>15 bloody hell! it gpf'd");
       eline("12<15madgoat12>15 looks like I am taking haltable remotes out of this new ver");
       eline("12<15madgoat12>15 well bye guys, just came on to test haltable remotes");
       eline("12<15madgoat12>15 and since I got a gpf I gotta remove them..");
       eline("12<15madgoat12>15 jakiechan16:15 I'll mail you those pics later");
       eline(nnn+" 15madgoat 14[15khaled@mardam.demon.co.uk14]15 has left #addons 14[]");
       eline("12<15`butane12>15 *yawn*");
       eline("12<15JakieChan12>15 lol khaled was here");
       eline("12<15`butane12>15 yeah hehe...");

       if (random.nextBoolean())
       { 
          eline("12<15JakieChan12>15 shutup jewTANE, I wasn't talking to you."); 
       }
       else 
       { 
          eline("12<15JakieChan12>15 shutup penisTANE, I wasn't talking to you."); 
       }

       if (random.nextBoolean())
       {
          eline(nnn+" 11snert 14[10ircn@snert.accesspro.net14]15 has joined #addons");
          eline("12<15snert12>15 guys! I think I broke the irc habit.");
          eline("12<15snert12>15 I managed to stay off for 3 weeks!  now I spend my time like");
          eline("12<15snert12>15 netsexing bitches on aol 24/7!  its fucking great.");
       }
       else 
       {
          eline(nnn+" 11blue-elf 14[10belf@elfy.owns.my14]15 has joined #addons");
          eline("12<15blue-elf12>15 butane! I made a new mircii addon");
          eline("12<15blue-elf12>15 its basically a little mass unban alias");
          eline("12<15blue-elf12>15 but now it also gives mircii a vrml viewer... ");
       }

       type("man you guys are fucked up.");
       eline("6<15"+me+"6>15 man you guys are fucked up.");
       eline("12<8JakieChan12>15 "+me+", yeah well I'll have khaled porn soon");
       eline("12<15JakieChan12>15 and you won't...");
       type("whatever dude, I think I'm getting off irc now.");
       eline("6<15"+me+"6>15 whatever dude, I think I'm getting off irc now.");
       type("and staying off perm.");
       eline("6<15"+me+"6>15 and staying off perm.");
       type("this is just to damned weird.");
       eline("6<15"+me+"6>15 this is just to damned weird.");
       eline(nnn+" 16"+me+" 15SignOff: #addons 14(15EOF from client14)");
    }

    public void type(String text)
    {
       if (text == null)
       {
          return;
       }

       for (int x = 0; x < text.length(); x++)
       {
          input.setText(text.substring(0, x));
          input.setCaretPosition(x);
          input.repaint();
          try
          {
            Thread.sleep(170 + (random.nextInt() % 100));
          } catch (Exception ex) { }
       }

       input.setText(text);
       input.setCaretPosition(text.length());
       input.repaint();

       try
       {
         Thread.sleep(1000 + (random.nextInt() % 1000));
       } catch (Exception ex) { }

       input.setText("");
       input.repaint();
    }

    public void eline(String text)
    {
       getDisplay().addText("1" + text);

       if (delay)
       {
       try
       {
          Thread.sleep(3800 + (random.nextInt() % 2700));
       }
       catch (Exception ex)
       {
       }
       }
    }

    public void init(ClientWindow _frame)
    {
       frame = _frame;
       frame.addWindowListener(new ClientWindowStuff());

       setLayout(new BorderLayout());

       display   = new WrappedDisplay();
       input     = new ModInputField();
       statusbar = new ModStatusBar(this);

       add(display, BorderLayout.CENTER);

       JPanel space = new JPanel();
       space.setLayout(new BorderLayout());

       space.add(statusbar, BorderLayout.NORTH);
       space.add(input,     BorderLayout.SOUTH);

       space.setOpaque(false);

       add(space, BorderLayout.SOUTH);

       frame.setContentPane(this);
 
       if (showKow) { setTitle("#Floods [+tn]: [PloP]"); }
       else { setTitle(getName()); }

       frame.setIcon(getImageIcon());

       ((ModInputField)input).rehashColorsFoSho();
    }
 
    private class ModInputField extends InputField
    {
       public void rehashColors() { }

       public void rehashColorsFoSho()
       {
          Color temp = Color.black;

          if (showKow) 
          { 
             temp = Color.black; 
          }
          else 
          { 
             temp = Color.gray.brighter(); 
          }

          setForeground(temp);
          setCaretColor(temp.brighter());

          setFont(ClientState.getClientState().getFont("ui.font", ClientDefaults.ui_font));
  
          revalidate();
       }
    }

    private class ModStatusBar extends WindowStatusBar
    {
       public ModStatusBar(StatusWindow parent) 
       {
          super(parent);

          if (background == Color.black)
          {
             contents.setNumberOfLines(2);
          }
          else
          {
             contents.setNumberOfLines(0);
          }
          rehash();
       }

       public void rehashValues() { } // do nothing...
 
       public void rehash()
       {
          if (background == Color.black)
          {
             contents.setLine("10[1603:14am10][15"+me+" 10(15+i10)] [15#addons 10(15+tn10)]", "", 0);
             contents.setLine("10 [15Lag16  210] [15Ops10/16. 15Non10/16. 15Voice10/16.10]", "", 1);
          }
       }

       public void paint (Graphics g)
       {
          g.setColor(background == Color.black ? Color.blue.darker() : Color.black);
          g.fillRect(0, 0, getWidth(), getHeight());

          paintChildren(g);
       }
    }

    public void paint(Graphics g)
    {
       g.setColor(background);
       g.fillRect(0, 0, getWidth(), getHeight());
       paintChildren(g);
    }

   public ImageIcon getImageIcon()
   {
      if (icon == null)
      {
         icon = new ImageIcon(ClientState.getClientState().getResource("jsmall.gif"));
      }

      return icon;
   }

   public boolean isLegalWindow()
   {
      return false;
   }
}
