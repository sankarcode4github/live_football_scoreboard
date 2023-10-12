package org.example.tests;

import org.example.ScoreBoardService;
import org.example.model.MatchComparator;
import org.example.model.MatchInProgress;
import org.example.serviceimpl.ScoreBoardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.example.model.Constants.ARGENTINA;
import static org.example.model.Constants.AUSTRALIA;

public class ScoreBoardServiceTest {
    @Test
    public void testStartNewMatch() {
        ScoreBoardService service = new ScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        List<MatchInProgress> summary = service.getSummary();
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(ARGENTINA, summary.get(0).getHomeTeam());
        Assertions.assertEquals(AUSTRALIA, summary.get(0).getAwayTeam());
    }

    @Test
    public void testFinishMatch() {
        ScoreBoardService service = new ScoreBoardServiceImpl(new MatchComparator());
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        service.startNewMatch(utc, ARGENTINA, AUSTRALIA);
        List<MatchInProgress> summary = service.getSummary();
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(1, summary.size());
        Assertions.assertTrue(service.finishMatch(ARGENTINA));
        summary = service.getSummary();
        Assertions.assertEquals(0, summary.size());
    }
}
