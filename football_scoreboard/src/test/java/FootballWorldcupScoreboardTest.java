import org.example.exception.ScoreBoardException;
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
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
        Assertions.assertFalse(repo.add(ARGENTINA,null));
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
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
        Assertions.assertFalse(repo.add(null, matchInProgress));
    }

    /**
     * If the home team is already playing we may not add a new match with the same home team
     */
    @Test
    public void testHomeTeamIsAlreadyPlaying() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
        repo.add(ARGENTINA, matchInProgress);

        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, SPAIN);

        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(ARGENTINA, matchInProgress1),
                "The same home team may not play in more than one matches at the same time");

    }

    /**
     * The same away team may not play in more than one matches at the same time
     */
    @Test
    public void awayTeamIsAlreadyPlaying() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
        repo.add(ARGENTINA, matchInProgress);

        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, AUSTRALIA);

        MatchInProgress matchInProgress1 = new MatchInProgress(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(SPAIN, matchInProgress1),
                "The same away team may not play in more than one matches at the same time");
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
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
        repo.add(ARGENTINA, matchInProgress);
        Assertions.assertNotNull(repo.get(ARGENTINA));
    }

    /**
     * When matches are goal less, start time should decide the order
     * More recently the match has started, earlier in the order it will show up
     * @throws InterruptedException
     */
    @Test
    public void testSummaryChangesAfterAddingNewLiveMatches() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
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
     * @throws InterruptedException
     */
    @Test
    public void testSummaryChangesAfterScoreChanges() throws InterruptedException {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, MEXICO);
        teams.put(AWAYTEAM, CANADA);
        MatchInProgress matchInProgress = new MatchInProgress(utc, teams);
        FootballWorldcupScoreboard repo = new FootballWorldcupScoreboard();
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
}
