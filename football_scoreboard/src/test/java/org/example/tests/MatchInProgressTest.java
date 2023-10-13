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
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC); //OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        Assertions.assertEquals(ARGENTINA, matchInProgress.getHomeTeam());
        Assertions.assertEquals(AUSTRALIA, matchInProgress.getAwayTeam());
        Assertions.assertEquals(0, matchInProgress.getHomeTeamScore());
        Assertions.assertEquals(0, matchInProgress.getAwayTeamScore());
        Assertions.assertEquals(utc, matchInProgress.getStartTime());
    }

    /**
     * When a match is in progress, score can always be set correctly
     */
    @Test
    public void testSetScoreOfAMatchInProgress() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC); //OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        Assertions.assertTrue(matchInProgress.setScore(3,1));
        Assertions.assertEquals(3, matchInProgress.getHomeTeamScore());
        Assertions.assertEquals(1, matchInProgress.getAwayTeamScore());
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
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, AUSTRALIA);
        teams.put(AWAYTEAM, ARGENTINA);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(null, teams));
    }

    /**
     *
     * So a start time may not be 1 min ago or more
     */
    @Test
    public void testVeryOldStartTime() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC).minusHours(7);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, AUSTRALIA);
        teams.put(AWAYTEAM, ARGENTINA);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc, teams)); //Too old, 7 hours
        OffsetDateTime utc1 = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(2);
        Assertions.assertThrows(ScoreBoardException.class, ()-> new MatchInProgress(utc1, teams)); //More than 1 minute

        OffsetDateTime utc2 = OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1); //perfectly okay
        MatchInProgress matchInProgress = new MatchInProgress(utc2, teams);
        Assertions.assertEquals(AUSTRALIA, matchInProgress.getHomeTeam());
        Assertions.assertEquals(ARGENTINA, matchInProgress.getAwayTeam());
        Assertions.assertEquals(0, matchInProgress.getHomeTeamScore());
        Assertions.assertEquals(0, matchInProgress.getAwayTeamScore());
        Assertions.assertEquals(utc2, matchInProgress.getStartTime());
    }
}
