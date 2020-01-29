#!/usr/bin/env ash

wait-for-container "$TESTED_CONTAINER_NAME" 30 || exit 1

# Lookup external domain (test forwarding)
nslookup google.com $TESTED_CONTAINER_IP

# Lookup internally defined domain
OUT=$(nslookup "some-record.test" $TESTED_CONTAINER_IP)
echo $OUT

# Verify the output contains the expected IP address
(echo $OUT | grep "127.0.0.122") || exit 2