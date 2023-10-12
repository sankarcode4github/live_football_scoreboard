package org.example.tests;

import org.example.ScoreBoardService;
import org.example.exception.ScoreBoardException;
import org.example.model.MatchComparator;
import org.example.model.MatchInProgress;
import org.example.serviceimpl.FootballWCScoreBoardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.example.model.Constants.*;

public class ScoreBoardDataStoreServiceTest {
    /**
     * Start the match between ARGENTINA and AUSTRALIA and see if it really is started
     */
    @Test
    public void testStartNewMatch() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        List<MatchInProgress> summary = service.getSummary();
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(ARGENTINA, summary.get(0).getHomeTeam());
        Assertions.assertEquals(AUSTRALIA, summary.get(0).getAwayTeam());
    }

    /**
     * Team may not be null
     */
    @Test
    public void testStartNewMatchNullTeam() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Assertions.assertThrows(ScoreBoardException.class, () -> service.startNewMatch(utc, null, AUSTRALIA),
                "The team may not be null");
    }

    /**
     * Start time may not be null
     */
    @Test
    public void testStartNewMatchNullStartTime() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = null;
        Assertions.assertThrows(ScoreBoardException.class, () -> service.startNewMatch(utc, ARGENTINA, AUSTRALIA),
                "The start time may not be null");
    }

    /**
     *  Finish the match between ARGENTINA and AUSTRALIA and see if it really is removed
     */
    @Test
    public void testFinishMatch() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        List<MatchInProgress> summary = service.getSummary();
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(1, summary.size());
        Assertions.assertTrue(service.finishMatch(ARGENTINA));
        summary = service.getSummary();
        Assertions.assertEquals(0, summary.size());
    }

    /**
     * No match with home team Mexico is going on now
     */
    @Test
    public void testFinishMatchInvalidHomeTeam() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Assertions.assertFalse(service.finishMatch(null));
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        Assertions.assertThrows(ScoreBoardException.class, () -> service.finishMatch(MEXICO),
                "This HomeTeam is not playing any ongoing match");
    }

    /**
     * No match with home team Mexico is going on now
     */
    @Test
    public void testUpdateScoreMatchInvalidHomeTeam() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Assertions.assertFalse(service.updateScore(null, 4,5));
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        Assertions.assertThrows(ScoreBoardException.class, () -> service.updateScore(MEXICO, 4, 5),
                "This HomeTeam is not playing any ongoing match");
    }

    /**
     * Update score to Argentina 3 - Australia 1 and test whether it is really updated
     */
    @Test
    public void testUpdateScoreMatch() {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        List<MatchInProgress> summary = service.getSummary();
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(0, summary.get(0).getHomeTeamScore());
        Assertions.assertEquals(0, summary.get(0).getAwayTeamScore());
        Assertions.assertEquals(1, summary.size());
        service.updateScore(ARGENTINA, 3, 1);
        summary = service.getSummary();
        Assertions.assertEquals(3, summary.get(0).getHomeTeamScore());
        Assertions.assertEquals(1, summary.get(0).getAwayTeamScore());
    }

    /**     *
     * When the goals are same, start time will decide the order
     * Later the start time, earlier it will show up in the list
     *
     * Following is the starting order with current scores
     * a. Mexico 0 - Canada 0   Started 1st
     * b. Spain 0 - Brazil 0   Started 2nd
     * c. Germany 0 - France 0   Started 3rd
     * d. Uruguay 0 - Italy 0   Started 4th
     * e. Argentina 0 - Australia 0   Started 5th/last
     *
     * Following is the summary
     * 1. Argentina 0 - Australia 0
     * 2. Uruguay 0 - Italy 0
     * 3. Germany 0 - France 0
     * 4. Spain 0 - Brazil 0
     * 5. Mexico 0 - Canada 0
     *
     */
    @Test
    public void testCurrentSummary() throws InterruptedException {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, MEXICO, CANADA);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, SPAIN, BRAZIL);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, GERMANY, FRANCE);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, URUGUAY, ITALY);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);

        List<MatchInProgress> summary = service.getSummary();

        Assertions.assertTrue(summary.get(0).getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(summary.get(1).getHomeTeam().equals(URUGUAY));
        Assertions.assertTrue(summary.get(2).getHomeTeam().equals(GERMANY));
        Assertions.assertTrue(summary.get(3).getHomeTeam().equals(SPAIN));
        Assertions.assertTrue(summary.get(4).getHomeTeam().equals(MEXICO));
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
     *
     */
    @Test
    public void testCurrentSummaryAfterScoreSet() throws InterruptedException {
        ScoreBoardService service = new FootballWCScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, MEXICO, CANADA);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, SPAIN, BRAZIL);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, GERMANY, FRANCE);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, URUGUAY, ITALY);
        Thread.sleep(50);

        utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);

        service.updateScore(MEXICO, 0, 5);
        service.updateScore(SPAIN, 10, 2);
        service.updateScore(GERMANY, 2, 2);
        service.updateScore(ITALY, 6, 6);
        service.updateScore(ARGENTINA, 3, 1);

        List<MatchInProgress> summary = service.getSummary();

        Assertions.assertTrue(summary.get(0).getHomeTeam().equals(URUGUAY));
        Assertions.assertTrue(summary.get(1).getHomeTeam().equals(SPAIN));
        Assertions.assertTrue(summary.get(2).getHomeTeam().equals(MEXICO));
        Assertions.assertTrue(summary.get(3).getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(summary.get(4).getHomeTeam().equals(GERMANY));
    }
}
