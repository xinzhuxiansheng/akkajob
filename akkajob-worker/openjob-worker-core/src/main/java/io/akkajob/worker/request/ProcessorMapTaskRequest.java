package io.akkajob.worker.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Data
public class ProcessorMapTaskRequest implements Serializable {
    private Long jobId;
    private Long jobInstanceId;
    private Long taskId;
    private String parentTaskName;
    private String taskName;
    private Integer initValueId;
    private Integer taskNum;
    private List<byte[]> tasks;
}
