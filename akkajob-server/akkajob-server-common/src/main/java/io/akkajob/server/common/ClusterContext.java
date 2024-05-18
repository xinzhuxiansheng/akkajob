package io.akkajob.server.common;

import akka.actor.ActorRef;
import com.google.common.collect.Maps;
import io.akkajob.common.context.Node;
import io.akkajob.server.common.dto.SystemDTO;
import io.akkajob.server.common.dto.WorkerDTO;

import java.util.*;

public class ClusterContext {

    /**
     * Cluster actor reference.
     */
    private static ActorRef clusterActorRef;

    /**
     * Current node.
     */
    private static Node currentNode;

    /**
     * System
     */
    private static SystemDTO system;

    /**
     * Current slots
     */
    private static final Set<Long> CURRENT_SLOTS = new HashSet<>();

    /**
     * Cluster nodes.
     * Key is server id, Value is server node.
     */
    private static final Map<Long, Node> NODES_LIST = Maps.newConcurrentMap();

    /**
     * App worker list.
     * Key is appid, Value is workers.
     */
    private static final Map<Long, List<WorkerDTO>> APP_WORKERS = Maps.newConcurrentMap();

    /**
     * Refresh Current node.
     *
     * @param slotsIds Current slots ids.
     */
    public static synchronized void refreshCurrentSlots(Set<Long> slotsIds) {
        CURRENT_SLOTS.clear();
        CURRENT_SLOTS.addAll(slotsIds);
    }

    /**
     * Refresh nodes.
     *
     * @param nodes nodes
     */
    public static synchronized void refreshNodeList(Map<Long, Node> nodes) {
        NODES_LIST.clear();
        NODES_LIST.putAll(nodes);
    }

    /**
     * Refresh app worker.
     *
     * @param workers workers
     */
    public static synchronized void refreshAppWorkers(Map<Long, List<WorkerDTO>> workers) {
        APP_WORKERS.clear();
        APP_WORKERS.putAll(workers);
    }

    /**
     * Set cluster actor reference.
     *
     * @param clusterActorRef clusterActorRef
     */
    public static synchronized void setClusterActorRef(ActorRef clusterActorRef) {
        ClusterContext.clusterActorRef = clusterActorRef;
    }

    /**
     * Set current node.
     *
     * @param currentNode currentNode
     */
    public static synchronized void setCurrentNode(Node currentNode) {
        ClusterContext.currentNode = currentNode;
    }

    /**
     * Refresh system.
     *
     * @param system system
     */
    public static synchronized void refreshSystem(SystemDTO system) {
        ClusterContext.system = system;
    }

    public static SystemDTO getSystem() {
        return ClusterContext.system;
    }

    public static Map<Long, List<WorkerDTO>> get() {
        return APP_WORKERS;
    }

    public static Map<Long, List<WorkerDTO>> getAppWorkers() {
        return APP_WORKERS;
    }

    public static List<WorkerDTO> getWorkersByAppId(Long appId) {
        return Optional.ofNullable(APP_WORKERS.get(appId))
                .orElseGet(ArrayList::new);
    }

    /**
     * Return current slots.
     *
     * @return Set<Long>
     */
    public static Set<Long> getCurrentSlots() {
        return CURRENT_SLOTS;
    }


    /**
     * Return nodes.
     *
     * @return Map<Long, Node>
     */
    public static Map<Long, Node> getNodesMap() {
        return NODES_LIST;
    }

    public static ActorRef getClusterActorRef() {
        return clusterActorRef;
    }

    /**
     * Return node.
     *
     * @return Node
     */
    public static Node getCurrentNode() {
        return currentNode;
    }
}
