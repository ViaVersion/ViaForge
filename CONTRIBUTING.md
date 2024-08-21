# Contributing guidelines for the project

## Setting up a Workspace
ViaFabricPlus uses Gradle, to make sure that it is installed properly you can check [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/ViaVersion/ViaForge`.
2. CD into the local repository.
3. Run `./gradlew build`.
4. Open the folder as a Gradle project in your preferred IDE.
5. Run the mod.

## Add a new feature or fix a bug
1. Create a new branch for your feature/bugfix (e.g. `feature/fix-xyz` or `fix/fix-xyz`)
2. Implement your feature/bugfix and make sure it works correctly
3. Clean your code and make sure it is readable and understandable (e.g. use proper variable names)
4. Use the Google java code style (https://google.github.io/styleguide/javaguide.html) and format your code accordingly
5. Create a pull request and wait for it to be reviewed and merged.
6. You're done, congrats!

## Add support for a new Minecraft version
1. Create a new branch for the new version (e.g. `update/1.20.6`)
2. Create a new `viaforge-mc<version>` folder in the root directory of the project
3. Add a `gradle.properties` file with the `forge_version` set (Available at https://files.minecraftforge.net/)
4. Register it inside the `settings.gradle` file
5. Copy the code from the previous version and update it accordingly

### Build logic
The `build.gradle` file contains the shared build code for all submodules. Build code for only specific versions
of the game can be wrapped with checks using the `versionId` integer which will be parsed from the `forge_version`.

An example would be:
````groovy
if (versionId >= 1_13_2) {
    // We don't need to package mixins into Forge 1.13+ jars, since Forge already has it
    exclude("org/spongepowered/**")
}
````

### Notes
Shared source code is inside the ``src/main/java`` root folder, while version-specific code is inside the ``/viaforge-mc<version>`` folders.