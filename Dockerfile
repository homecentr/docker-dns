FROM alpine:3.11.2

ARG uid=7001 
ARG gid=7001

LABEL maintainer="Lukas Holota <me@lholota.com>"
LABEL uid=${uid}
LABEL gid=${gid}

RUN cat /etc/passwd

RUN addgroup -g ${gid} dns && \
    adduser dns -u ${uid} -G dns -D && \
    apk add --no-cache bind=9.14.8-r5 libcap=2.27-r0 && \
    # Prepare directory for pid file so that also non-root user can write into it (# chmod 0757 /var/run/named && \)
    chown -R dns:dns /var/run/named && \
    # Create directory for built-in configs
    mkdir /config-default && \
    # mkdir /config && chown -R dns:dns /config && \
    # Grant the named process to open a well-known port (1-1024) which normally requires root permissions
    setcap 'cap_net_bind_service=+ep' /usr/sbin/named

# Copy default config files to the container
COPY ./config/named.conf /config-default/
COPY ./config/healthcheck.conf /config-default/
COPY ./config/healthcheck.zone /config-default/
 
    # Change ownership of the default config and allow the dns user to read them
RUN chown -R ${uid}:${gid} /config-default && \
    chmod -R 0550 /config-default

USER ${uid}:${gid}

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