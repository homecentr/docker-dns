FROM alpine:3.1

LABEL maintainer="Lukas Holota <me@lholota.com>"

# TODO: Scan vulnerabilities to check which packages should be upgraded
#RUN apk add --no-cache bind=9.14.8-r5 && \
RUN mkdir /config-default

COPY ./config/named.conf /config-default/
COPY ./config/healthcheck.conf /config-default/
COPY ./config/healthcheck.zone /config-default/

HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=3 CMD [ "nslookup", "ns1.bind9-healthcheck", "127.0.0.1" ]

# Config directory
VOLUME "/config"

# DNS protocol
EXPOSE 53/tcp 53/udp

# Dynamic updates from DHCP server
EXPOSE 953/tcp

# Default statistics port, not opened by default, must be configured
EXPOSE 8888/tcp

ENTRYPOINT ["/usr/sbin/named", "-f", "-g", "-4", "-c", "/config-default/named.conf"]