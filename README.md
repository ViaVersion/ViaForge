# ViaForge 1.8 git tree

Upstream changes are merged into this branch regularly.

Switch to the `master` branch for the latest changes.

## Installation

ViaVersion 5.0.0+ versions are using Java 17 which is usually not supported by this setup,
therefore a script automatically downloading and downgrading the jar files using [JavaDowngrader](https://github.com/RaphiMC/JavaDowngrader) has been written:

1. Execute either `./run.sh` or `run.bat` depending on your operating system.
2. A `downloads` folder will be created which is ignored by git and should not be pushed.
3. The `output` directory will contain the final jar files and will be automatically used by the build script.

Note: the `url_list.txt` contains all the URLs of the jar files that need to be downloaded.
