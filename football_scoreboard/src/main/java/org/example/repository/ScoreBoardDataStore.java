package org.example.repository;

import org.example.model.MatchInProgress;

import java.util.List;

public interface ScoreBoardDataStore {
    boolean add(MatchInProgress matchInProgress);
    boolean setScore(String homeTeam, int homeScore, int awayScore);
    MatchInProgress get(String homeTeam);
    boolean remove(String homeTeam);
    List<MatchInProgress> getSummary();
}
