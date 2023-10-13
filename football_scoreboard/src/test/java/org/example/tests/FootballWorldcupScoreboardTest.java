package org.example.tests;

import org.example.exception.ScoreBoardException;
import org.example.helper.MatchComparator;
import org.example.model.MatchInProgress;
import org.example.repository.impl.FootballWorldcupScoreboard;
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
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(null),
                "Match may not be null");
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
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);
        Assertions.assertTrue(repo.setScore(MEXICO, 3, 2));
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.setScore(AUSTRALIA, 3, 2),
                "No game is currently going on where Australia is the home team"); //No game is currently going on where Australia is the home team

    }

    /**
     * A home team which is not playing is passed
     * A home team which is null is passed
     */
    @Test
    public void testWrongHomeTeamPassed() {
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.setScore(MEXICO, 3,1),
                "No match is going on with this home team ");
        Assertions.assertFalse(repo.setScore(null, 3,1));
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.remove(MEXICO),
                "No match is going on with this home team ");
        Assertions.assertFalse(repo.remove(null));

    }
    /**
     * If the home team is already playing we may not add a new match with the same home team ARGENTINA
     * The same home team ARGENTINA may not play with away teams AUSTRALIA and SPAIN at the same time
     */
    @Test
    public void testHomeTeamIsAlreadyPlaying() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC); //OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);

        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, SPAIN);

        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(matchInProgress1),
                "The same home team may not play in more than one matches at the same time");

    }

    /**
     * The same away team AUSTRALIA may not play in more than one matches at the same time
     * The same away team AUSTRALIA may not play with home teams ARGENTINA and SPAIN at the same time
     */
    @Test
    public void testAwayTeamIsAlreadyPlaying() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);//OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);

        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(matchInProgress1),
                "The same away team may not play in more than one matches at the same time");
    }


    /**
     * Mexico is trying to play as home team and away team in two different matches at the same time
     * This should not be allowed
     *
     */
    @Test
    public void testAwayTeamIsAlreadyPlayingAsHomeTeam() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, MEXICO);
        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);

        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(matchInProgress1),
                "The same home team may not play in another match as away team at the same time");
    }

    /**
     * Test to check whether the get method of the Repository is working fine
     * The same match can be obtained by using both home team and away team as key
     */
    @Test
    public void testGetMatch() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC); //OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);
        Assertions.assertNotNull(repo.get(ARGENTINA)); //The match is correctly stored
        Assertions.assertEquals(AUSTRALIA, repo.get(ARGENTINA).getAwayTeam()); //The away team is correct
        Assertions.assertNotNull(repo.get(AUSTRALIA)); //The smae match is obtained by away team as key
        Assertions.assertEquals(AUSTRALIA, repo.get(AUSTRALIA).getAwayTeam());
        Assertions.assertNull(repo.get(SPAIN)); //This match was never added
    }

    /**
     * This method tests the following
     * If a match is deleted
     * 1)The match itself is deleted
     * 2)The playing teams are also deleted
     * Now the earlier home team can play a match with a different away team
     * Now the earlier away team can play a match with a different home team
     *
     */
    @Test
    public void testRemoveMatch() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC); //OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());

        repo.add(matchInProgress);

        Assertions.assertNotNull(repo.get(ARGENTINA)); //Match added properly
        Assertions.assertEquals(AUSTRALIA, repo.get(ARGENTINA).getAwayTeam()); //Away Team assigned properly


        repo.remove(ARGENTINA); //remove by using home team ARGENTINA as key
        Assertions.assertNull(repo.get(ARGENTINA)); //Match removed
        Assertions.assertNull(repo.get(AUSTRALIA)); //Match removed

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, MEXICO);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress); //Now a different match with the same home team "ARGENTINA" added

        Assertions.assertNotNull(repo.get(ARGENTINA));
        Assertions.assertEquals(MEXICO, repo.get(ARGENTINA).getAwayTeam());


        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, BRAZIL);
        teams.put(AWAYTEAM, AUSTRALIA);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress); //Now a different match with the same away team "AUSTRALIA" added

        Assertions.assertNotNull(repo.get(BRAZIL));
        Assertions.assertEquals(AUSTRALIA, repo.get(BRAZIL).getAwayTeam());

        repo.remove(AUSTRALIA); //remove using the away team AUSTRALIA as key
        Assertions.assertNull(repo.get(AUSTRALIA)); //Match removed
        Assertions.assertNull(repo.get(BRAZIL)); //Match removed
    }

    /**
     * When matches are goal less or the result is same, start time should decide the order
     * More recently the match has started, earlier in the order it will show up
     * <p>
     * Matches started as follows
     * MEXICO 0 CANADA 0
     * SPAIN 0 BRAZIL 0
     * GERMANY 0 FRANCE 0
     * <p>
     * Summary should return as follows
     * GERMANY 0 FRANCE 0
     * SPAIN 0 BRAZIL 0
     * MEXICO 0 CANADA 0
     * <p>
     *
     */
    @Test
    public void testSummaryChangesAfterAddingNewLiveMatches() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);
        List<MatchInProgress> summary = repo.getSummary();
        Assertions.assertEquals(GERMANY, summary.get(0).getHomeTeam());
        Assertions.assertEquals(SPAIN, summary.get(1).getHomeTeam());
        Assertions.assertEquals(MEXICO, summary.get(2).getHomeTeam());
    }

    /**
     * When matches have different number of total goal scored, this number will decide the order
     * More is the number of goals, earlier it will come in the order.
     * When the goals are same, start time will decide the order
     * <p>
     * Following is the starting order with current scores
     * a. Mexico 0 - Canada 5   Started 1st
     * b. Spain 10 - Brazil 2   Started 2nd
     * c. Germany 2 - France 2   Started 3rd
     * d. Uruguay 6 - Italy 6   Started 4th
     * e. Argentina 3 - Australia 1   Started 5th/last
     * <p>
     * Following is the summary
     * 1. Uruguay 6 - Italy 6
     * 2. Spain 10 - Brazil 2
     * 3. Mexico 0 - Canada 5
     * 4. Argentina 3 - Australia 1
     * 5. Germany 2 - France 2
     *
     */
    @Test
    public void testSummaryChangesAfterScoreChanges() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, URUGUAY);
        teams.put(AWAYTEAM, ITALY);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);


        repo.setScore(MEXICO, 0, 5);
        repo.setScore(SPAIN, 10, 2);
        repo.setScore(GERMANY, 2, 2);
        repo.setScore(ITALY, 6, 6);
        repo.setScore(ARGENTINA, 3, 1);
        List<MatchInProgress> summary = repo.getSummary();



        Assertions.assertEquals(URUGUAY, summary.get(0).getHomeTeam());
        Assertions.assertEquals(SPAIN, summary.get(1).getHomeTeam());
        Assertions.assertEquals(MEXICO, summary.get(2).getHomeTeam());
        Assertions.assertEquals(ARGENTINA, summary.get(3).getHomeTeam());
        Assertions.assertEquals(GERMANY, summary.get(4).getHomeTeam());
    }

    /**
     * Following method tests the summary after a match with Mexico is deleted
     * 1. Uruguay 6 - Italy 6
     * 2. Spain 10 - Brazil 2
     * <p>
     * 3. Argentina 3 - Australia 1
     * 4. Germany 2 - France 2
     *
     */
    @Test
    public void testSummaryChangesAfterAMatchFinishes() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = FootballWorldcupScoreboard.getScoreBoard(new MatchComparator());
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, URUGUAY);
        teams.put(AWAYTEAM, ITALY);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);
        matchInProgress = new MatchInProgress(utc, teams);
        repo.add(matchInProgress);

        repo.setScore(MEXICO, 0, 5);
        repo.setScore(SPAIN, 10, 2);
        repo.setScore(GERMANY, 2, 2);
        repo.setScore(ITALY, 6, 6);
        repo.setScore(ARGENTINA, 3, 1);

        repo.remove(MEXICO); //Match finishes and removed

        List<MatchInProgress> summary = repo.getSummary();
        Assertions.assertEquals(URUGUAY, summary.get(0).getHomeTeam());
        Assertions.assertEquals(SPAIN, summary.get(1).getHomeTeam());
        Assertions.assertEquals(ARGENTINA, summary.get(2).getHomeTeam());
        Assertions.assertEquals(GERMANY, summary.get(3).getHomeTeam());
    }
}
