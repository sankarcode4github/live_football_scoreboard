package org.example.tests;

import org.example.exception.ScoreBoardException;
import org.example.model.MatchComparator;
import org.example.model.MatchInProgress;
import org.example.repository.FootballWorldcupScoreboard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.model.Constants.*;
import static org.example.model.Constants.AUSTRALIA;

public class FootballWorldcupScoreboardTest {
    /**
     * null new match may not be added to the in memory store
     */
    @Test
    public void testNullMatch() {
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        Assertions.assertFalse(repo.add(ARGENTINA,null));
    }

    /**
     * Testing whether set score is working fine
     */
    @Test
    public void testSetScore() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(MEXICO, matchInProgress);
        Assertions.assertTrue(repo.setScore(MEXICO, 3, 2));
        Assertions.assertFalse(repo.setScore(AUSTRALIA, 3, 2)); //No game is currently going on
                                                                                  //where Australia is the home team
    }

    /**
     * home team may not be null
     */
    @Test
    public void testNullHomeTeam() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        Assertions.assertFalse(repo.add(null, matchInProgress));
    }

    /**
     * If the home team is already playing we may not add a new match with the same home team ARGENTINA
     * The same home team ARGENTINA may not play with away teams AUSTRALIA and SPAIN at the same time
     */
    @Test
    public void testHomeTeamIsAlreadyPlaying() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(ARGENTINA, matchInProgress);

        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, SPAIN);

        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(ARGENTINA, matchInProgress1),
                "The same home team may not play in more than one matches at the same time");

    }

    /**
     * The same away team AUSTRALIA may not play in more than one matches at the same time
     * The same away team AUSTRALIA may not play with home teams ARGENTINA and SPAIN at the same time
     */
    @Test
    public void testAwayTeamIsAlreadyPlaying() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(ARGENTINA, matchInProgress);

        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(SPAIN, matchInProgress1),
                "The same away team may not play in more than one matches at the same time");
    }


    /**
     * Mexico is trying to play as home team and away team in two different matches at the same time
     * This should not be allowed
     * @throws InterruptedException
     */
    @Test
    public void testAwayTeamIsAlreadyPlayingAsHomeTeam() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(MEXICO, matchInProgress);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, MEXICO);
        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);

        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(SPAIN, matchInProgress1),
                "The same home team may not play in another match as away team at the same time");
    }

    /**
     * Test to check whether the get method of the Repository is working fine
     *
     */
    @Test
    public void testGetMatch() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(ARGENTINA, matchInProgress);
        Assertions.assertNotNull(repo.get(ARGENTINA)); //The match is correctly stored
        Assertions.assertTrue(repo.get(ARGENTINA).getAwayTeam().equals(AUSTRALIA)); //The away team is correct
        Assertions.assertNull(repo.get(SPAIN)); //This match was never added
    }

    /**
     * This method tests the following
     * If a match is deleted
     * 1)The match itself is deleted
     * 2)The playing teams are also deleted
     * Now the earlier home team can play a match with a different away team
     * Now the earlier away team can play a match with a different home team
     */
    @Test
    public void testRemoveMatch() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());

        repo.add(ARGENTINA, matchInProgress);

        Assertions.assertNotNull(repo.get(ARGENTINA)); //Match added properly
        Assertions.assertTrue(repo.get(ARGENTINA).getAwayTeam().equals(AUSTRALIA)); //Away Team added properly


        repo.remove(ARGENTINA);
        Assertions.assertNull(repo.get(ARGENTINA)); //Match removed

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, MEXICO);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(ARGENTINA, matchInProgress); //Now a different match with the same home team "ARGENTINA" added

        Assertions.assertNotNull(repo.get(ARGENTINA));
        Assertions.assertTrue(repo.get(ARGENTINA).getAwayTeam().equals(MEXICO));


        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, BRAZIL);
        teams.put(AWAYTEAM, AUSTRALIA);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(BRAZIL, matchInProgress); //Now a different match with the same away team "AUSTRALIA" added

        Assertions.assertNotNull(repo.get(BRAZIL));
        Assertions.assertTrue(repo.get(BRAZIL).getAwayTeam().equals(AUSTRALIA));
    }

    /**
     * When matches are goal less or the result is same, start time should decide the order
     * More recently the match has started, earlier in the order it will show up
     *
     * Matches started as follows
     * MEXICO 0 CANADA 0
     * SPAIN 0 BRAZIL 0
     * GERMANY 0 FRANCE 0
     *
     * Summary should return as follows
     * GERMANY 0 FRANCE 0
     * SPAIN 0 BRAZIL 0
     * MEXICO 0 CANADA 0
     *
     * @throws InterruptedException
     */
    @Test
    public void testSummaryChangesAfterAddingNewLiveMatches() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(MEXICO, matchInProgress);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(SPAIN, matchInProgress);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(GERMANY, matchInProgress);
        List<MatchInProgress> summary = repo.getSummary();
        Assertions.assertTrue(summary.get(0).getHomeTeam().equals(GERMANY));
        Assertions.assertTrue(summary.get(1).getHomeTeam().equals(SPAIN));
        Assertions.assertTrue(summary.get(2).getHomeTeam().equals(MEXICO));
    }

    /**
     * When matches have different number of total goal scored, this number will decide the order
     * More is the number of goals, earlier it will come in the order.
     * When the goals are same, start time will decide the order
     *
     * Following is the starting order with current scores
     * a. Mexico 0 - Canada 5   Started 1st
     * b. Spain 10 - Brazil 2   Started 2nd
     * c. Germany 2 - France 2   Started 3rd
     * d. Uruguay 6 - Italy 6   Started 4th
     * e. Argentina 3 - Australia 1   Started 5th/last
     *
     * Following is the summary
     * 1. Uruguay 6 - Italy 6
     * 2. Spain 10 - Brazil 2
     * 3. Mexico 0 - Canada 5
     * 4. Argentina 3 - Australia 1
     * 5. Germany 2 - France 2
     * @throws InterruptedException
     */
    @Test
    public void testSummaryChangesAfterScoreChanges() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(MEXICO, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(SPAIN, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(GERMANY, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, URUGUAY);
        teams.put(AWAYTEAM, ITALY);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(ITALY, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(ARGENTINA, matchInProgress);


        repo.setScore(MEXICO, 0, 5);
        repo.setScore(SPAIN, 10, 2);
        repo.setScore(GERMANY, 2, 2);
        repo.setScore(ITALY, 6, 6);
        repo.setScore(ARGENTINA, 3, 1);
        List<MatchInProgress> summary = repo.getSummary();



        Assertions.assertTrue(summary.get(0).getHomeTeam().equals(URUGUAY));
        Assertions.assertTrue(summary.get(1).getHomeTeam().equals(SPAIN));
        Assertions.assertTrue(summary.get(2).getHomeTeam().equals(MEXICO));
        Assertions.assertTrue(summary.get(3).getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(summary.get(4).getHomeTeam().equals(GERMANY));
    }

    /**
     * Following method tests the summary after a match with Mexico is deleted
     * 1. Uruguay 6 - Italy 6
     * 2. Spain 10 - Brazil 2
     *
     * 3. Argentina 3 - Australia 1
     * 4. Germany 2 - France 2
     * @throws InterruptedException
     */
    @Test
    public void testSummaryChangesAfterAMatchFinishes() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard(new MatchComparator());
        repo.add(MEXICO, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(SPAIN, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(GERMANY, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, URUGUAY);
        teams.put(AWAYTEAM, ITALY);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(ITALY, matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(ARGENTINA, matchInProgress);

        repo.setScore(MEXICO, 0, 5);
        repo.setScore(SPAIN, 10, 2);
        repo.setScore(GERMANY, 2, 2);
        repo.setScore(ITALY, 6, 6);
        repo.setScore(ARGENTINA, 3, 1);

        repo.remove(MEXICO); //Match finishes

        List<MatchInProgress> summary = repo.getSummary();
        Assertions.assertTrue(summary.get(0).getHomeTeam().equals(URUGUAY));
        Assertions.assertTrue(summary.get(1).getHomeTeam().equals(SPAIN));
        Assertions.assertTrue(summary.get(2).getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(summary.get(3).getHomeTeam().equals(GERMANY));
    }
}
