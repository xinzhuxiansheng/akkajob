package io.akkajob.worker.dto;

import lombok.Data;
@Data
public class JobInstanceDTO {
    private Long jobId;
    private Long jobInstanceId;
    private Long circleId;
    private Long dispatchVersion;
    private String jobParamType;
    private String jobParams;
    private String jobExtendParamsType;
    private String jobExtendParams;
    private String executeType;
    private Long workflowId;
    private String processorType;
    private String processorInfo;
    private Integer failRetryTimes;
    private Integer failRetryInterval;
    private Integer executeTimeout;
    private Integer concurrency;
    private String timeExpressionType;
    private String timeExpression;
    private Integer executeOnce;
}
