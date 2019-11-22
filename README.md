# HomeCenter - DNS
This container contains the [BIND9 DNS server](https://www.isc.org/bind/) along with a metrics endpoint compatible with [Prometheus](https://prometheus.io/).

## Project status

| Project status/quality | Analytics |
|--------|---------------|
| ![](https://snyk.io/test/github/homecentr/docker-dns/badge.svg) | [![](https://img.shields.io/docker/pulls/homecentr/dns.svg)](https://hub.docker.com/repository/docker/homecentr/dns) |
| [![](https://img.shields.io/github/issues-raw/homecentr/docker-dns/bug?label=open%20bugs)](https://github.com/homecentr/docker-dns/labels/bug) | [![](https://images.microbadger.com/badges/version/homecentr/dns.svg)](https://hub.docker.com/repository/docker/homecentr/dns) |
| [![](https://img.shields.io/github/license/homecentr/docker-dns)](https://github.com/homecentr/docker-dns/blob/master/LICENSE) |
| [![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/homecentr/docker-dns/graphs/commit-activity) |
| ![](https://github.com/homecentr/docker-dns/workflows/CI%2FCD%20on%20master/badge.svg) |

## Usage

> Make sure you set container's DNS to **localhost (127.0.0.1)**. The container has a built-in health check which tries to resolve a dummy DNS record. If you do not override the DNS, the health check command will be asking the DNS server the host is using instead of the DNS server running inside of this container and will obviously fail.

```yml
version: "3.7"
services:
  dns:
    build: .
    dns:
      - 127.0.0.1 # important for health check
    restart: unless-stopped
    volumes:
      # the directory MUST be writable, BIND9 writes jnl files next to the configuration
      - "./example:/config:rw"
    ports:
      - "53:53/tcp"
      - "53:53/udp"
      - "9000:9000/tcp"
```

### Configuration
See the `example/named.conf` for a quickstart or [BIND9 documentation](https://kb.isc.org/docs/aa-01031) for full configuration reference. The root configuration file (named.conf) is expected to be at `/config/named.conf`. The container contains a piece of built-in configuration which automatically sets up the statistics endpoint and a zone for health check.

## Exposed ports

| Port | Description |
|------|-------------|
| 53/tcp | DNS protocol over TCP |
| 53/udp | DNS protocol over UDP |
| 9000/tcp | HTTP endpoint with Prometheus metrics |