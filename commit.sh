#!/bin/bash

# Remove existing Git history
rm -rf .git

# Reinitialize the repository
git init

# Add all files to the new repository
git add .

# Commit the files
git commit -m "Initial commit with fresh history"


# Add the new remote repository URL
git remote add origin git@github.com:xtea/simpletrade.git

# Push to the new repository
git push -u origin main

