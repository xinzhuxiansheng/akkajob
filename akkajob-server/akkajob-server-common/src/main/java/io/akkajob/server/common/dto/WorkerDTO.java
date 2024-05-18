package io.akkajob.server.common.dto;

import lombok.Data;

@Data
public class WorkerDTO {
    private Long appId;

    private Long namespaceId;

    private String appName;

    private String workerKey;

    private String address;

    private String protocolType;
}
