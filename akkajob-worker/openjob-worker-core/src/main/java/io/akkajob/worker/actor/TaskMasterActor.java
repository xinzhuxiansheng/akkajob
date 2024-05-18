package io.akkajob.worker.actor;

import akka.actor.AbstractActor;
import io.openjob.common.actor.BaseActor;
import io.openjob.common.constant.CommonConstant;
import io.openjob.common.constant.JobInstanceStopEnum;
import io.openjob.common.request.*;
import io.openjob.common.response.Result;
import io.openjob.common.response.WorkerInstanceTaskChildListPullResponse;
import io.openjob.common.response.WorkerInstanceTaskListPullResponse;
import io.openjob.common.response.WorkerResponse;
import io.openjob.worker.dto.JobInstanceDTO;
import io.openjob.worker.master.MapReduceTaskMaster;
import io.openjob.worker.master.TaskMaster;
import io.openjob.worker.master.TaskMasterFactory;
import io.openjob.worker.master.TaskMasterPool;
import io.openjob.worker.request.ContainerBatchTaskStatusRequest;
import io.openjob.worker.request.ProcessorMapTaskRequest;

import java.util.Objects;
import java.util.Optional;

public class TaskMasterActor extends BaseActor {

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ServerSubmitJobInstanceRequest.class, this::submitJobInstance)
                .match(ServerStopJobInstanceRequest.class, this::stopJobInstance)
                .match(ServerStopInstanceTaskRequest.class, this::stopInstanceTask)
                .match(ServerCheckTaskMasterRequest.class, this::checkJobInstance)
                .match(ContainerBatchTaskStatusRequest.class, this::handleContainerTaskStatus)
                .match(ProcessorMapTaskRequest.class, this::handleProcessorMapTask)
                .match(ServerInstanceTaskListPullRequest.class, this::handlePullInstanceTaskList)
                .match(ServerInstanceTaskChildListPullRequest.class, this::handlePullInstanceTaskChildList)
                .build();
    }

    /**
     * Submit job instance.
     *
     * @param submitReq submit request.
     */
    public void submitJobInstance(ServerSubmitJobInstanceRequest submitReq) {
        if (TaskMasterPool.contains(submitReq.getJobInstanceId())) {
            throw new RuntimeException(String.format("Task master is running! jobInstanceId=%s", submitReq.getJobInstanceId()));
        }

        JobInstanceDTO jobInstanceDTO = new JobInstanceDTO();
        jobInstanceDTO.setJobId(submitReq.getJobId());
        jobInstanceDTO.setJobInstanceId(submitReq.getJobInstanceId());
        jobInstanceDTO.setCircleId(submitReq.getCircleId());
        jobInstanceDTO.setDispatchVersion(submitReq.getDispatchVersion());
        jobInstanceDTO.setJobParamType(submitReq.getJobParamType());
        jobInstanceDTO.setJobParams(submitReq.getJobParams());
        jobInstanceDTO.setJobExtendParamsType(submitReq.getJobExtendParamsType());
        jobInstanceDTO.setJobExtendParams(submitReq.getJobExtendParams());
        jobInstanceDTO.setWorkflowId(submitReq.getWorkflowId());
        jobInstanceDTO.setExecuteType(submitReq.getExecuteType());
        jobInstanceDTO.setProcessorType(submitReq.getProcessorType());
        jobInstanceDTO.setProcessorInfo(submitReq.getProcessorInfo());
        jobInstanceDTO.setFailRetryInterval(submitReq.getFailRetryInterval());
        jobInstanceDTO.setFailRetryTimes(submitReq.getFailRetryTimes());
        jobInstanceDTO.setExecuteTimeout(submitReq.getExecuteTimeout());
        jobInstanceDTO.setConcurrency(submitReq.getConcurrency());
        jobInstanceDTO.setTimeExpression(submitReq.getTimeExpression());
        jobInstanceDTO.setTimeExpressionType(submitReq.getTimeExpressionType());
        jobInstanceDTO.setExecuteOnce(Optional.ofNullable(submitReq.getExecuteOnce()).orElse(CommonConstant.NO));

        TaskMaster taskMaster = TaskMasterPool.get(submitReq.getJobInstanceId(), (id) -> TaskMasterFactory.create(jobInstanceDTO, getContext()));
        taskMaster.submit();

        // Result
        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Stop job instance.
     *
     * @param stopReq stop request.
     */
    public void stopJobInstance(ServerStopJobInstanceRequest stopReq) {
        if (!TaskMasterPool.contains(stopReq.getJobInstanceId())) {
            getSender().tell(Result.fail(String.format("Task master is not running and stop failed! jobInstanceId=%s", stopReq.getJobInstanceId())), getSelf());
            return;
        }

        JobInstanceDTO jobInstanceDTO = new JobInstanceDTO();
        TaskMaster taskMaster = TaskMasterPool.get(stopReq.getJobInstanceId(), (id) -> TaskMasterFactory.create(jobInstanceDTO, getContext()));
        taskMaster.stop(JobInstanceStopEnum.NORMAL.getType());
        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Stop instance task
     *
     * @param request request
     */
    public void stopInstanceTask(ServerStopInstanceTaskRequest request) {
        if (!TaskMasterPool.contains(request.getJobInstanceId())) {
            getSender().tell(Result.fail(String.format("Task master is not running and stop task failed! jobInstanceId=%s", request.getJobInstanceId())), getSelf());
            return;
        }

        JobInstanceDTO jobInstanceDTO = new JobInstanceDTO();
        TaskMaster taskMaster = TaskMasterPool.get(request.getJobInstanceId(), (id) -> TaskMasterFactory.create(jobInstanceDTO, getContext()));
        taskMaster.stopTask(request);
        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Check job instance.
     *
     * @param checkRequest check request.
     */
    public void checkJobInstance(ServerCheckTaskMasterRequest checkRequest) {
        if (TaskMasterPool.contains(checkRequest.getJobInstanceId())) {
            getSender().tell(Result.success(new WorkerResponse()), getSelf());
            return;
        }

        getSender().tell(Result.fail("Task master is not exist! instanceId=" + checkRequest.getJobInstanceId()), getSelf());
    }

    /**
     * Hande container task status.
     *
     * @param batchTaskStatusReq status request.
     */
    public void handleContainerTaskStatus(ContainerBatchTaskStatusRequest batchTaskStatusReq) {
        TaskMaster taskMaster = TaskMasterPool.get(batchTaskStatusReq.getJobInstanceId());
        if (Objects.nonNull(taskMaster)) {
            taskMaster.updateStatus(batchTaskStatusReq);
        }

        WorkerResponse workerResponse = new WorkerResponse(batchTaskStatusReq.getDeliveryId());
        getSender().tell(Result.success(workerResponse), getSelf());
    }

    /**
     * Handle map task.
     *
     * @param mapTaskReq map task request.
     */
    public void handleProcessorMapTask(ProcessorMapTaskRequest mapTaskReq) {
        TaskMaster taskMaster = TaskMasterPool.get(mapTaskReq.getJobInstanceId());
        if (taskMaster instanceof MapReduceTaskMaster) {
            ((MapReduceTaskMaster) taskMaster).map(mapTaskReq);
        }

        getSender().tell(Result.success(new WorkerResponse()), getSelf());
    }

    /**
     * Pull instance task list
     *
     * @param request request
     */
    public void handlePullInstanceTaskList(ServerInstanceTaskListPullRequest request) {
        TaskMaster taskMaster = TaskMasterPool.get(request.getJobInstanceId());
        if (Objects.isNull(taskMaster)) {
            getSender().tell(Result.success(new WorkerInstanceTaskListPullResponse()), getSelf());
            return;
        }

        WorkerInstanceTaskListPullResponse response = taskMaster.pullInstanceTaskList(request);
        getSender().tell(Result.success(response), getSelf());
    }

    /**
     * Handle pull instance task child list
     *
     * @param request request
     */
    public void handlePullInstanceTaskChildList(ServerInstanceTaskChildListPullRequest request) {
        TaskMaster taskMaster = TaskMasterPool.get(request.getJobInstanceId());
        if (Objects.isNull(taskMaster)) {
            getSender().tell(Result.success(new WorkerInstanceTaskChildListPullResponse()), getSelf());
            return;
        }

        WorkerInstanceTaskChildListPullResponse response = taskMaster.pullInstanceTaskChildList(request);
        getSender().tell(Result.success(response), getSelf());
    }
}
