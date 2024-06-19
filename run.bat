@echo off
setlocal enabledelayedexpansion

rem GitHub repository information
set repoOwner=RaphiMC
set repoName=JavaDowngrader
set releaseTag=v1.1.2
set jarFileName=JavaDowngrader-Standalone-1.1.2.jar

rem Download URL construction
set downloadUrl=https://github.com/%repoOwner%/%repoName%/releases/download/%releaseTag%/%jarFileName%

rem Download the specific JAR file from GitHub
echo Downloading %jarFileName% from GitHub release %releaseTag%
curl -L -o %jarFileName% %downloadUrl%

rem URL list file
set urlList=url_list.txt

rem Directory to save downloaded files
set downloadDir=downloads

rem Create the download directory if it doesn't exist
if not exist %downloadDir% (
    mkdir %downloadDir%
)

rem Download each file from the URL list
for /f "delims=" %%u in (%urlList%) do (
    echo Downloading %%u
    curl -o %downloadDir%\%%~nzu.jar %%u
)

rem Change to the download directory
cd %downloadDir%

set outputDir=output

rem Create the output directory if it doesn't exist
if not exist %outputDir% (
    mkdir %outputDir%
)

rem Loop through all .jar files in the download directory
for %%f in (*.jar) do (
    rem Check if the file name contains the jarFileName
    echo %%f | find /I "%jarFileName%" >nul
    if errorlevel 1 (
        rem If the jarFileName is not found in the file name, execute your command
        echo Processing file: %%f
        rem Downgrade every jar file
		java -jar ../%jarFileName% --input %%f --version 8 --output ../output/downgraded-%%f
    ) else (
        echo Skipping file: %%f
    )
)

endlocal
