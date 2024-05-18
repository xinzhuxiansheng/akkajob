package io.akkajob.common.constant;

public class AkkaConstant {
    /**
     * Path
     */
    public static final String AKKA_PATH_FORMAT = "akka://%s@%s/user/%s";

    /**
     * Server
     */
    public static final String SERVER_SYSTEM_NAME = "akkajob-server";
    public static final String SERVER_ACTOR_WORKER = "worker";
    public static final String SERVER_ACTOR_WORKER_HEARTBEAT = "worker-heartbeat";
    public static final String SERVER_ACTOR_WORKER_INSTANCE = "worker-instance-status";
    public static final String SERVER_ACTOR_WORKER_INSTANCE_TASK_LOG = "worker-instance-task-log";
    public static final String SERVER_ACTOR_WORKER_DELAY_INSTANCE = "worker-delay-instance";
    public static final String SERVER_ACTOR_WORKER_DELAY_INSTANCE_PULL = "worker-delay-instance-pull";
    public static final String SERVER_ACTOR_WORKER_DELAY_INSTANCE_STATUS = "worker-delay-instance-status";

    /**
     * Worker
     */
    public static final String WORKER_SYSTEM_NAME = "akkajob-worker";
    public static final String WORKER_ACTOR_HEARTBEAT = "heartbeat";
    public static final String WORKER_ACTOR_MASTER = "task-master";
    public static final String WORKER_ACTOR_DELAY_MASTER = "delay-master";

    /**
     * Worker Path
     */
    public static final String WORKER_PATH_TASK_MASTER = "/user/" + WORKER_ACTOR_MASTER;
}
