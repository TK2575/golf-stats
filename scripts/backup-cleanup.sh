#!/bin/bash

# remove old backup directories such that
# there are only the 14 most recent folders in $HOME/golf-stats-data
backup_dir="$HOME/golf-stats-data"

# Check if the backup directory exists
if [ ! -d "$backup_dir" ]; then
    echo "Backup directory not found: $backup_dir"
    exit 1
fi

# Change directory to the backup directory
cd "$backup_dir" || exit

# List directories sorted by modification time in ascending order
# and remove the oldest directories leaving only the 14 most recent ones
ls -t1 | tail -n +15 | xargs -d '\n' rm -rf

echo "Old backup directories removed successfully."
