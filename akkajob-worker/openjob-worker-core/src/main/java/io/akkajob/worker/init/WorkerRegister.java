package io.akkajob.worker.init;

import io.akkajob.AkkajobWorker;
import io.akkajob.common.constant.ProtocolTypeEnum;
import io.akkajob.common.request.WorkerStartRequest;
import io.akkajob.common.response.ServerWorkerStartResponse;
import io.akkajob.common.util.FutureUtil;
import io.akkajob.worker.util.WorkerUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerRegister {

    private final AkkajobWorker akkajobWorker;

    public WorkerRegister(AkkajobWorker openjobWorker) {
        this.akkajobWorker = openjobWorker;
    }

    /**
     * Register
     */
    public void register() {
        String serverAddress = WorkerConfig.getServerHost();

        WorkerStartRequest startReq = new WorkerStartRequest();
        startReq.setAddress(WorkerConfig.getWorkerAddress());
        startReq.setAppName(WorkerConfig.getAppName());
        startReq.setProtocolType(ProtocolTypeEnum.AKKA.getType());

        try {
            ServerWorkerStartResponse response = FutureUtil.mustAsk(WorkerUtil.getServerWorkerActor(),
                    startReq, ServerWorkerStartResponse.class, 15000L);
            log.info("Register worker success. serverAddress={} workerAddress={}", serverAddress,
                    WorkerConfig.getWorkerAddress());

            // Do register.
            this.doRegister(response);
        } catch (Throwable e) {
            log.error("Register worker fail. serverAddress={} workerAddress={}", serverAddress, WorkerConfig.getWorkerAddress());
            throw e;
        }
    }

    /**
     * Do register.
     *
     * @param response response
     */
    private void doRegister(ServerWorkerStartResponse response) {
        this.akkajobWorker.getWorkerContext().init(response.getAppId(), response.getWorkerAddressList());
    }
}
