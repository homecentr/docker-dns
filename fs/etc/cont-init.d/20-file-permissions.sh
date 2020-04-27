#!/usr/bin/with-contenv ash

chown -R "$PUID:$PGID" /var/run/named
chown -R "$PUID:$PGID" /config-default