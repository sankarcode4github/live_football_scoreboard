package org.example.repository.impl;

import org.example.exception.ScoreBoardException;
import org.example.model.MatchInProgress;
import org.example.repository.ScoreBoardDataStore;

import java.util.*;

/**
 * All the methods of this class is thread safe
 * <p>
 * This class stores the live scoreboard and the live summary of current Football world cup
 * The key to store each match in the HashMap is the name of the home Team
 * This class also tracks the current teams who are playing now
 * This class also provides a summary
 */
public class FootballWorldcupScoreboard implements ScoreBoardDataStore {

    private final Map<String, MatchInProgress> scoreBoard = new HashMap<>(); //Live match score board
    private final TreeSet<MatchInProgress> summary; //The summary

    private FootballWorldcupScoreboard(Comparator<MatchInProgress> comparator) {
        summary = new TreeSet<>(comparator);
    }

    public static FootballWorldcupScoreboard getScoreBoard(Comparator<MatchInProgress> comparator) {
        return new FootballWorldcupScoreboard(comparator);
    }


    /**
     * Add a new match which has just started
     * O(log N) as The Match has to be put in the TreeSet
     */
    @Override
    public synchronized boolean add(MatchInProgress matchInProgress) {
        if(matchInProgress == null) {
            //Log it so that it may be debugged
            throw new ScoreBoardException("Match may not be null", null);
        }
        String homeTeam = matchInProgress.getHomeTeam();
        if(homeTeam == null) {
            //Log it so that it may be debugged
            return false;
        }
        if(scoreBoard.containsKey(homeTeam)) {
            //Log it so that it may be debugged
            throw new ScoreBoardException("The same team " + homeTeam + " is already playing as a home team, so another match with it is not possible", null);
        }
        if(scoreBoard.containsKey(matchInProgress.getAwayTeam())) {
            //Log it so that it may be debugged
            throw new ScoreBoardException("The same team " + matchInProgress.getAwayTeam() + " is already playing as an away team, so another match with it is not possible", null);
        }

        scoreBoard.put(homeTeam, matchInProgress);
        scoreBoard.put(matchInProgress.getAwayTeam(), matchInProgress);

        summary.add(matchInProgress);
        return true;
    }

    /**
     * Set the new score of an ongoing match after a team scores
     * The summary also changes
     * O(log N) as The Match has to be removed and put in the TreeSet
     */
    @Override
    public synchronized boolean setScore(String homeTeam, int homeScore, int awayScore){
        if(homeTeam == null) {
            //Log it so that it may be debugged
            return false;
        }
        if(!scoreBoard.containsKey(homeTeam)) {
            //Log it so that it may be debugged
            throw new ScoreBoardException("There is no such match going on with this home team "+homeTeam, null);
        }
        MatchInProgress matchInProgress = scoreBoard.get(homeTeam);
        summary.remove(matchInProgress);
        matchInProgress.setScore(homeScore, awayScore);
        summary.add(matchInProgress);
        return true;
    }

    /**
     * O(1)
     *
     */
    @Override
    public synchronized MatchInProgress get(String homeTeam) {
        return scoreBoard.get(homeTeam);
    }

    /**
     * Remove a finished match
     * O(log N) as The Match has to be removed from the TreeSet
     */
    @Override
    public synchronized boolean remove(String homeTeam) {
        if(homeTeam == null) {
            //Log it so that it may be debugged
            return false;
        }
        if(!scoreBoard.containsKey(homeTeam)) {
            //Log it so that it may be debugged
            throw new ScoreBoardException("There is no such match going on with this home team "+homeTeam, null);
        }
        MatchInProgress matchInProgress = scoreBoard.get(homeTeam);
        summary.remove(matchInProgress);
        scoreBoard.remove(matchInProgress.getAwayTeam());
        scoreBoard.remove(matchInProgress.getHomeTeam());

        return true;
    }

    /**
     * Get the summary of all the currently ongoing matches
     * O(n) as the TreeSet is iterated
     */
    @Override
    public synchronized List<MatchInProgress> getSummary() {
        List<MatchInProgress> result = new ArrayList<>();
        for(MatchInProgress matchInProgress :summary) {
            result.add(matchInProgress);
        }
        return result;
    }
}
