FROM homecentr/tests-base:1.0.0 as base

FROM alpine:3.11.3

RUN apk add --no-cache docker=19.03.5-r0

COPY --from=base / /

COPY entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]