FROM node:11.0-stretch 

RUN apt-get update && apt-get install binutils

# FROM alpine:3.3

# LABEL maintainer="Lukas Holota <me@lholota.com>"

# RUN apk add --no-cache bind=9.10.4_p8-r1 && \
#     mkdir /config-default

# COPY ./config/named.conf /config-default/
# COPY ./config/healthcheck.conf /config-default/
# COPY ./config/healthcheck.zone /config-default/

# HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=3 CMD [ "nslookup", "ns1.bind9-healthcheck", "127.0.0.1" ]

# # Config directory
# VOLUME "/config"

# # DNS protocol
# EXPOSE 53/tcp 53/udp

# # Dynamic updates from DHCP server
# EXPOSE 953/tcp

# # Default statistics port, not opened by default, must be configured
# EXPOSE 8888/tcp

# ENTRYPOINT ["/usr/sbin/named", "-f", "-g", "-4", "-c", "/config-default/named.conf"]