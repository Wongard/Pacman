public class CustomTimer {
    int seconds,minutes,ticks;
    CustomTimer()
    {
        reset();
    }
    void reset()
    {
        seconds = 0; minutes = 0; ticks = 0;
    }
    void addTick() {
        ticks++;
        if(ticks < 60) return;
        ticks = 0;
        addSecond();
    }
    void addSecond()
    {
        seconds++;
        if(seconds < 60) return;
        seconds = 0;
        minutes++;
    }
    boolean inkyTrigger()
    {
        if(ticks == 0 && seconds % 10 == 0) return true;
        return false;
    }
    boolean closeWallTrigger()
    {
        if(ticks == 0 && seconds == 3) return true;
        return false;
    }
    String getTime(){if(seconds < 10) return ""+minutes+":0"+seconds;
    return ""+minutes+":"+seconds;}
    int getSeconds(){return seconds;}

    public boolean changeToGameScene() {
        if(ticks == 0 && seconds == 3) return true;
        return false;
    }
}
