package rero.ircfw;

/* InternalDataList
   This almost could and probably should be split up into 3 classes.

   My information - keeps track of the users nickname and User Object
   User List Info - keeps track of user information and provides facilities for updating
   Channel Info   - keeps track of channel information and provides some facilities for updating,
         the bulk of this chore is handled by User List Info though.
 */

import rero.ircfw.interfaces.ChannelDataWatch;

import java.util.*;

public class InternalDataList {
  protected String myNickname = "<Unknown>";
  protected HashMap users = new HashMap(); /* key=<string>, value=<User> */
  protected MyUser myInformation = new MyUser();
  protected HashMap channels = new HashMap(); /* key=<string> value=<Channel> */
  protected HashMap sync = new HashMap(); /* key=<string> value=<ChannelDataWatch> */

  protected HashMap wasOn =
    new HashMap(); /* key=String value=Set hashmap containing nick->channel mappings for all users who quit.  Upon its first access though the value is removed. */

  protected UserMode umode = new UserMode("ohv", "@%+");

  protected HashMap iSupport = new HashMap();
  protected HashMap chanModes = new HashMap(); // key=<string-group name (A,B,C,D)> value = <string with modes>

  public void reset() {
    myNickname = "<Unknown>";
    users = new HashMap();
    myInformation = new MyUser();
    channels = new HashMap();
    umode = new UserMode("ohv", "@%+");
    iSupport = new HashMap();
    chanModes = new HashMap();
  }

  // checks if the mode belongs to one of the CHANMODES group (A, B, C or D)
  // A - modes that always have a param and operate user lists
  // B - modes that always have a param and change setting
  // C - modes that have a param only when set and change setting
  // D - modes that never have a param and change setting
  public boolean isChanGroupMode(String group, char mode) {
    if (!chanModes.containsKey(group)) return false;
    String modes = ((String) chanModes.get(group));
    return modes.indexOf(mode) > -1;
  }

  public void setChanGroupMode(String group, String modes) {
    chanModes.put(group, modes);
  }

  public Set getChannelsFromPriorLife(String nick) {
    Set temp = (Set) wasOn.get(nick);
    wasOn.remove(temp);
    return temp;
  }

  public HashMap getSupportInfo() {
    return iSupport;
  }

  public void addSupportInfo(String key, String value) {
    iSupport.put(key, value);
  }

  public String getMyNick() {
    return myNickname;
  }

  public User getMyUser() {
    return getUser(getMyNick());
  }

  public UserMode getPrefixInfo() {
    return umode;
  }

  public void setPrefixInfo(String modes, String chars) {
    umode = new UserMode(modes, chars);
  }

  public void setMyNick(String n) {
    myNickname = n;
  }

  public MyUser getMyUserInformation() {
    return myInformation;
  }

  public void installChannelWatch(String channel, ChannelDataWatch ch) {
    sync.put(channel.toUpperCase(), ch);
  }

  public ChannelDataWatch getChannelDataWatch(Channel ch) {
    return (ChannelDataWatch) sync.get(ch.getName().toUpperCase());
  }

  /**
   * ********************************************************************
   */

  public LinkedList nickCompleteAll(String pnick, String channel) {
    LinkedList rv = new LinkedList();

    if (getChannel(channel) != null) {
      Set users = getChannel(channel).getAllUsers();

      Iterator i = users.iterator();
      while (i.hasNext()) {
        User temp = (User) i.next();
        if (temp.getNick().length() >= pnick.length()) {
          if (temp.getNick().toLowerCase().substring(0, pnick.length()).equals(pnick.toLowerCase())) {
            rv.addFirst(temp.getNick());
          } else if (temp.getNick().toLowerCase().indexOf(pnick.toLowerCase()) > -1 &&
            (!temp.getNick().toLowerCase().equals(getMyNick().toLowerCase()))) {
            rv.addLast(temp.getNick());
          }
        }
      }
    }

    rv.addLast(pnick);

    return rv;
  }

  public String nickComplete(String pnick, String channel) {
    if (getChannel(channel) == null) {
      return pnick;
    }

    Set users = getChannel(channel).getAllUsers();

    if (isUser(pnick) && users.contains(getUser(pnick))) {
      return pnick;
    }

    String possible = null;

    Iterator i = users.iterator();
    while (i.hasNext()) {
      User temp = (User) i.next();
      if (temp.getNick().length() >= pnick.length()) {
        if (temp.getNick().toLowerCase().substring(0, pnick.length()).equals(pnick.toLowerCase())) {
          return temp.getNick();
        }

        if (temp.getNick().toLowerCase().indexOf(pnick.toLowerCase()) > -1 &&
          (!temp.getNick().toLowerCase().equals(getMyNick().toLowerCase()))) {
          possible = temp.getNick();
        }
      }
    }

    if (possible != null) {
      return possible;
    }

    return pnick;
  }

  public Collection getAllUsers() {
    return users.values();
  }

  public String toString() {
    return "[IDL for " + myNickname + " - users: " + users.size() + ", channels:  " + channels.size() + "]";
  }

  public InternalDataList() {
    /* probably don't need to do anything */
  }

  public boolean isUser(String nickname) {
    return (users.get(nickname) != null);
  }

  public User getUser(String nickname) {
    if (users.get(nickname) == null) {
      users.put(nickname, new User(nickname));
    }
    return (User) users.get(nickname);
  }

  public Set getUsersWithMode(String channel, char mode) {
    return umode.getUsersWithMode(getChannel(channel), mode);
  }

  public void QuitNick(String nickname) {
    Channel temp;

    Set oldchannels = new HashSet();

    Iterator iter = (new LinkedList(getUser(nickname).getChannels())).iterator();
    while (iter.hasNext()) {
      temp = (Channel) iter.next();
      RemoveUser(getUser(nickname), temp);
      oldchannels.add(temp.getName());
    }

    wasOn.put(nickname, oldchannels);

    users.remove(nickname);
  }

  public void PartNick(String nickname, Channel channel) {
    if (getMyNick().equals(nickname)) {
      Iterator iter = channel.getAllUsers().iterator();
      while (iter.hasNext()) {
        User temp = (User) iter.next();
        temp.getChannelData().remove(channel);

        if (temp.getChannelData().size() == 0) {
          users.remove(temp); // remove users who are no longer visible
        }
      }

      channel.getAllUsers().clear();
      channels.remove(channel.getName().toUpperCase());
    }

    RemoveUser(getUser(nickname), channel);
  }

  public void JoinNick(String nickname, String channel) {
    if (getMyNick().equals(nickname)) {
      createChannel(channel);
    }

    getUser(nickname).getChannelData().put(getChannel(channel), new Integer(0));
    getChannel(channel).getAllUsers().add(getUser(nickname));

    if (getChannelDataWatch(getChannel(channel)) != null) {
      getChannelDataWatch(getChannel(channel)).userAdded(getUser(nickname));
    }
  }

  public void ChangeNick(String oldnick, String newnick) {
    User temp = getUser(oldnick);

    // remove the user before the nick is changed in the data structure
    Channel channel;

    Iterator iter = temp.getChannels().iterator();
    while (iter.hasNext()) {
      channel = (Channel) iter.next();
      channel.getAllUsers().remove(temp);
    }

    // change the nick in the data structure
    users.remove(oldnick);

    temp.setNick(newnick);

    users.put(newnick, temp);

    // re add the user to the channel data structure
    iter = temp.getChannels().iterator();
    while (iter.hasNext()) {
      channel = (Channel) iter.next();
      channel.getAllUsers().add(temp);

      if (getChannelDataWatch(channel) != null) {
        getChannelDataWatch(channel).userChanged();
      }
    }

    // change my nickname (if applicable)
    if (oldnick.equals(getMyNick())) {
      setMyNick(newnick);
    }
  }

  public boolean isOn(User user, Channel channel) {
    return user != null && user.getChannels().contains(channel);
  }

  public void AddUser(String nickname, Channel channel) {
    int modes = 0;

    while (umode.isPrefixChar(nickname.charAt(0))) {
      modes = umode.setMode(modes, umode.getModeForDisplay(nickname.charAt(0)));
      nickname = nickname.substring(1, nickname.length());
    }

    User user = getUser(nickname);

    // @Serge: when user joins empty channel and obtains +o automatically, he's already present on this channel,
    // (user.getChannelData().containsKey(channel) returns true on some networks), but without +o,
    // so we need to update user's mode.
    // Fix for: http://jirc.hick.org/cgi-bin/bitch.cgi/view.html?7013225
    if (user.getChannelData().containsKey(channel)) {
      // only put new mode if it's different
      if (((Integer) user.getChannelData().get(channel)).intValue() != modes)
        user.getChannelData().put(channel, new Integer(modes));
    } else {
      user.getChannelData().put(channel, new Integer(modes));
      channel.getAllUsers().add(user);
    }
  }

  public Channel getChannel(String channel) {
    return (Channel) channels.get(channel.toUpperCase());
  }

  public void createChannel(String channel) {
    channels.put(channel.toUpperCase(), new Channel(channel));

    if (getChannelDataWatch(getChannel(channel)) != null) {
      getChannelDataWatch(getChannel(channel)).createChannel(getChannel(channel));
    }
  }

  public void synchronizeUserPreChange(User user, Channel channel) {
    channel.getAllUsers().remove(user);
  }

  public void synchronizeUserPostChange(User user, Channel channel) {
    channel.getAllUsers().add(user);
    if (getChannelDataWatch(channel) != null) {
      getChannelDataWatch(channel).userChanged();
    }
  }

  public void RemoveUser(User user, Channel channel) {
    channel.getAllUsers().remove(user);
    user.getChannelData().remove(channel);

    if (user.getChannelData().size() == 0) {
      users.remove(user);
    }

    if (getChannelDataWatch(channel) != null) {
      getChannelDataWatch(channel).userRemoved(user);
    }
  }
}
