package helpers;

import io.homecentr.testcontainers.images.EnvironmentImageTagResolver;

public class DnsImageTagResolver extends EnvironmentImageTagResolver {
    public DnsImageTagResolver() {
        super("homecentr/dns:local");
    }
}
