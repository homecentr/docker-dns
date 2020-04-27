FROM homecentr/base:2.0.0-alpine

LABEL maintainer="Lukas Holota <me@lholota.com>"

# Copy S6 scripts & default configs
COPY ./fs/ /

RUN apk add --no-cache \
      bind=9.14.8-r5 \
      libcap=2.27-r0 \
      shadow=4.7-r1 && \
    # Grant the named process to open a well-known port (1-1024) which normally requires root permissions
    setcap 'cap_net_bind_service=+ep' /usr/sbin/named

HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=3 CMD [ "nslookup", "ns1.bind9-healthcheck", "127.0.0.1" ]

# Config directory
VOLUME "/config"

# DNS protocol
EXPOSE 53/tcp 53/udp

# Dynamic updates from DHCP server
EXPOSE 953/tcp

# Default statistics port, not opened by default, must be configured
EXPOSE 8888/tcp

ENTRYPOINT ["/init"]