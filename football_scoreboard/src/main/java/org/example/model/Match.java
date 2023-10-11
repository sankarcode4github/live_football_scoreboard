package org.example.model;

import org.example.exception.ScoreBoardException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.example.model.Constants.AWAYTEAM;
import static org.example.model.Constants.HOMETEAM;

/**
 * The team which is playing in it's own country or venue, is called the home team
 * The team which is palying in the other country, is the away team
 * I assume that the game is taking place in either of the countries
 * Otherwise this is decided by the toss.
 * One of the teams is home team and the other is away team
 * For the whole match home team and away team may not interchange their status
 */
public class Match {
    private boolean inProgress;
    private final OffsetDateTime startedAt;
    private final Map<String, String> teams; //example: HOMETEAM:Argentina, AWAYTEAM:Australia
    private final Map<String, Integer> currentScore; //example: HOMETEAM:3, AWAYTEAM:1

    public Match(OffsetDateTime startedAt, Map<String, String> teams) {
        if(teams.get(HOMETEAM) == null || teams.get(AWAYTEAM) == null) {
            throw new ScoreBoardException("None of the Teams may be null", null);
        }
        if(teams.get(HOMETEAM).equals(teams.get(AWAYTEAM))) {
            throw new ScoreBoardException("Two teams must be different", null);
        }

        this.inProgress = true;
        this.startedAt = startedAt;
        this.teams = teams;
        this.currentScore = new HashMap<>();
        currentScore.put(HOMETEAM, 0);
        currentScore.put(AWAYTEAM, 0);
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(o.getClass()!=this.getClass()) {
            return false;
        }
        Match other = ((Match)(o));
        if(this.getStartTime().equals(other.getStartTime()) && this.getHomeTeam().equals(other.getHomeTeam()) && this.getAwayTeam().equals(other.getAwayTeam())) {
            return true;
        }
        return false;
    }

    public boolean setScore(int home, int away) {
        if(!inProgress) { //After the match is finished, the score may not be changed
            return false;
        }
        currentScore.put(HOMETEAM, home);
        currentScore.put(AWAYTEAM, away);
        return true;
    }

    public String getHomeTeam() {
        return teams.get(HOMETEAM);
    }

    public String getAwayTeam() {
        return teams.get(AWAYTEAM);
    }

    public int getHomeTeamScore() {
       return currentScore.get(HOMETEAM);
    }

    public int getAwayTeamScore() {
        return currentScore.get(AWAYTEAM);
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public OffsetDateTime getStartTime() {
        return startedAt;
    }

    public void finish() {
        inProgress = false;
    }
}
