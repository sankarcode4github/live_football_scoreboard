package org.example.tests;

import org.example.exception.ScoreBoardException;
import org.example.model.MatchInProgress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.example.model.Constants.*;

public class MatchInProgressTest {

    /**
     * This method tests whether a match can be created properly
     */
    @Test
    public void testFootballMatchCreation() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        Assertions.assertTrue(matchInProgress.getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(matchInProgress.getAwayTeam().equals(AUSTRALIA));
        Assertions.assertTrue(matchInProgress.getHomeTeamScore()==0);
        Assertions.assertTrue(matchInProgress.getAwayTeamScore()==0);
        Assertions.assertTrue(utc.equals(matchInProgress.getStartTime()));
    }

    /**
     * When a match is in progress, score can always be set correctly
     */
    @Test
    public void testSetScoreOfAMatchInProgress() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        Assertions.assertTrue(matchInProgress.setScore(3,1));
        Assertions.assertTrue(matchInProgress.getHomeTeamScore()==3);
        Assertions.assertTrue(matchInProgress.getAwayTeamScore()==1);
    }

    /**
     * HomeTeam and AwayTeam must be different
     */
    @Test
    public void testSameHomeAwayTeam() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, ARGENTINA);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc, teams));
    }

    /**
     * None of the teams may be null
     */
    @Test
    public void testNullHomeOrAwayTeam() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, null);
        teams.put(AWAYTEAM, ARGENTINA);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc, teams));
    }

    @Test
    public void testNullStartTime() {
        OffsetDateTime utc = null;
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, AUSTRALIA);
        teams.put(AWAYTEAM, ARGENTINA);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc, teams));
    }

    /**
     * A football match may not last for more than 6 hours
     * So a start time may not be 6 hours ago or more
     */
    @Test
    public void testVeryOldStartTime() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC).minusHours(7);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, AUSTRALIA);
        teams.put(AWAYTEAM, ARGENTINA);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc, teams));
        OffsetDateTime utc1 = OffsetDateTime.now(ZoneOffset.UTC).minusHours(6);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc1, teams));
    }
}
