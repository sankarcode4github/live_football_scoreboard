import org.example.exception.ScoreBoardException;
import org.example.model.Match;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.example.model.Constants.*;

public class MatchTest {

    /**
     * This method tests whether a match can be created properly
     */
    @Test
    public void testFootballMatchCreation() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        Match match = new Match(1, utc, teams);
        Assertions.assertEquals(1, match.getId());
        Assertions.assertTrue(match.getHomeTeam().equals(ARGENTINA));
        Assertions.assertTrue(match.getAwayTeam().equals(AUSTRALIA));
        Assertions.assertTrue(match.getHomeTeamScore()==0);
        Assertions.assertTrue(match.getAwayTeamScore()==0);
        Assertions.assertTrue(match.isInProgress());
        Assertions.assertTrue(utc.equals(match.getStartTime()));
    }

    /**
     * Score may not be set after match is already finished
     */
    @Test
    public void testFootballMatchSetScoreWhenMatchIsFinished() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        Match match = new Match(1, utc, teams);
        match.finish();
        Assertions.assertFalse(match.setScore(3,1));
    }

    /**
     * When a match is in progress, score can always be set
     */
    @Test
    public void testSetScoreOfAMatchInProgress() {
        OffsetDateTime utc = OffsetDateTime.of(2023, 4, 9, 20, 15, 45, 345875000, ZoneOffset.UTC);
        Map<String, String> teams = new HashMap<>();
        teams.put(HOMETEAM, ARGENTINA);
        teams.put(AWAYTEAM, AUSTRALIA);

        Match match = new Match(1, utc, teams);
        Assertions.assertTrue(match.setScore(3,1));
        Assertions.assertTrue(match.getHomeTeamScore()==3);
        Assertions.assertTrue(match.getAwayTeamScore()==1);
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
        Assertions.assertThrows(ScoreBoardException.class, ()-> new Match(1, utc, teams));
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
        Assertions.assertThrows(ScoreBoardException.class, ()-> new Match(1, utc, teams));
    }
}
