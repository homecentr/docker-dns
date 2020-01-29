import com.github.dockerjava.api.model.ExposedPort;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.Paths;

public abstract class ContainerTestBase {
    private static GenericContainer _container;

    @BeforeClass
    public static void setUp() {
        System.out.println(Paths.get(System.getProperty("user.dir"), "..", "example").normalize());

        _container = new GenericContainer<>("homecentr/dns")
                .withFileSystemBind(Paths.get(System.getProperty("user.dir"), "..", "example").normalize().toString(), "/config")
                .waitingFor(Wait.forHealthcheck());

        _container.start();
    }

    @AfterClass
    public static void cleanUp() {
        _container.stop();
        _container.close();
    }

    protected static Integer getMappedUdpPort(int originalPort) {
        return Integer.parseInt(_container.getContainerInfo().getNetworkSettings().getPorts().getBindings().get(ExposedPort.udp(originalPort))[0].getHostPortSpec());
    }
}