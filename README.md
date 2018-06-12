# RPGChat
My solution to [this](https://bukkit.org/threads/ic-and-ooc-in-game-rpg-chat.473468/) Bukkit plugin request.

## Features
* A new chat system, which distinguishes between global and local chat messages.
* A highly customizable config.
* A permission node for using chat colors.
* [Rolecraft](https://dev.bukkit.org/projects/rolecraft) integration.

## Commands
* `/reloadrpgchat` Reloads config file entries into server memory.
* `/ooc` Sends a global chat message.
* `/whisper` Sends a local chat message with decreased range.
* `/yell` Sends a local chat message with increased range.

## Permissions
* `rpgchat.reload` Grants access to the `/reloadrpgchat` command. (Only OPs have this by default)
* `rpgchat.ooc` Grants access to the `/ooc` command. (All players have this by default)
* `rpgchat.color` Allows color codes in chat messages. (Only OPs have this by default)
* `rpgchat.whisper` Grants access to the `/whisper` command. (All players have this by default)
* `rpgchat.yell` Grants access to the `/yell` command. (All players have this by default)
