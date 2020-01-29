import com.github.dockerjava.api.model.ExposedPort;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.Paths;

public abstract class ContainerTestBase {
    private static GenericContainer _container;

    @BeforeClass
    public static void setUp() {
        String configDirPath = Paths.get(System.getProperty("user.dir"), "..", "example").normalize().toString();

        System.out.println("Config dir path: " + configDirPath);
        System.out.println("Image: " + System.getProperty("image_tag", "homecentr/dns"));

        System.out.println(Paths.get(System.getProperty("user.dir"), "..", "example").normalize());



        Logger logger = LoggerFactory.getLogger(ContainerTestBase.class);
        logger.info("!!! HELLO !!!");
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);

        _container = new GenericContainer<>(System.getProperty("image_tag", "homecentr/dns"))
                .withFileSystemBind(Paths.get(System.getProperty("user.dir"), "..", "example").normalize().toString(), "/config")
                .waitingFor(Wait.forHealthcheck());

        _container.start();
        _container.followOutput(logConsumer);
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