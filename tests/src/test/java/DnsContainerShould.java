import helpers.DnsImageTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.xbill.DNS.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DnsContainerShould {
    private static final Logger logger = LoggerFactory.getLogger(DnsContainerShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void setUp() {
        _container = new GenericContainerEx<>(new DnsImageTagResolver())
                .withRelativeFileSystemBind(Paths.get("..", "example"), "/config")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(Wait.forHealthcheck());

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void cleanUp() {
        _container.close();
    }

    @Test
    public void resolveExternalZoneViaForwarders() throws UnknownHostException, TextParseException {
        Lookup lookup = new Lookup("google.com", Type.A);
        lookup.setResolver(createResolver());

        Record[] results = lookup.run();

        assertEquals("google.com.", results[0].getName().toString());
        assertEquals(Type.A, results[0].getType());
    }

    @Test
    public void resolveInternallyDefinedZone() throws TextParseException, UnknownHostException {
        Lookup lookup = new Lookup("some-record.test", Type.A);
        lookup.setResolver(createResolver());

        Record[] results = lookup.run();

        assertTrue(results[0] instanceof ARecord);

        ARecord aRecord = (ARecord)results[0];

        assertEquals("some-record.test.", aRecord.getName().toString());
        assertEquals(Type.A, aRecord.getType());

        // The address is configured in the example configs
        assertEquals(InetAddress.getByName("127.0.0.122"), aRecord.getAddress());
    }

    private SimpleResolver createResolver() throws UnknownHostException {
        SimpleResolver resolver = new SimpleResolver("127.0.0.1");
        resolver.setPort(_container.getMappedUdpPort(53));

        return resolver;
    }
}
