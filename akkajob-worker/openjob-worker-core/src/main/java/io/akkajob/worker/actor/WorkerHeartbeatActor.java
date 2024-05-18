package io.akkajob.worker.actor;

import akka.actor.AbstractActor;
import io.akkajob.common.request.ServerWorkerHeartbeatRequest;
import io.akkajob.common.response.Result;

public class WorkerHeartbeatActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ServerWorkerHeartbeatRequest.class, this::workerHeartbeatCheckRequest)
                .build();
    }

    public void workerHeartbeatCheckRequest(ServerWorkerHeartbeatRequest workerHeartbeatCheckRequest) {
        getSender().tell(Result.success(new Object()), getSelf());
    }
}
