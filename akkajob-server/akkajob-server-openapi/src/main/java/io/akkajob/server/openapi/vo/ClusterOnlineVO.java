package io.akkajob.server.openapi.vo;

import lombok.Data;

import java.util.List;
@Data
public class ClusterOnlineVO {
    private List<String> servers;
}
