package io.akkajob.worker.actor;

import akka.actor.ActorSelection;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;
import io.akkajob.common.constant.StatusEnum;
import io.akkajob.common.request.WorkerJobInstanceStatusRequest;
import io.akkajob.common.request.WorkerJobInstanceTaskBatchRequest;
import io.akkajob.common.response.Result;
import io.akkajob.common.response.ServerResponse;
import io.akkajob.common.response.WorkerResponse;
import io.akkajob.worker.request.ContainerBatchTaskStatusRequest;
import io.akkajob.worker.request.MasterDestroyContainerRequest;
import io.akkajob.worker.request.MasterStopContainerRequest;
import io.akkajob.worker.request.MasterStopInstanceTaskRequest;
import io.akkajob.worker.util.WorkerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class WorkerPersistentActor extends AbstractPersistentActorWithAtLeastOnceDelivery {
    private final Integer id;

    public WorkerPersistentActor(Integer id) {
        this.id = id;
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .matchAny(System.out::println)
                .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ContainerBatchTaskStatusRequest.class, this::handleBatchTaskStatus)
                .match(WorkerJobInstanceStatusRequest.class, this::handleJobInstanceStatus)
                .match(WorkerJobInstanceTaskBatchRequest.class, this::handleBatchJobInstanceTask)
                // .match(WorkerDelayStatusRequest.class, this::handleDelayStatus)
                .match(MasterDestroyContainerRequest.class, this::handleDestroyContainer)
                .match(MasterStopContainerRequest.class, this::handleStopContainer)
                .match(MasterStopInstanceTaskRequest.class, this::handleStopInstanceTask)
                .match(Result.class, this::handleResult)
                .build();
    }

    /**
     * Handle batch task status.
     *
     * @param batchRequest batch request.
     */
    public void handleBatchTaskStatus(ContainerBatchTaskStatusRequest batchRequest) {
        ActorSelection masterSelection = getContext().actorSelection(batchRequest.getMasterActorPath());
        deliver(masterSelection, deliveryId -> {
            batchRequest.setDeliveryId(deliveryId);
            return batchRequest;
        });
    }

    /**
     * Handle job instance status request.
     *
     * @param jobInstanceStatusReq status request.
     */
    public void handleJobInstanceStatus(WorkerJobInstanceStatusRequest jobInstanceStatusReq) {
        ActorSelection serverWorkerActor = WorkerUtil.getServerWorkerJobInstanceActor();
        deliver(serverWorkerActor, deliveryId -> {
            jobInstanceStatusReq.setDeliveryId(deliveryId);
            return jobInstanceStatusReq;
        });
    }

    /**
     * Handle job instance task batch request.
     *
     * @param jobInstanceTaskBatchRequest job instance task batch request
     */
    public void handleBatchJobInstanceTask(WorkerJobInstanceTaskBatchRequest jobInstanceTaskBatchRequest) {
        ActorSelection serverWorkerActor = WorkerUtil.getServerWorkerJobInstanceActor();
        deliver(serverWorkerActor, deliveryId -> {
            jobInstanceTaskBatchRequest.setDeliveryId(deliveryId);
            return jobInstanceTaskBatchRequest;
        });
    }

    /**
     * Handle delay status.
     *
     * @param workerDelayStatusRequest workerDelayStatusRequest
     */
//    public void handleDelayStatus(WorkerDelayStatusRequest workerDelayStatusRequest) {
//        ActorSelection serverDelayStatusActor = WorkerUtil.getServerDelayStatusActor();
//        deliver(serverDelayStatusActor, deliveryId -> {
//            workerDelayStatusRequest.setDeliveryId(deliveryId);
//            return workerDelayStatusRequest;
//        });
//    }

    /**
     * Handle destroy container.
     *
     * @param destroyRequest destroyRequest
     */
    public void handleDestroyContainer(MasterDestroyContainerRequest destroyRequest) {
        ActorSelection workerContainerActor = WorkerUtil.getWorkerContainerActor(destroyRequest.getWorkerAddress());
        deliver(workerContainerActor, deliveryId -> {
            destroyRequest.setDeliveryId(deliveryId);
            return destroyRequest;
        });
    }

    /**
     * Handle stop container.
     *
     * @param stopRequest stopRequest
     */
    public void handleStopContainer(MasterStopContainerRequest stopRequest) {
        ActorSelection workerContainerActor = WorkerUtil.getWorkerContainerActor(stopRequest.getWorkerAddress());
        deliver(workerContainerActor, deliveryId -> {
            stopRequest.setDeliveryId(deliveryId);
            return stopRequest;
        });
    }

    /**
     * Handle stop instance task
     *
     * @param stopTaskRequest stopTaskRequest
     */
    public void handleStopInstanceTask(MasterStopInstanceTaskRequest stopTaskRequest) {
        ActorSelection workerContainerActor = WorkerUtil.getWorkerContainerActor(stopTaskRequest.getWorkerAddress());
        deliver(workerContainerActor, deliveryId -> {
            stopTaskRequest.setDeliveryId(deliveryId);
            return stopTaskRequest;
        });
    }

    /**
     * Handle result
     *
     * @param result result.
     */
    public void handleResult(Result<?> result) {
        if (StatusEnum.FAIL.getStatus().equals(result.getStatus()) || Objects.isNull(result.getData())) {
            log.warn("Handle result fail! message={}", result.getMessage());
            return;
        }

        if (result.getData() instanceof WorkerResponse) {
            WorkerResponse workerResponse = (WorkerResponse) result.getData();
            confirmDelivery(workerResponse.getDeliveryId());
            return;
        }

        if (result.getData() instanceof ServerResponse) {
            ServerResponse serverResponse = (ServerResponse) result.getData();
            confirmDelivery(serverResponse.getDeliveryId());
            return;
        }

        log.error("Handle result data not defined data={}", result.getData().toString());
    }

    @Override
    public String persistenceId() {
        return String.format("persistence-new-id-%d", id);
    }
}
