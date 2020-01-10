package com.hp.sh.expv3;

import com.hp.sh.expv3.match.config.setting.BbmatchIdSetting;
import com.hp.sh.expv3.match.config.setting.BbmatchSetting;
import com.hp.sh.expv3.match.match.core.match.thread.impl.BbOrderSnapshotCreateTriggerThread;
import com.hp.sh.expv3.match.match.core.order.OrderInitializer;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.util.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

//@EnableDiscoveryClient
//@EnableFeignClients({"com.hp.sh.expv3"})
@ComponentScan("com.hp.sh.expv3")
//@SpringBootApplication

@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
public class ExBbMatchApplication extends Application implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ExBbMatchApplication.class);

    @Autowired
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager threadManagerBbMatchImpl;
    @Autowired
    @Qualifier("threadManagerBbMatchedImpl")
    private IThreadManager threadManagerBbMatchedImpl;

    @Autowired
    private OrderInitializer orderInitializer;

    @Value("${rmq.nameSpace}")
    private String nameSpace;
    @Value("${suffix}")
    private String suffix;
    @Autowired
    private BbmatchIdSetting bbmatchIdSetting;
    @Autowired
    private BbmatchSetting bbmatchSetting;

    public static void main(String[] args) {
        Thread.currentThread().setName("BbMatchMain");

        ConfigurableApplicationContext ac = new SpringApplicationBuilder(ExBbMatchApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        ExBbMatchApplication main = ac.getBean(ExBbMatchApplication.class);

        main.run();
    }

    @Override
    protected void onStart() {

        logger.info("nameSpace:{},suffix:{},matchGroupId:{},serverId:{}", nameSpace, suffix, bbmatchSetting.getMatchGroupId(), bbmatchIdSetting.getServerId());

        orderInitializer.start(true);
        threadManagerBbMatchedImpl.waitAllReady();
        threadManagerBbMatchImpl.waitAllReady();
        // 等到所有的 matchedHandler都启动
        logger.info("bb match started!");
    }

    @Override
    protected void onStop() {
//        dubboServiceBeans.shutdown();
        threadManagerBbMatchImpl.safeStop();
        threadManagerBbMatchedImpl.safeStop();
        // 等到所有的 matchedHandler都关闭
        logger.info("bb match stopped!");
    }

    @Autowired
    @Qualifier("commandLineExecutor")
    private Executor executor;

    @Override
    protected void onCommand(String[] commands) {
        String cmdLine = String.join(" ", commands);
        String[] command = StringUtils.split(cmdLine, " ");
        BbOrderSnapshotCreateTriggerThread bbOrderSnapshotCreateTriggerThread = applicationContext.getBean(BbOrderSnapshotCreateTriggerThread.class);
        if (command.length <= 0) {
            return;
        }

        switch (command[0]) {
            case "triggerSnapshot":
                executor.execute(() -> {
                    try {
                        long now = System.currentTimeMillis();
                        bbOrderSnapshotCreateTriggerThread.trigger();
                        logger.info("trigger snapshot,cost:{}", (System.currentTimeMillis() - now));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
                break;
            default:
                String first = command[0];
                if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(first))) {
                    logger.error("cmd {} not bbmatch", first);
                }
                break;
        }
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
