package io.akkajob.server.openapi.service;

import io.akkajob.server.openapi.request.ClusterOnlineRequest;
import io.akkajob.server.openapi.vo.ClusterOnlineVO;

public interface ClusterService {

    ClusterOnlineVO online(ClusterOnlineRequest clusterOnlineRequest);
}
