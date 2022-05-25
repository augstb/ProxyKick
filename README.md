# ProxyKick (BungeeCord Plugin)

**ProxyKick** is a very basic [BungeeCord](https://www.spigotmc.org/wiki/bungeecord/) plugin which allows Minecraft server moderators (with ```proxykick.kick``` permission node) to **kick** players from the **entire bungee network** with optional custom message.

Initial **localization** strings (English and French) are available, to customize kick messages.

The ```proxykick.bypass``` permission node is available to exempt specific players from beeing kicked (*CONSOLE* and *Rcon* can still kick anyone).

The ```proxykick.reload``` permission node is available to reload configuration and localization data.

**ProxyKick.jar** is available in Releases section.

---

#### Permissions

```proxykick.kick``` : Allows to use the ```/kick``` and ```/proxykick:kick``` commands.

```proxykick.bypass``` : Allows player to be insensitive to ```/kick``` command.

```proxykick.reload``` : Allows player to reload config files using ```/proxykick:reload``` command.

#### Usage

```/kick [player name] (reason)``` : Kick a player with an optional reason.

```/proxykick:kick [player name] (reason)``` : Alias of ```/kick``` command.

```/proxykick:reload``` : Reload configuration and localization files.

---

*Feel free to give me feedback using the Issues tab !*

*Augustin Blanchet*
