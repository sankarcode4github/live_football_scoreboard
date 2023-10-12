package org.example;

import org.example.model.MatchComparator;
import org.example.model.MatchInProgress;
import org.example.serviceimpl.FootballWCScoreBoardServiceImpl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.example.model.Constants.*;
import static org.example.model.Constants.AUSTRALIA;

public class Main {
    public static void main(String[] args) throws InterruptedException {
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

        System.out.println("====Before any team scores a goal====");
        for(int i=0; i<summary.size(); i++) {
            print(summary, i);
        }

        service.updateScore(MEXICO, 0, 5);
        service.updateScore(SPAIN, 10, 2);
        service.updateScore(GERMANY, 2, 2);
        service.updateScore(ITALY, 6, 6);
        service.updateScore(ARGENTINA, 3, 1);

        summary = service.getSummary();
        System.out.println("\n====After new scores are set====\n");
        for(int i=0; i<summary.size(); i++) {
            print(summary, i);
        }
    }

    private static void print(List<MatchInProgress> summary, int index) {
        System.out.println(summary.get(index).getHomeTeam()+" "+summary.get(index).getHomeTeamScore()+"-"+summary.get(index).getAwayTeam()+" "+summary.get(index).getAwayTeamScore());
    }
}
