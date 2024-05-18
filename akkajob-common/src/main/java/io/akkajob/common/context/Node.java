package io.akkajob.common.context;

import lombok.Data;

@Data
public class Node {
    private Long serverId;
    private String ip;
    private String akkaAddress;
    private Integer status;
}
