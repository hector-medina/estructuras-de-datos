package uoc.ds.pr.util;

import uoc.ds.pr.SportEvents4Club;

public class LevelHelper {
    public static SportEvents4Club.Level getLevel(int num){
        SportEvents4Club.Level level = null;
        if(num >= 15){
            level = SportEvents4Club.Level.LEGEND;
        }
        if(num < 15){
            level = SportEvents4Club.Level.MASTER;
        }
        if(num < 10){
            level = SportEvents4Club.Level.EXPERT;
        }
        if(num < 5){
            level = SportEvents4Club.Level.PRO;
        }
        if(num < 2){
            level = SportEvents4Club.Level.ROOKIE;
        }
        return level;
    }
}
