package org.example;

import org.example.model.MatchInProgress;

import java.time.OffsetDateTime;
import java.util.List;

public interface ScoreBoardService {

    boolean startNewMatch(OffsetDateTime time, String homeTeam, String awayTeam);
    boolean updateScore(String homeTeam, int homeTeamScore, int awayTeamScore);
    boolean finishMatch(String homeTeam);
    List<MatchInProgress> getSummary();
}
