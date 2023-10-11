package org.example.model;

import java.util.Comparator;

public class MatchComparator implements Comparator<Match> {
    @Override
    public int compare(Match o1, Match o2) {
        if(o1.getStartTime().equals(o2.getStartTime()) && o1.getHomeTeam().equals(o2.getHomeTeam()) && o1.getAwayTeam().equals(o2.getAwayTeam())) {
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
