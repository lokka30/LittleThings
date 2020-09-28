# LittleThings Changelog

### v1.0.0-ALPHA
* Initial Version

### v1.0.1-ALPHA
* Fixed maven shade path for bStats

### v1.1.0
* **IMPORTANT INFORMATION**
  * `config.yml` has been updated, just about everything has changed. If you don't update it, the plugin will break.
  * This update has not been tested, please report any bugs, suggestions or comments to me on the [ArcanePlugins Discord Server](https://discord.io/arcaneplugins).
* Completely overhauled the configuration. Per-world, per-entity, all that stuff. Enjoy :)

### v1.2.0
* **IMPORTANT INFORMATION**
  * `config.yml` has been updated *again*, I apologise. You must delete your old file and reconfigure the resource. :(
  * This update has not been tested, please report any bugs, suggestions or comments to me on the [ArcanePlugins Discord Server](https://discord.io/arcaneplugins).
* Added 'prevent piglin zombification in overworld' feature requested by Oathkeeper
* Added `file-version` to the `config.yml` file.
* Fixed an important bug where entity type lists were always used, ignoring if `all-entities` was enabled.

### v1.2.1
* Fixed default config value where explosion block damage was enabled by default. It is now disabled by default, as originally intended.
  * No config update is necessary, file version has not changed. :)