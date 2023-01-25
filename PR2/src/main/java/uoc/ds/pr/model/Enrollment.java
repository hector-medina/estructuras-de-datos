package uoc.ds.pr.model;

import java.util.Comparator;

public class Enrollment implements Comparable<Enrollment> {
    Player player;
    boolean isSubstitute;

    public static final Comparator<Enrollment> CMP_E = (e1, e2)->e1.compareTo(e2);


    public Enrollment(Player player, boolean isSubstitute) {
        this.player = player;
        this.isSubstitute = isSubstitute;
    }

    public Player getPlayer(){
        return player;
    }

    @Override
    public int compareTo(Enrollment o) {
        Player p1 = getPlayer();
        Player p2 = o.getPlayer();
        int cmp = p1.getLevel().compareTo(p2.getLevel());
        return cmp;
    }
}
