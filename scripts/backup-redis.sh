#!/bin/bash
# Dump the entire redis database to a file and copy it to a backup directory
REDIS="$HOME/golf-stats-data"
DIR=$(date +%Y-%m-%d_%H-%M-%S)  # Include time in directory name
DEST="$REDIS/$DIR"
LOG_FILE="$REDIS/backup.log"

# Create directory for backup
mkdir -p "$DEST" || { echo "Failed to create backup directory"; exit 1; }

# Perform Redis backup
docker exec golf-stats-redis redis-cli SAVE || { echo "Failed to execute Redis SAVE command"; exit 1; }

# Check if dump.rdb exists
if [ ! -f "$REDIS/dump.rdb" ]; then
    echo "Redis dump file not found"
    exit 1
fi

# Copy dump.rdb to backup directory
cp "$REDIS/dump.rdb" "$DEST/dump.rdb" || { echo "Failed to copy Redis dump file"; exit 1; }

# Log backup information
echo "Backup successful: $DEST" >> "$LOG_FILE"

exit 0
