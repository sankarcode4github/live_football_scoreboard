package org.example.helper;

import org.example.model.MatchInProgress;

import java.util.Comparator;

public class MatchComparator implements Comparator<MatchInProgress> {
    @Override
    public int compare(MatchInProgress o1, MatchInProgress o2) {
        if(o1.equals(o2)) {
            return 0; //consistent with equals
        }
        int score1 = o1.getHomeTeamScore() + o1.getAwayTeamScore();
        int score2 = o2.getHomeTeamScore() + o2.getAwayTeamScore();
        if(score1 != score2) {
            return score2 - score1;
        }
        return o2.getStartTime().compareTo(o1.getStartTime());
    }
}
