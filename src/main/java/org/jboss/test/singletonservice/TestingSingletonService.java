package org.jboss.test.singletonservice;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestingSingletonService implements Service<String> {
    private static final Logger LOGGER = Logger.getLogger(TestingSingletonService.class.getName());

    public static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append("test", "myservice");

    final InjectedValue<ServerEnvironment> env = new InjectedValue<ServerEnvironment>();

    private AtomicBoolean started = new AtomicBoolean(false);
    private String nodeName;

    @Override
    public String getValue() {
        if (!started.get()) {
            throw new IllegalStateException();
        }
        return this.nodeName;
    }

    @Override
    public void stop(StopContext stopContext) {
        if (!started.compareAndSet(true, false)) {
            LOGGER.log(Level.INFO, "The service '" + this.getClass().getName() + "' is not active!");
        } else {
            LOGGER.log(Level.INFO, "Stop service '" + this.getClass().getName() + "' on " + this.getValue());
        }
    }

    @Override
    public void start(StartContext startContext) throws StartException {
        if (!started.compareAndSet(false, true)) {
            throw new StartException("The service is still started!");
        }
        LOGGER.log(Level.INFO, "Start service '" + this.getClass().getName() + "'");

        this.nodeName = this.env.getValue().getNodeName();

        LOGGER.log(Level.INFO, "Service started on " + this.getValue());
    }
}
