#!/bin/bash

TABLES=`echo "\d"|psql $1|awk -F\| '{print $2}'`
for t in $TABLES; do
    echo "ALTER TABLE \"$t\" OWNER TO \"$2\""|psql $1
done
