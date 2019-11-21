FROM golang as metrics-build

RUN go get github.com/digitalocean/bind_exporter && \
    cd "/go/src/github.com/digitalocean/bind_exporter" && \
    make

RUN ls /go/src/github.com/digitalocean/bind_exporter

FROM alpine

LABEL maintainer="Lukas Holota <me@lholota.com>"

RUN apk upgrade --no-cache && \
    apk add --no-cache bind supervisor && \
    apk add --virtual tmp_pip py-pip && \
    pip install supervisor-stdout && \
    apk del tmp_pip && \
    mkdir /config-default

COPY ./scripts/exit-event-listener.py /usr/local/bin/exit-event-listener
COPY --from=metrics-build /go/src/github.com/digitalocean/bind_exporter/bind_exporter /usr/bin/bind_exporter

COPY ./config/supervisord.conf /etc/supervisord.conf

COPY ./config/named.conf /config-default/
COPY ./config/healthcheck.conf /config-default/
COPY ./config/healthcheck.zone /config-default/
COPY ./config/statistics.conf /config-default/

HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=3 CMD [ "nslookup", "ns1.bind9-healthcheck", "127.0.0.1" ]

# Config directory
VOLUME "/config"

# DNS protocol
EXPOSE 53/tcp 53/udp

# Dynamic updates from DHCP server
EXPOSE 953/tcp

# Prometheus metrics
EXPOSE 9000

ENTRYPOINT ["supervisord", "-n", "--configuration", "/etc/supervisord.conf"]