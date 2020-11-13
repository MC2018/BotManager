# BotManager
This is a collection of Discord bots that I have worked on over the years. My first Discord project was in 2017, and was only a single bot. As time grew on, I wanted to have another, but didn't want to have a second project solely dedicated towards it when a lot of the framework would need to be rewritten; with this came about the idea of the BotManager.

This project is on its 3rd or 4th rendition, and a lot of the code is fairly old (MaiDiscordBot and Boteyy_ running code from 2018, as well as SpeedrunBot running code from 2019), so this project is not necessarily a showcase of my best work. A more recent bot that I am more proud of is the GitManager, which is using newer implementation classes to clean up redundant code.

If I could redo this project again, my first prerogative would be to implement a proper database through something like SQL. When I first started this project, reading out of CSV files was the best way I knew how to read and write files, which is why it's used in some of the older bots. As time progressed, I switched to JSON, a much more refined process but still lacking in the integrity, reliability, and efficiency of an indexed database.

Onto a quick summary of all the bots and the development put into them (in chronological order, I think):

* MaiDiscordBot is a gambling bot used by my close friends, and has fun games like group jackpots, as well as a unique game called Harvest which my friend Hyreon created
* Boteyy_ used to be more customized for another server, but now uses MaiDiscordBot for its functionality
* SpeedrunBot connects to the Speedrun.com API to gather information about leaderboards and players' speedruns, additionally using Selenium to parse through generated HTML due to API limitations
* NSFWPolice is a relatively simple bot used to prevent under-aged users from accessing NSFW content on a server
* SuggestionBox allows users to submit suggestions (typically for server changes like new channels, rules, or emotes) through a public forum where everyone can vote on the issues
* BulletBot is essentially a tool for moderators on a server I help out with, giving tools for user information as well as a "dirty word" tracker
* GitManager was developed for a small group of friends on a project, which implements a customizable JIRA-like ticket system in Discord including a time logger, a meeting manager, and a notification system for GitHub which directly links up with users and their created tasks in the guild
