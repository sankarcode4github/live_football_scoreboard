# Live football scoreboard  

## Background

Our Football (Soccer) worldcup scoreboard library is used to show the summary of all the ongoing matches with their current score.

## Requirements
Each match is played between 2 teams, one of them is the home team and the other is the away team.

The startNewMatch api is used to start a new match on the scoreboard with initial score 0-0.

The getSummary api is used to show the current status of the scoreboard. The match with total score more than another match will show up above the other match on the scoreboard. If one match has same score as the other match, then the match which has started more recently will show up above the other on the scoreboard.

The finishMatch api removes a match from the scoreboard

The updateScore api updates the score of an ongoing match. It is possible to rectify the score if the Referee cancels a Goal.

## Asumptions

At a time maximum 5 matches are played at the same time. It may be more and this solution can handle that too. But to be realistic like that happens in the WC, it looks more natural.

It's assumed that these library methods are called serially. Although care has been taken so that the library also perfectly runs in a multithreaded environment.

It's assumed that this program runs on a single computer. It has been tested in a windows 11 environment with i5 processor and 8 GB ram. Although much less configuration can handle it.

The same team can only play in one ongoing match at a time. For example if a match is going on between ARGENTINA and BRAZIL, no other match can show up with any of these teams on the scoreboard.

It is assumed that a when a match starts, there may be a fraction of a second delay for it to show up in the scoreboard. But this delay may not be more than 1 min. Because in general a match finished within 90 minutes. If we consider the delay it may not be more than a few seconds.



## Data Structures used

A HashMap has been used to store the individual matches. Here the keys of this Hashmap are the home team and the away team. So if a match is being played between ARGENTINA and AUSTRALIA, then there will be two keys as String and both will point to this match as the value in the HashMap.

![HashMap](https://github.com/sankarcode4github/live_football_scoreboard/assets/142508542/197fbb61-9e5c-4e97-8632-6b853dbc3baf)


To store the summary a TreeSet has been used.

![TreeSet](https://github.com/sankarcode4github/live_football_scoreboard/assets/142508542/8478967d-5cb0-4b43-b2e4-a55db557694b)

## Design 

TDD is used as practice.

Solid principles have been thought of mostly.

Each class tries to follow single responsibility and just does what it's supposed to do. No more no less.

Interfaces have been used to make the design as loosely couples as possible.

Object creation has been encapsulated whenever possible.

No separate match id has been created. The teams and the start time of a match compositely identify it uniquely.

The comparator may be modified accordingly if the requirement changes.

## Performance

If there are n ongoing matches the getSummary api runs in O(n) time.
All other apis run in O(log n) time.


## How to run the program

The program has been developed in Intellij Idea.

The Main.java is the code to test the Library. Following is an example output

====Before any team scores a goal====

Argentina 0-Australia 0

Uruguay 0-Italy 0

Germany 0-France 0

Spain 0-Brazil 0

Mexico 0-Canada 0

====After new scores are set====

Uruguay 6-Italy 6

Spain 10-Brazil 2

Mexico 0-Canada 5

Argentina 3-Australia 1

Germany 2-France 2

====After Mexico canada match is aborted due to storm====

Uruguay 6-Italy 6

Spain 10-Brazil 2

Argentina 3-Australia 1

Germany 2-France 2

====After Brazil scores 2 more goals====

Spain 10-Brazil 4

Uruguay 6-Italy 6

Argentina 3-Australia 1

Germany 2-France 2


