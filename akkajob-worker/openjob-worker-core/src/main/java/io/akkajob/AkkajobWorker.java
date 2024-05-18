package io.akkajob;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.akkajob.worker.config.AkkajobConfig;
import io.akkajob.worker.constant.WorkerConstant;
import io.akkajob.worker.init.WorkerActorSystem;
import io.akkajob.worker.init.WorkerConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AkkajobWorker {

    private final WorkerConfig workerConfig;
    private final WorkerActorSystem workerActorSystem;
    private final WorkerRegister workerRegister;


    public AkkajobWorker(){
        this.workerConfig = new WorkerConfig();
        this.workerActorSystem = new WorkerActorSystem();
    }

    /**
     * Worker 入口方法
     */
    public void init() {

        // Retry interval
        int retryInterval = AkkajobConfig.getInteger(WorkerConstant.WORKER_HEARTBEAT_INTERVAL,
                WorkerConstant.DEFAULT_WORKER_HEARTBEAT_INTERVAL);

        // Initialize executor
        ScheduledThreadPoolExecutor initializeExecutor = new ScheduledThreadPoolExecutor(
                1,
                new ThreadFactoryBuilder().setNameFormat("Akkajob-initialize-thread").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //Initialize continuously until complete
        initializeExecutor.scheduleWithFixedDelay(() -> {
            // Initialize complete
            if (this.doInitialize()) {
                initializeExecutor.shutdown();
                log.info("Akkajob worker initialize complete!");
            }
        }, 0, retryInterval, TimeUnit.SECONDS);
    }

    /**
     * Do initialize
     *
     * @return Boolean
     */
    public synchronized Boolean doInitialize() {
        try {
            // Checker
//            this.workerChecker.init();
//
            // Initialize worker config.
            /*
                请求 master url 获取 server list，随机一个 作为 通信的 server
             */
            this.workerConfig.init();

            // Initialize actor system.
            /*
                创建多个 Dispatcher
             */
            this.workerActorSystem.init();

            // Register worker.
            this.workerRegister.register();

            // Initialize worker heartbeat.
            this.workerHeartbeat.init();

            // Initialize worker initializer
            this.workerInitializer.init();

            // Initialize shutdown.
            this.workerShutdown.init();

            return true;
        } catch (Throwable ex) {
            log.error("Openjob worker initialize failed!", ex);
            return false;
        }
    }

}
