package com.hp.sh.expv3;

import com.hp.sh.expv3.match.config.setting.PcmatchIdSetting;
import com.hp.sh.expv3.match.config.setting.PcmatchSetting;
import com.hp.sh.expv3.match.match.core.match.thread.impl.PcOrderSnapshotCreateTriggerThread;
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
public class ExMatchApplication extends Application implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ExMatchApplication.class);

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager threadManagerPcMatchImpl;
    @Autowired
    @Qualifier("threadManagerPcMatchedImpl")
    private IThreadManager threadManagerPcMatchedImpl;

    @Autowired
    private OrderInitializer orderInitializer;

    @Value("${rmq.nameSpace}")
    private String nameSpace;
    @Value("${suffix}")
    private String suffix;
    @Autowired
    private PcmatchIdSetting pcmatchIdSetting;
    @Autowired
    private PcmatchSetting pcmatchSetting;

    public static void main(String[] args) {
        Thread.currentThread().setName("PcMatchMain");

        ConfigurableApplicationContext ac = new SpringApplicationBuilder(ExMatchApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        ExMatchApplication main = ac.getBean(ExMatchApplication.class);

        main.run();
    }

    @Override
    protected void onStart() {

        logger.info("nameSpace:{},suffix:{},matchGroupId:{},serverId:{}", nameSpace, suffix, pcmatchSetting.getMatchGroupId(), pcmatchIdSetting.getServerId());

        orderInitializer.start(true);
        threadManagerPcMatchedImpl.waitAllReady();
        threadManagerPcMatchImpl.waitAllReady();
        // 等到所有的 matchedHandler都启动
        logger.info("pc match started!");
    }

    @Override
    protected void onStop() {
//        dubboServiceBeans.shutdown();
        threadManagerPcMatchImpl.safeStop();
        threadManagerPcMatchedImpl.safeStop();
        // 等到所有的 matchedHandler都关闭
        logger.info("pc match stopped!");
    }

    @Autowired
    @Qualifier("commandLineExecutor")
    private Executor executor;

    @Override
    protected void onCommand(String[] commands) {
        String cmdLine = String.join(" ", commands);
        String[] command = StringUtils.split(cmdLine, " ");
        PcOrderSnapshotCreateTriggerThread pcOrderSnapshotCreateTriggerThread = applicationContext.getBean(PcOrderSnapshotCreateTriggerThread.class);
        if (command.length <= 0) {
            return;
        }

        switch (command[0]) {
            case "snapshot":
                executor.execute(() -> {
                    try {
                        long now = System.currentTimeMillis();
                        pcOrderSnapshotCreateTriggerThread.trigger();
                        logger.info("trigger snapshot,cost:{}", (System.currentTimeMillis() - now));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
                break;
            default:
                String first = command[0];
                if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(first))) {
                    logger.error("cmd {} not pcmatch", first);
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
