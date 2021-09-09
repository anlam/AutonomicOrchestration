package eu.arrowhead.autonomic.orchestrator;

import java.util.concurrent.Executor;

import org.apache.jena.sys.JenaSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = { CommonConstants.BASE_PACKAGE })
public class OrchestrationRegisterProvider implements ApplicationRunner {
    // =================================================================================================
    // members

    private final Logger logger = LogManager.getLogger(OrchestrationRegisterProvider.class);

    @Autowired
    protected SSLProperties sslProperties;

    // =================================================================================================
    // methods

    // ------------------------------------------------------------------------------------------------
    public static void main(final String[] args) {
        JenaSystem.init();
        SpringApplication.run(OrchestrationRegisterProvider.class, args);
    }

    @Bean
    public Executor monitorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        return executor;
    }

    // -------------------------------------------------------------------------------------------------

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Auto-generated method stub
    }

}
