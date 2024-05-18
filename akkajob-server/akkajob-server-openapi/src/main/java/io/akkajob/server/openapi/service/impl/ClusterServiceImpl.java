package io.akkajob.server.openapi.service.impl;

import io.akkajob.common.context.Node;
import io.akkajob.server.common.ClusterContext;
import io.akkajob.server.openapi.request.ClusterOnlineRequest;
import io.akkajob.server.openapi.service.ClusterService;
import io.akkajob.server.openapi.vo.ClusterOnlineVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Override
    public ClusterOnlineVO online(ClusterOnlineRequest clusterOnlineRequest) {
        ClusterOnlineVO clusterOnlineVO = new ClusterOnlineVO();
        List<String> servers = ClusterContext
                .getNodesMap()
                .values().stream()
                .map(Node::getAkkaAddress)
                .collect(Collectors.toList());
        clusterOnlineVO.setServers(servers);
        return clusterOnlineVO;
    }
}
