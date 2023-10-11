package org.example.repository;

import org.example.exception.ScoreBoardException;
import org.example.model.Match;
import org.example.model.MatchComparator;

import java.util.*;

public class InMemoryRepository {

    private Map<String, Match> scoreBoard = new HashMap<>(); //Live match score board
    private Set<String> currentAwayTeams = new HashSet<>();
    private TreeSet<Match> summary = new TreeSet<>(new MatchComparator()); //The summary
    public boolean add(String homeTeam, Match match) {
        if(match == null || homeTeam == null) {
            return false;
        }
        if(scoreBoard.containsKey(homeTeam)) {
            throw new ScoreBoardException("The same home team is already playing, so another match with it is not possible", null);
        }
        if(currentAwayTeams.contains(match.getAwayTeam())) {
            throw new ScoreBoardException("The same away team is already playing, so another match with it is not possible", null);
        }
        scoreBoard.put(homeTeam, match);
        currentAwayTeams.add(match.getAwayTeam());
        summary.add(match);
        return true;
    }

    public boolean setScore(String homeTeam, int homeScore, int awayScore){
        if(!scoreBoard.containsKey(homeTeam)) {
            return false;
        }
        Match match = scoreBoard.get(homeTeam);
        summary.remove(match);
        match.setScore(homeScore, awayScore);
        summary.add(match);
        return true;
    }

    public Match get(String homeTeam) {
        return scoreBoard.get(homeTeam);
    }

    public List<Match> getSummary() {
        List<Match> result = new ArrayList<>();
        for(Match match:summary) {
            result.add(match);
        }
        return result;
    }
}
