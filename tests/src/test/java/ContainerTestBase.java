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
    private static final Logger logger = LoggerFactory.getLogger(ContainerTestBase.class);

    private static GenericContainer _container;

    @BeforeClass
    public static void setUp() {
        String dockerImageTag = System.getProperty("image_tag", "homecentr/dns");
        String configDirPath = Paths.get(System.getProperty("user.dir"), "..", "example").normalize().toString();

        logger.info("Tested Docker image tag : " + dockerImageTag);
        logger.info("Config directory : " + configDirPath);

        _container = new GenericContainer<>(System.getProperty("image_tag", "homecentr/dns"))
                .withFileSystemBind(Paths.get(System.getProperty("user.dir"), "..", "example").normalize().toString(), "/config")
                .waitingFor(Wait.forHealthcheck());

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
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