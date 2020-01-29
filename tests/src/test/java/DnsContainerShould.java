import org.junit.Test;
import org.xbill.DNS.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DnsContainerShould extends ContainerTestBase {
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
        resolver.setPort(super.getMappedUdpPort(53));

        return resolver;
    }
}
