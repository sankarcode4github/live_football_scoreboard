import org.example.exception.ScoreBoardException;
import org.example.model.Match;
import org.example.repository.InMemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.model.Constants.*;
import static org.example.model.Constants.AUSTRALIA;

public class InMemoryRepositoryTest {
    /**
     * null new match may not be added to the in memory store
     */
    @Test
    public void testNullMatch() {
        InMemoryRepository repo = new InMemoryRepository();
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

        Match match = new Match(utc, teams);
        InMemoryRepository repo = new InMemoryRepository();
        Assertions.assertFalse(repo.add(null,match));
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

        Match match = new Match(utc, teams);
        InMemoryRepository repo = new InMemoryRepository();
        repo.add(ARGENTINA, match);

        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, SPAIN);

        Match match1 = new Match(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(ARGENTINA, match1),
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

        Match match = new Match(utc, teams);
        InMemoryRepository repo = new InMemoryRepository();
        repo.add(ARGENTINA, match);

        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, AUSTRALIA);

        Match match1 = new Match(utc, teams);
        Assertions.assertThrows(ScoreBoardException.class, () -> repo.add(SPAIN, match1),
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

        Match match = new Match(utc, teams);
        InMemoryRepository repo = new InMemoryRepository();
        repo.add(ARGENTINA, match);
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
        Match match = new Match(utc, teams);
        InMemoryRepository repo = new InMemoryRepository();
        repo.add(MEXICO, match);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        match = new Match(utc, teams);
        repo.add(SPAIN, match);
        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        match = new Match(utc, teams);
        repo.add(GERMANY, match);
        List<Match> summary = repo.getSummary();
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
        Match match = new Match(utc, teams);
        InMemoryRepository repo = new InMemoryRepository();
        repo.add(MEXICO, match);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, SPAIN);
        teams.put(AWAYTEAM, BRAZIL);
        match = new Match(utc, teams);
        repo.add(SPAIN, match);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, GERMANY);
        teams.put(AWAYTEAM, FRANCE);
        match = new Match(utc, teams);
        repo.add(GERMANY, match);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, URUGUAY);
        teams.put(AWAYTEAM, ITALY);
        match = new Match(utc, teams);
        repo.add(ITALY, match);

        Thread.sleep(50);
        utc = OffsetDateTime.now(ZoneOffset.UTC);
        teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);
        match = new Match(utc, teams);
        repo.add(ARGENTINA, match);


        repo.setScore(MEXICO, 0, 5);
        repo.setScore(SPAIN, 10, 2);
        repo.setScore(GERMANY, 2, 2);
        repo.setScore(ITALY, 6, 6);
        repo.setScore(ARGENTINA, 3, 1);
        List<Match> summary = repo.getSummary();



        Assertions.assertTrue(summary.get(0).getHomeTeam().equals(URUGUAY));
        Assertions.assertTrue(summary.get(1).getHomeTeam().equals(SPAIN));
        Assertions.assertTrue(summary.get(2).getHomeTeam().equals(MEXICO));
        Assertions.assertTrue(summary.get(3).getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(summary.get(4).getHomeTeam().equals(GERMANY));
    }
}
