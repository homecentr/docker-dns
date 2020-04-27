[![Project status](https://img.shields.io/badge/Project%20status-stable%20%26%20actively%20maintaned-green.svg)](https://github.com/homecentr/docker-dns/graphs/commit-activity) 
[![](https://img.shields.io/github/issues-raw/homecentr/docker-dns/bug?label=open%20bugs)](https://github.com/homecentr/docker-dns/labels/bug) 
[![](https://images.microbadger.com/badges/version/homecentr/cadvisor.svg)](https://hub.docker.com/repository/docker/homecentr/cadvisor)
[![](https://img.shields.io/docker/pulls/homecentr/cadvisor.svg)](https://hub.docker.com/repository/docker/homecentr/cadvisor) 
[![](https://img.shields.io/docker/image-size/homecentr/cadvisor/latest)](https://hub.docker.com/repository/docker/homecentr/cadvisor)

![CI/CD on master](https://github.com/homecentr/docker-dns/workflows/CI/CD%20on%20master/badge.svg)
![Regular Docker image vulnerability scan](https://github.com/homecentr/docker-dns/workflows/Regular%20Docker%20image%20vulnerability%20scan/badge.svg)


# HomeCenter - DNS
[BIND9 DNS server](https://www.isc.org/bind/) packed with the usual bells and whistles.

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

## Configuration
See the `example/named.conf` for a quickstart or [BIND9 documentation](https://kb.isc.org/docs/aa-01031) for full configuration reference. The root configuration file (named.conf) is expected to be at `/config/named.conf`. The container contains a piece of built-in configuration which automatically sets up the statistics endpoint and a zone for health check.

## Environment variables

| Name | Default value | Description |
|------|---------------|-------------|
| PUID | 7077 | UID of the user cadvisor should be running as. The UID must have sufficient rights to read from the Docker socket. |
| PGID | 7077 | GID of the user cadvisor should be running as. You must set the PUID if you want to set the PGID variable. |

## Exposed ports

| Port | Description |
|------|-------------|
| 53/tcp | DNS protocol over TCP |
| 53/udp | DNS protocol over UDP |
| 8888/tcp | Default port for statistics, is not opened by default, must be configured in named.conf |

## Volumes

| Container path | Description |
|--------------|----------------|
| /config | BIND9 configuration files. This location **must be writable** by the PUID/PGID user because BIND9 writes journal files next to the zone files. |

## Security
The container is regularly scanned for vulnerabilities and updated. Further info can be found in the [Security tab](https://github.com/homecentr/docker-dns/security).

### Container user
The container supports privilege drop. Even though the container starts as root, it will use the permissions only to perform the initial set up. The cadvisor process runs as UID/GID provided in the PUID and PGID environment variables.

:warning: Do not change the container user directly using the `user` Docker compose property or using the `--user` argument. This would break the privilege drop logic.