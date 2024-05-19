package io.akkajob.worker.actor;

import akka.actor.AbstractActor;
import io.akkajob.common.actor.BaseActor;
import io.akkajob.common.response.Result;
import io.akkajob.common.response.WorkerResponse;
import io.akkajob.worker.context.JobContext;
import io.akkajob.worker.request.*;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
public class TaskContainerActor extends BaseActor {
    private static final ThreadPoolExecutor CONTAINER_EXECUTOR;

    static {
        CONTAINER_EXECUTOR = new ThreadPoolExecutor(
                2,
                2,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> new Thread(r, "Openjob-container-executor"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        CONTAINER_EXECUTOR.allowCoreThreadTimeOut(true);
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MasterStartContainerRequest.class, this::handleStartContainer)
                .match(MasterBatchStartContainerRequest.class, this::handleBatchStartContainer)
                .match(MasterStopContainerRequest.class, this::handleStopContainer)
                .match(MasterStopInstanceTaskRequest.class, this::handleStopInstanceTask)
                .match(MasterDestroyContainerRequest.class, this::handleDestroyContainer)
                .match(MasterCheckContainerRequest.class, this::handleCheckContainer)
                .build();
    }

    public void handleStartContainer(MasterStartContainerRequest startReq) {
        this.startContainer(startReq);
        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Handle start container
     *
     * @param batchStartReq start request.
     */
    public void handleBatchStartContainer(MasterBatchStartContainerRequest batchStartReq) {
        CONTAINER_EXECUTOR.submit(new ContainerRunnable(batchStartReq));
        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Handle stop container.
     *
     * @param stopReq stop request.
     */
    public void handleStopContainer(MasterStopContainerRequest stopReq) {
        TaskContainer taskContainer = TaskContainerPool.get(stopReq.getJobInstanceId());
        if (Objects.nonNull(taskContainer)) {
            taskContainer.stop(stopReq.getStopType());
        }

        WorkerResponse workerResponse = new WorkerResponse();
        workerResponse.setDeliveryId(stopReq.getDeliveryId());
        getSender().tell(Result.success(workerResponse), getSelf());
    }

    /**
     * Handle stop instance task
     *
     * @param request request
     */
    public void handleStopInstanceTask(MasterStopInstanceTaskRequest request) {
        TaskContainer taskContainer = TaskContainerPool.get(request.getJobInstanceId());
        if (Objects.nonNull(taskContainer)) {
            taskContainer.stopTask(request.getTaskId());
        }

        WorkerResponse workerResponse = new WorkerResponse();
        workerResponse.setDeliveryId(request.getDeliveryId());
        getSender().tell(Result.success(workerResponse), getSelf());
    }

    /**
     * Handle destroy container.
     *
     * @param destroyReq destroy request
     */
    public void handleDestroyContainer(MasterDestroyContainerRequest destroyReq) {
        TaskContainer taskContainer = TaskContainerPool.get(destroyReq.getJobInstanceId());
        if (Objects.nonNull(taskContainer)) {
            taskContainer.destroy();
        }

        WorkerResponse workerResponse = new WorkerResponse();
        workerResponse.setDeliveryId(destroyReq.getDeliveryId());
        getSender().tell(Result.success(workerResponse), getSelf());
    }

    /**
     * Handle check container
     *
     * @param checkRequest checkRequest
     */
    public void handleCheckContainer(MasterCheckContainerRequest checkRequest) {
        Optional.ofNullable(TaskContainerPool.get(checkRequest.getJobInstanceId()))
                .orElseThrow(() -> new RuntimeException(String.format("Task container is not exist!instanceId=%s", checkRequest.getJobInstanceId())));

        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Handle start container.
     *
     * @param startReq start container.
     */
    private void startContainer(MasterStartContainerRequest startReq) {
        JobContext jobContext = new JobContext();
        jobContext.setShardingId(startReq.getShardingId());
        jobContext.setShardingParam(startReq.getShardingParam());
        jobContext.setShardingNum(startReq.getShardingNum());
        jobContext.setJobId(startReq.getJobId());
        jobContext.setJobInstanceId(startReq.getJobInstanceId());
        jobContext.setDispatchVersion(startReq.getDispatchVersion());
        jobContext.setCircleId(startReq.getCircleId());
        jobContext.setTaskId(startReq.getTaskId());
        jobContext.setJobParamType(startReq.getJobParamType());
        jobContext.setJobParams(startReq.getJobParams());
        jobContext.setJobExtendParamsType(startReq.getJobExtendParamsType());
        jobContext.setJobExtendParams(startReq.getJobExtendParams());
        jobContext.setProcessorType(startReq.getProcessorType());
        jobContext.setProcessorInfo(startReq.getProcessorInfo());
        jobContext.setFailRetryInterval(startReq.getFailRetryInterval());
        jobContext.setFailRetryTimes(startReq.getFailRetryTimes());
        jobContext.setExecuteType(startReq.getExecuteType());
        jobContext.setConcurrency(startReq.getConcurrency());
        jobContext.setTimeExpression(startReq.getTimeExpression());
        jobContext.setTimeExpressionType(startReq.getTimeExpressionType());
        jobContext.setTaskName(startReq.getTaskName());
        jobContext.setMasterActorPath(startReq.getMasterAkkaPath());
        if (Objects.nonNull(startReq.getTask())) {
            jobContext.setTask(KryoUtil.deserialize(startReq.getTask()));
        }

        TaskContainer taskContainer = TaskContainerPool.get(startReq.getJobInstanceId(), id -> TaskContainerFactory.create(startReq));
        taskContainer.execute(jobContext);
    }

    private class ContainerRunnable implements Runnable {
        private final MasterBatchStartContainerRequest containerRequest;

        public ContainerRunnable(MasterBatchStartContainerRequest containerRequest) {
            this.containerRequest = containerRequest;
        }

        @Override
        public void run() {
            try {
                for (MasterStartContainerRequest req : this.containerRequest.getStartContainerRequests()) {
                    startContainer(req);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
}
