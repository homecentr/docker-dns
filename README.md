# HomeCenter - DNS
This container contains the [BIND9 DNS server](https://www.isc.org/bind/).

## Project status

TODO: Remove snyk, replace with Phonito - link to actions

| Project status/quality | Analytics |
|--------|---------------|
| [![](https://img.shields.io/github/issues-raw/homecentr/docker-dns/bug?label=open%20bugs)](https://github.com/homecentr/docker-dns/labels/bug) | [![](https://img.shields.io/docker/pulls/homecentr/dns.svg)](https://hub.docker.com/repository/docker/homecentr/dns) |
| [![](https://img.shields.io/github/license/homecentr/docker-dns)](https://github.com/homecentr/docker-dns/blob/master/LICENSE) | [![](https://images.microbadger.com/badges/version/homecentr/dns.svg)](https://hub.docker.com/repository/docker/homecentr/dns) |
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

> Please note that the directory with zone files must be writable. BIND creates journal files next to the zone files (this cannot be changed) and will fail if it cannot create them. Please refer to the Security section below for details on container UID/GID.

## Exposed ports

| Port | Description |
|------|-------------|
| 53/tcp | DNS protocol over TCP |
| 53/udp | DNS protocol over UDP |
| 8888/tcp | Default port for statistics, is not opened by default, must be configured in named.conf |

## Security

### Vulnerabilities

The image is periodically (daily) scanned by Phonito.io for possible vulnerabilities. The results are publically available, just check the [output of the daily scan](https://github.com/homecentr/docker-dns/actions?query=workflow%3A%22Regular+Docker+image+vulnerability+scan%22).

### Container user
The container runs as non-root user created during the build with UID and GID **7001**. In case this collides with another image/user, you can rebuild the image using the command below and supply a custom UID and GID.

```bash
docker build . --build-arg UID=9999 --build-arg GID=8888
```

The container will not work if you try to change the UID/GID using the `docker run` because the process would not have access to the required files in the image itself.