#!/bin/bash

set -e

if [ -d "output" ]; then
    echo "Folder 'output' exists. Deleting..."
    rm -rf "output"
    echo "Folder 'output' has been deleted."
else
    echo "Folder 'output' does not exist."
fi

if [ -d "downloads" ]; then
    echo "Folder 'downloads' exists. Deleting..."
    rm -rf "downloads"
    echo "Folder 'downloads' has been deleted."
else
    echo "Folder 'downloads' does not exist."
fi

# GitHub repository information
repoOwner="RaphiMC"
repoName="JavaDowngrader"
releaseTag="v1.1.2"
jarFileName="JavaDowngrader-Standalone-1.1.2.jar"

# Download URL construction
downloadUrl="https://github.com/$repoOwner/$repoName/releases/download/$releaseTag/$jarFileName"

# Download the specific JAR file from GitHub
echo "Downloading $jarFileName from GitHub release $releaseTag"
curl -L -o "$jarFileName" "$downloadUrl"

# URL list file
urlList="url_list.txt"

# Directory to save downloaded files
downloadDir="downloads"

# Create the download directory if it doesn't exist
if [ ! -d "$downloadDir" ]; then
    mkdir "$downloadDir"
fi

# Download each file from the URL list
while IFS= read -r url; do
    # Skip empty lines
    if [ -z "$url" ]; then
        continue
    fi
    
    echo "Downloading $url"
    fileName=$(basename "$url")
    curl -L -o "$downloadDir/$fileName" "$url"
done < "$urlList"

# Change to the download directory
cd "$downloadDir"

outputDir="../output"

# Create the output directory if it doesn't exist
if [ ! -d "$outputDir" ]; then
    mkdir "$outputDir"
fi

# Loop through all .jar files in the download directory
for file in *.jar; do
    # Check if the file name contains the jarFileName
    if [[ "$file" != *"$jarFileName"* ]]; then
        # If the jarFileName is not found in the file name, execute your command
        echo "Processing file: $file"
        # Downgrade every jar file
        java -jar "../$jarFileName" --input "$file" --version 8 --output "$outputDir/downgraded-$file"
    else
        echo "Skipping file: $file"
    fi
done
