# AdvancedChat
A fabric mod that adds a bunch of useful multiplayer chat features to the game.

## Features

### Data-driven chat channels

Allows you to add different channels that players can use to chat in (e.g. per-dimension, local, staff).\
These channels are completely customizable, and using [predicates](https://github.com/Patbox/PredicateAPI/blob/master/BUILTIN.md) you can control exactly which players receive a message.\
Besides that, you can also similarly use [placeholders](https://placeholders.pb4.eu/user/default-placeholders/) to customize the prefix and actionbar text from the chat channel.
___
### Player ignoring

Allows players to ignore others. This will make it so that chat messages from ignored players aren't shown to them.\
Players also won't be able to send private messages to others that have ignored them, showing an error in chat.

___
### Socialspy

Since this mod adds different sorts of private conversations, it also includes a socialspy for staff members with different modes.\
These modes allow you to control which type of messages you want to 'spy' on, as all of them could make chat go very fast.

___
### Mod support

This mod has built-in added support for several other mods.\
For example, vanished players will be taken into account when sending messages and showing receivers.\
It also adds a new placeholder that mods overriding chat styling like styledchat can use, namely `%advancedchat:channelprefix%`

## Commands, Permissions and Configuration

### Commands

- `/advancedchat reload` - Reloads the configuration file and all the chat channels.

- `/chat <channel>` - Lets each player control which chat channel they want to talk in.

- `/ignore <player> | /unignore <player>` - Ignores / Unignores the specified player.

- `/socialspy all | channel | private | none` - Lets you control which types private messages you want to see.

### Permissions

- `advancedchat.command.reload` - Permission to use the reload command.

- `advancedchat.command.socialspy` - Permission to use the socialspy command.

- `advancedchat.bypass.ignore` - Permission to bypass ignore.

- `advancedchat.?` - Any permission added by the data-driven chat channels.

### Configuration

The configuration file can be found at `/config/advancedchat.json`
```json5
{
  // Decides if channels should display action bars.
  "actionbar": true,

  // If set to false, advancedchat will cancel ServerMessageEvents.CHAT_MESSAGE if the message was sent through a chat channel.
  // This may help prevent some mods like discord-bridges sending (private) channel messages in a public discord channel.
  "alwaysTriggerMessageEvent": false,

  // Text to display when hovering over a channel prefix. By default, it shows the names of everyone that received the chat message.
  // This excludes players who are either vanished or in spectator.
  "hoverText": "<dark_aqua>Receivers: <dark_gray>[${receivers}]</dark_gray>",
  "receiver": "<green>${player}</green>",
  "selfPrefix": "<dark_gray>[<aqua>Self</aqua>]</dark_gray> ",

  // Command feedback messages.
  "messages": {
    "switchedChannels": "<dark_aqua>Chat Mode -> <green>${channel}",
    "switchedSocialSpy": "<dark_aqua>Spy Mode -> <green>${mode}",
    "ignored": "<red>${player} is ignoring you.",
    "ignoredPlayer": "<dark_aqua>You are now ignoring <green>${player}",
    "unignoredPlayer": "<dark_aqua>You are no longer ignoring <green>${player}",
    "cannotIgnoreSelf": "<red>You cannot ignore yourself!",
    "alreadyIgnored": "<red>You are already ignoring ${player}!",
    "notAlreadyIgnored": "<red>You aren't ignoring ${player}!",
    "channelNotFound": "<red>Unable to find a channel with name '${name}'!"
  },

  // Socialspy related configuration.
  "socialSpy": {
    // If enabled, logs private messages in the server logs.
    "logPrivateMessages": false,

    // Decides how socialspy messages should be formatted.
    "prefix": "<dark_gray>[<aqua>Spy</aqua>]</dark_gray> ",
    "privateMessage": "<dark_gray>[</dark_gray>${source} <gray>→</gray> ${target}<dark_gray>]</dark_gray> <gray>${message}",
    "channelMessage": "${channel}${sender} <dark_gray>»</dark_gray> ${message}"
  },

  // An array of chat channels.
  // This is where you can add, remove and modify chat channels players can use.
  "channels": [
    {
      // The name of the channel. This is used to identify them, so don't create multiple channels with the same name.
      "name": "world",

      // Allows you to disable the channel without removing it entirely.
      "enabled": true,

      // Flag mainly to determine if a channel is only used by staff members. Mostly used for socialspy and vanish support.
      "isStaff": false,

      // Permission required to join this channel. Without this permission, you also won't be able to see messages in this channel.
      "permission": "channel.world",

      // The (optional) actionbar text and channel prefixes to display to players inside the channel.
      // Both of these support placeholders.
      "actionbar": "<dark_aqua>Chat Mode: <green>%world:name%",
      "prefix": "<dark_gray>[<aqua>%world:name%</aqua>] ",

      // An optional minecraft (JSON) predicate, that allows you to control exactly who gets to view each message.
      // Supports any predicate registered through the Predicate API (https://github.com/Patbox/PredicateAPI/blob/master/BUILTIN.md).
      "canSee": {
        // Vanilla distance predicate except it compares the worlds aswell.
        "type": "advancedchat:distance", 
        "value": {
          // Excludes players further than 256 blocks away from the sender horizontally.
          "horizontal": { 
            "min": 0.0,
            "max": 256.0
          }
        }
      }
    }
  ]
}
```
