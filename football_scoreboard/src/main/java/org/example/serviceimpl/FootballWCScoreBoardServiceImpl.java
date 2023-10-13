package org.example.serviceimpl;

import org.example.ScoreBoardService;
import org.example.model.MatchInProgress;
import org.example.repository.ScoreBoardDataStore;
import org.example.repository.impl.FootballWorldcupScoreboard;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.model.Constants.AWAYTEAM;
import static org.example.model.Constants.HOMETEAM;

public class FootballWCScoreBoardServiceImpl implements ScoreBoardService {

    private final ScoreBoardDataStore scoreBoardDataStore;

    public FootballWCScoreBoardServiceImpl(Comparator<MatchInProgress> comparator) {
        scoreBoardDataStore = FootballWorldcupScoreboard.getScoreBoard(comparator);
    }
    @Override
    public boolean startNewMatch(OffsetDateTime time, String homeTeam, String awayTeam) {
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, homeTeam);
        teams.put(AWAYTEAM, awayTeam);
        MatchInProgress matchInProgress = new MatchInProgress(time, teams);
        return scoreBoardDataStore.add(matchInProgress);
    }

    @Override
    public boolean updateScore(String homeTeam, int homeTeamScore, int awayTeamScore) {
        return scoreBoardDataStore.setScore(homeTeam, homeTeamScore, awayTeamScore);
    }

    @Override
    public boolean finishMatch(String homeTeam) {
        return scoreBoardDataStore.remove(homeTeam);
    }

    @Override
    public List<MatchInProgress> getSummary() {
        return scoreBoardDataStore.getSummary();
    }
}
