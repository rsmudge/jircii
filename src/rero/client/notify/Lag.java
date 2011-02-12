package rero.client.notify;

import rero.client.WatchableData;

public class Lag extends WatchableData
{
   protected String lag = "??";
   protected long checkTime;
   
    public String getLag()
   {
      return lag;
   }
 
   public void checkLag()
   {
      lag = "??";
      checkTime = System.currentTimeMillis();      
      fireEvent();
   }

   public void setLag()
   {
      int temp = (int)(System.currentTimeMillis() - checkTime) / 1000;
      if (temp <= 9)
      {
         lag = "0" + temp;
      }
      else
      {
         lag = temp + "";
      }
      fireEvent();
   }
}
