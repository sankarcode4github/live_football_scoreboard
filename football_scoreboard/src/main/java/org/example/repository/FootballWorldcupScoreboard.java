package org.example.repository;

import org.example.exception.ScoreBoardException;
import org.example.model.MatchInProgress;

import java.util.*;

/**
 * This class stores the live scoreboard and the live summary
 * The key to store each match in the HashMap is the name of the home Team
 * It also tracks the current away teams who are playing
 */
public class FootballWorldcupScoreboard {

    private final Map<String, MatchInProgress> scoreBoard = new HashMap<>(); //Live match score board
    private final Set<String> currentlyPlayingTeams = new HashSet<>();
    private final TreeSet<MatchInProgress> summary; //The summary

    public FootballWorldcupScoreboard(Comparator<MatchInProgress> comparator) {
        summary = new TreeSet<>(comparator);
    }

    /**
     * Add a new match which has just started
     *
     */
    public boolean add(String homeTeam, MatchInProgress matchInProgress) {
        if(matchInProgress == null || homeTeam == null) {
            return false;
        }
        if(scoreBoard.containsKey(homeTeam)) {
            throw new ScoreBoardException("The same home team is already playing, so another match with it is not possible", null);
        }
        if(currentlyPlayingTeams.contains(matchInProgress.getAwayTeam())) {
            throw new ScoreBoardException("The same team is already playing, so another match with it is not possible", null);
        }
        if(currentlyPlayingTeams.contains(matchInProgress.getHomeTeam())) {
            throw new ScoreBoardException("The same team is already playing, so another match with it is not possible", null);
        }
        scoreBoard.put(homeTeam, matchInProgress);
        currentlyPlayingTeams.add(matchInProgress.getAwayTeam());
        currentlyPlayingTeams.add(matchInProgress.getHomeTeam());
        summary.add(matchInProgress);
        return true;
    }

    /**
     * Set the new score of an ongoing match after a team scores
     * The summary also changes
     *
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

    /**
     * Remove a finished match
     *
     */
    public boolean remove(String homeTeam) {
        if(!scoreBoard.containsKey(homeTeam)) {
            return false;
        }
        MatchInProgress matchInProgress = scoreBoard.get(homeTeam);
        summary.remove(matchInProgress);
        currentlyPlayingTeams.remove(matchInProgress.getAwayTeam());
        currentlyPlayingTeams.remove(matchInProgress.getHomeTeam());
        scoreBoard.remove(homeTeam);
        return true;
    }

    /**
     * Get the summary of all the current matches
     *
     */
    public List<MatchInProgress> getSummary() {
        List<MatchInProgress> result = new ArrayList<>();
        for(MatchInProgress matchInProgress :summary) {
            result.add(matchInProgress);
        }
        return result;
    }
}
