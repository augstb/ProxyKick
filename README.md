# ProxyKick (BungeeCord Plugin)

**Description :**
***ProxyKick*** is a very basic BungeeCord plugin which allows Minecraft server moderators to kick players from the entire bungee network with optional custom message.

![image](./illustrations/illustration.jpg)

**Features :**
- A *bypass* permission node is available to exempt players from beeing kicked (CONSOLE and Rcon can still kick anyone).
- A *kick* command is available to kick a player from the entire bungee network with an optional custom message.
- A *kickall* command is available to kick all players from the entire bungee network with an optional custom message.
- A *reload* command is available to reload configuration and localization data.
- A *version* command is available to show plugin version and informations.
- A *help* command is available to show the help page, containing command list.
- Initial *localization* strings (English and French) are available, to customize kick messages.

---

#### Permissions

```proxykick.kick``` - Allows to use the ```/kick``` and ```/proxykick:kick``` commands.<br/>
```proxykick.kickall``` - Allows to use the ```/kickall``` and ```/proxykick:kickall``` commands.<br/>
```proxykick.bypass``` - Allows player to be insensitive to ```/kick``` command.<br/>
```proxykick.reload``` - Allows player to reload config files using ```/proxykick:reload``` command.<br/>

#### Usage

```/kick [player name] (reason)``` - Kick player with a message.<br/>
```/kickall (reason)``` - Kick everyone with a message.<br/>
```/proxykick:help``` - Show the help page.<br/>
```/proxykick:reload``` - Reload the configuration files.<br/>
```/proxykick:version``` - Show plugin version.<br/>

---

*Feel free to give me feedback using the Issues tab !*<br/>
*Augustin Blanchet*
