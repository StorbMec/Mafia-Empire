# Private server

It barely works. Basic server protocol is figured (see `request-response-flow.md`), and we can enter the game and inject custom data (e.g., arbitrary amount of gold, shop price, opponent attributes, etc).

What is not working:

- Items in shop and gears aren't correct.
  - Gotta use the correct ID for item (see `GameDefinitions` and `ItemIds` or the original `Mafia_en.xml`)
  - Items table isn't made yet (i.e., the starter gears, the shops catalog which dynamically based on player's level, etc)
  - Items price table isn't made too.
- Mission table needs to be made too (i.e., also apply xp and cash scaling)
- Duel doesn't find anyone because there isn't anyone. This apply to ranking search too.
  - Need to generate bots player for ranking and duel entry
- The duel is server-sided, which means the server controls the duel.
  - More specifically, client request fight, server simulates the fight in server and return the winner and the list of turns.
  - This means it's going to be hard to simulate fight based on the original.
  - Attributes, combat stats, and fight are already simulated now, but the accuracy isn't measured.
  - It will be harder if we consider that unique mechanism where fight goes very long and each player goes on rage mode.
- Gangwars opponent data is server-sided, which means we won't be able to produce same opponent attributes from the original game.
- There are no event as this needs to be started manually on server.
- There are no quests as this needs to be created by server. The original quests data don't exist, but there are template of quests in `Mafia_en.xml` (also added to `GameDefinitions`). Other implication is that we can give player of any level task (in contrast, the original game never give us more task after some levels).
- and many more...

As mentioned above, server contains many data of the game, so it's hard to accurately recreate the same experience.

## How to run the game?

- You need to explore the way to retrieve the game client and assets.
- Once you get it, put it in a directory following the `FileRoutes.kt`
- You need Java 24, MongoDB community edition, Adobe AIR runtime (or adobe AIR SDK for dev).

## Credits for:
https://github.com/glennhenry/
