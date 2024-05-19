package io.akkajob.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;
@Data
public class ServerWorkerStartResponse implements Serializable {
    private Long appId;
    private String appName;
    private Set<String> workerAddressList;
}
