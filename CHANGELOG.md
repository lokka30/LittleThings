# LittleThings Release Changelog

Sorted in **descending** order.

## v2.3.0
* [ProfliX](https://github.com/ProfliX) added a new module: `MobSilent`!
  * This allows you to set which mobs should be permanently silent when they spawn in after the module is configured.
  * This module only runs on MC 1.9+, older versions don't have the required code to run it.
* [ProfliX](https://github.com/ProfliX) and I have made various improvements to the code - this plugin laid dormant whilst my programming ability improved since the previous update.

# Old LittleThings Changelog

Sorted in **ascending** order.

## v1.0.0-ALPHA
* Initial Version



## v1.0.1-ALPHA
* Fixed maven shade path for bStats



## v1.1.0
* **IMPORTANT INFORMATION**
  * `config.yml` has been updated, just about everything has changed. If you don't update it, the plugin will break.
  * This update has not been tested, please report any bugs, suggestions or comments to me on the [ArcanePlugins Discord Server](https://discord.io/arcaneplugins).
* Completely overhauled the configuration. Per-world, per-entity, all that stuff. Enjoy :)



## v1.2.0
* **IMPORTANT INFORMATION**
  * `config.yml` has been updated *again*, I apologise. You must delete your old file and reconfigure the resource. :(
  * This update has not been tested, please report any bugs, suggestions or comments to me on the [ArcanePlugins Discord Server](https://discord.io/arcaneplugins).
* Added 'prevent piglin zombification in overworld' feature requested by Oathkeeper
* Added `file-version` to the `config.yml` file.
* Fixed an important bug where entity type lists were always used, ignoring if `all-entities` was enabled.



## v1.2.1
* Fixed default config value where explosion block damage was enabled by default. It is now disabled by default, as originally intended.
  * No config update is necessary, file version has not changed. :)
  
  
  
## v1.2.2
* Code change: `Piglin` to `PiglinAbstract`, might fix a bug (or create them?).



## v1.2.3
* Added `debug` to the config (config file update is optional, file version unchanged).



## v1.2.4
* Switched `EntityTeleportEvent` for piglin stuff to `EntitySpawnEvent`, fixing piglins not being prevented from transforming into zombified piglins. Thanks to Oathkeeper for reporting and testing this version!



## v1.2.5
* Minor change, update not required. Moved logging to MicroLib's MicroLogger. You do not need to install MicroLib, as LittleThings has it shaded inside its .jar file.
  * This was mainly done to make sure MicroLib v2 is working properly. :)
  
  
  
## v1.2.6
* **You need to reset your config!**
* **This update was not tested.**
* Added 'stop crop trampling', kindly suggested by Oathkeeper



## v1.3.0
* **IMPORTANT:** `config.yml` updated! you must update your config, else you will receive errors.
* Added a `/littlethings` command, including a `reload` subcommand :)
    * Includes tab completion.
    * New permissions for the command:
      * `littlethings.command` is required to run the command, given to all users by default
      * `littlethings.reload` is required to run the 'reload' subcommand, given to operators by default
    * Customisable messages are on the to-do list.
* Moved all listeners to separate classes. Far cleaner code now :)
* Fixed StopFarmlandTrampling material error logging using Bukkit's logger instead of MicroLogger



## v1.3.1
* **Optional:** `config.yml` updated to v5.
* Various code improvements. When new modules are added, you no longer need to update your config (so long you don't want to use them).
* Added a 'Stop Portals from Teleporting Players' function, as kindly suggested by Oathkeeper! You can also specify which portals to block.



## v1.4.0
### Notes:
* Required file change: `config.yml` updated to v6.
* This update was **not** tested.

### Changelog:
* Added configurable messages in the config.yml file.
* Added a bypass permission for the *Stop Portal Teleport* module: `littlethings.bypass.stop-portal-teleport`'
* Updated deps



## v2.0.0
### Notes:
* Required file change: `config.yml` updated to version `8`.
* This update was **tested** quite thoroughly, although I recommend you quickly test the modules you will use on a separate server to ensure the plugin is working properly.

### Changelog:
* You may wonder why the sudden jump in the version: I've completely overhauled the plugin behind-the-scenes! :)
  * A new module system has been installed which separates features into different configurations so that you can easily tell LittleThings what to do and what not to.
  * This means that you no longer have a cluttered `config.yml` file. Simply find the module you want to enable or configure, and boom! No need to search through the entire file for the feature you want to configure.
  * This also means that you no longer have to worry about constantly updating your config each time I make a change to a module's configuration. If you don't use the module, you can ignore it. Each module's config has its own file-version system.
  * The plugin is still lightweight as ever: performance has always been and still is a fundamental aspect of this plugin.
  * A bunch of code improvements - I went through every class and tidied most of the mess. There's a little left in the main class which I will work on later (just need to relocate some methods).
  * This took a while as I learnt new things in the process and therefore constantly adapted the code. In addition, my quality standards also slowed it down.
  * I would like to add that no modules have been added in this update, they will be coming in the next updates. This update was to just implement this system early on before more things are added.
* Unfortunately, I had to remove a feature:
  * Removed fire spread prevention. Unfortunately I was not able to find how to fix this feature for a decent period of searching. For now, `/gamerule doFireTick` achieves similar functionality.
  
  
  
## v2.1.0
### Notes:
* No changes to existing configurations. A new module was added and its configuration file will be generated automatically.
* This update was **not tested**, you are advised to test this version before deploying it on a production server.

### Changelog:
* Added a new module 'IronGolemZombieVillager' which provides immunity to zombie villagers if they are being attacked by iron golems. By default, it is configured to only provide immunity if the zombie villager is converting. Thanks to UltimaOath for suggesting this feature!



## v2.1.1
### Notes:
* No configuration changes.
* This update was tested prior to release.

### Changelog:
* Fixed the DaylightCombustion module - thank @Gadse and @StealingDaPenta for helping me out [here](https://www.spigotmc.org/threads/stop-mobs-from-burning-in-daylight.481610/#post-4046516), and @Noiverre for reporting that it was broken (and their patience!).

***

## v2.2.0
### Notes:
* No `config.yml` changes.
* This update was tested prior to release.

### Changelog:
**New: Dragon Egg Break Module!**
* This module allows users to break the dragon egg using their bare hands.
* Thanks to @Oathkeeper for requesting this :)

**New: Falling Blocks Module!**
* Developed by Hugo5551 and requested by @Oathkeeper, this module can make any block fall like sand!
* **I would not recommend using this module** as it could cause significant damage to the worlds you enable it on. Regardless, enable it if you want to play around with it. Be careful :)
* Chests have been force-disabled although other containers such as Shulkers are functional.

**New: Plant Growth Module!**
* Another request by @Oathkeeper! This allows you to stop plants and turtle eggs from growing.

***

## v2.2.1
### Notes:
* No file changes.
* This update was tested prior to release.

### Changelog:
**Bug Fix**
* Fixed armor stands module checking the wrong config. Arms and base plate settings will work now :)