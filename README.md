<div align="center">
  <img src="https://raw.githubusercontent.com/ViaVersion/ViaForge/master/src/main/resources/icon.png" width="150">
  <h1>ViaForge</h1>
  <a href="https://files.minecraftforge.net/net/minecraftforge/forge"><img src="https://img.shields.io/badge/Mod%20Loader-Forge-lightyellow?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAYCAYAAACbU/80AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAALySURBVEhLtZZPaBNBFMbzz42NtoII0aCiRQURVGIFEak3URFEEHqUCkVsPZSKoHjQiwjiQb1UFOlBUXoS0XjowYte1JutF6kKUtYIUQpN1k3zz9+bndos3TabNPng4817b+Z7Mzs7sxtoFKVSaV25XO6BD4vF4nodbi4QD+nmfxDbWqlUnsFfUIHYNZ12gUluI32R/Ed4Uof9g0EGPKVdBUS7nLIumMTbdT7BmLNwjHjOSSuklIAfMLhDN6V9Hx7XrvhrEJtyNOdBfBjzBP5RATcKcDSbzQa1zNJA7A7slTYrGmTwd/Z5tUoCcq+V7NL4DVP07Udjh23b/ooLGHSMwVPY/UJRw97V6QDuJYl54Af9RuBpijb2YqbT6RCr3YTIIQTHEEpiTVEndlj6ENuDW5RYNcj3KZHlAqGD8CrsolgHthv9HPYz+2hIH/y3qqobD5RAM0CxPgTfY3drfx/+NPaG9s+oklUgNm6a5oKj2zAQvI6uje0Xn/Yu2hNsUbJQKBj4k1JYI0/uOfGVavBykM/nIxSRfb7taKvVjUrOsqw425CQNrFeUu+wA7BTYvXC82jkcrlEKBTqjkajbcFg0JIQBfIcpQ+xWEy9AyBMcbEV+vzFrtAUTZXQmKsxrfu54DkBisXpLIVKTkT1s3kqVyKRiGyHraLzhTx1qtAGL6D5yHFrgAmcU8+d/YdWFUuwUQxpeRc8Z05neZPvwQEVqB+zcAKdT9ivLEhOz5hhGF9U1i8YdBS+QOgnnIW1ICfmFnfHzkwmU2tb/APRtbCTI/ZYF/KCfPmO6CGtAadgC0Xki+eFYd2tteDxtlMs7dR04bzuUhcauTblsxx1mi5s17Y14PYLs/q9rDTlLHgBLN6TIfps0EOaB4Tl0zwO/dwDM3CES0tuxZrwdVQQvIm57HgKb4i9ZGLfaK8Kh8MHsD0wLkkgN+RGbj7TcReH3wkkMfIzQrMyyXfilUpUgUe/mfgJmrLyGSb3lInNXdmLIBD4Bw72x5r0eQ99AAAAAElFTkSuQmCC"></a>
  <img src="https://img.shields.io/badge/Enviroment-Client-purple">
  <a href="https://discord.gg/viaversion"><img src="https://img.shields.io/discord/316206679014244363?color=0098DB&label=Discord&logo=discord&logoColor=0098DB"></a> <br />
  <a href="https://modrinth.com/mod/viaforge"><img src="https://img.shields.io/badge/dynamic/json?color=158000&label=downloads&prefix=+%20&query=downloads&url=https://api.modrinth.com/v2/project/Z6se2s8f&logo=modrinth"></a>
  <a href="https://curseforge.com/minecraft/mc-mods/viaforge"><img src="https://cf.way2muchnoise.eu/full_418933_downloads.svg"></a>
  <a href="https://github.com/ViaVersion/ViaForge/actions/workflows/build.yml"><img src="https://github.com/ViaVersion/ViaForge/actions/workflows/build.yml/badge.svg?branch=master"></a>  

#### Client-side ViaVersion implementation for MinecraftForge and NeoForge
</div>

## Supported Server versions
- Release (1.0.0 - 1.21.4)
- Beta (b1.0 - b1.8.1)
- Alpha (a1.0.15 - a1.2.6)
- Classic (c0.0.15 - c0.30 including [CPE](https://wiki.vg/Classic_Protocol_Extension))
- April Fools (3D Shareware, 20w14infinite)
- Combat Snapshots (Combat Test 8c)

## Anti cheat integration

ViaForge supports sending a custom payload to transmit player protocol version data to the server. This allows servers to retrieve a player's protocol version via plugin messages, which can be useful for anti-cheat checks and version-dependent logic.
This feature is enabled by default and can be disabled using the `send-connection-details` config option.
For more details on how to use this feature, see the [Player Details Protocol wiki](https://github.com/ViaVersion/ViaVersion/wiki/Player-Details-Protocol).

## How to (Users)

ViaForge can be installed on 1.12.2, 1.16.5, 1.17.1, 1.18.2, 1.19.2, 1.19.4, 1.20.1, 1.20.4, 1.20.6 and 1.21.4 Forge,
and 1.20.1, 1.20.6, 1.21.4 NeoForge client versions. <br>

The ``legacy-1.8`` also supports Minecraft 1.8.9.

Dev builds: https://ci.viaversion.com/view/Platforms/job/ViaForge/

If you encounter any issues, please report them on either:
- [the issue tracker](https://github.com/ViaVersion/ViaForge/issues)
- [the ViaVersion Discord](https://discord.gg/viaversion)

## How to (Developers)

See [this project](https://github.com/ViaVersionMCP/ViaMCP) for a copy-paste solution to include ViaForge into your MCP based project.

### How to build
1. Clone the repository with `git clone`.
2. Run `./gradlew build` in the root directory of the repository.
3. The compiled jar files can be found in `viaforge-mc<version>/build/libs`.

Note: Build scripts are made to be run using Java 21.

## Other ViaVersion Mods / Platforms

ViaBungee - https://hangar.papermc.io/ViaVersion/ViaBungee (ensure Bungee is updated to latest) <br>
ViaSponge - https://modrinth.com/project/viasponge <br>
ViaFabric - https://modrinth.com/mod/viafabric <br>
ViaFabricPlus - https://modrinth.com/mod/viafabricplus (Includes additional modifications to make the experience better) <br>
ViaForge - https://modrinth.com/mod/viaforge/ <br>
ViaProxy (App) - https://github.com/ViaVersion/ViaProxy/#readme <br>
ViaaaS (Proxy) - https://github.com/ViaVersion/ViaaaS#readme <br>

For a more detailed summary see https://viaversion.com
