package org.example.repository;

import org.example.exception.ScoreBoardException;
import org.example.model.MatchInProgress;
import org.example.model.MatchComparator;

import java.util.*;

/**
 * This class stores the live scoreboard and the live summary
 * The key to store each match in the HashMap is the name of the home Team
 * It also tracks the current away teams who are playing
 */
public class FootballWorldcupScoreboard {

    private Map<String, MatchInProgress> scoreBoard = new HashMap<>(); //Live match score board
    private Set<String> currentAwayTeams = new HashSet<>();
    private TreeSet<MatchInProgress> summary = new TreeSet<>(new MatchComparator()); //The summary

    /**
     * Add a new match which has just started
     * @param homeTeam
     * @param matchInProgress
     * @return
     */
    public boolean add(String homeTeam, MatchInProgress matchInProgress) {
        if(matchInProgress == null || homeTeam == null) {
            return false;
        }
        if(scoreBoard.containsKey(homeTeam)) {
            throw new ScoreBoardException("The same home team is already playing, so another match with it is not possible", null);
        }
        if(currentAwayTeams.contains(matchInProgress.getAwayTeam())) {
            throw new ScoreBoardException("The same away team is already playing, so another match with it is not possible", null);
        }
        scoreBoard.put(homeTeam, matchInProgress);
        currentAwayTeams.add(matchInProgress.getAwayTeam());
        summary.add(matchInProgress);
        return true;
    }

    /**
     * Set the new score of an ongoing match after a team scores
     * @param homeTeam
     * @param homeScore
     * @param awayScore
     * @return
     */
    public boolean setScore(String homeTeam, int homeScore, int awayScore){
        if(!scoreBoard.containsKey(homeTeam)) {
            return false;
        }
        MatchInProgress matchInProgress = scoreBoard.get(homeTeam);
        summary.remove(matchInProgress);
        matchInProgress.setScore(homeScore, awayScore);
        summary.add(matchInProgress);
        return true;
    }

    public MatchInProgress get(String homeTeam) {
        return scoreBoard.get(homeTeam);
    }

    public List<MatchInProgress> getSummary() {
        List<MatchInProgress> result = new ArrayList<>();
        for(MatchInProgress matchInProgress :summary) {
            result.add(matchInProgress);
        }
        return result;
    }
}
