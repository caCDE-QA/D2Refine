#!/bin/bash
echo "################## D2Refine Deploymnet Script #################################"
echo "Deploys D2Refine Zip file in OpenRefine's Workspace Directory."
echo "Usually OpenRefine's Workspace directory is:"
echo "$HOME/Library/Application Support/OpenRefine"
echo ""
echo "Usage: "
echo "./deploy.sh [refreshws]"
echo ""
echo "The argumnet 'refreshws' also removes the old projects and workspace json files."
echo "###############################################################################"
echo ""
echo "BEGIN SCRIPT"
dir="$HOME/Library/Application Support/OpenRefine"
extensions="$dir/extensions"
d2RefineDir="$extensions/D2Refine"
d2RefineZip="D2Refine.zip"

if [ -d "$dir" ]; then
	echo "Deploy Directory is $dir"
		if [ "$1" = "refreshws" ] ; then
			echo "Removing old projects and workspace setting files..."
			find "$dir" -type d -name '*.project' -exec rm -r {} +
			rm "$dir/workspace.json"
			rm "$dir/workspace.old.json"
			echo "Removed old projects and workspace setting files!"
		fi
        if [ -d "$extensions" ]; then
		echo "Removing previous D2Refine Plugin from OpenRefine..."
		rm -rf "$d2RefineDir"
		echo "Removed!"
	else
		echo "extensions  folder does not exist, creating..."
		mkdir extensions
		echo "Created the 'extensions' folder for OpenRefine!"
	fi
	if [ -f $d2RefineZip ]; then
		echo "Unzipping $d2RefineZip to '$d2RefineDir'..."
		unzip -o -q $d2RefineZip -d "$d2RefineDir"
		echo "Deployment done!"
	else
		echo "File $d2RefineZip not found"
	fi
else
	echo "Could not find directory '$dir' - Existing deployment..."
fi
echo "END OF SCRIPT"