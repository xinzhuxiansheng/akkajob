package io.akkajob.worker.processor;

import lombok.Data;
@Data
public class TaskResult {
    private Long jobInstanceId;
    private Long circleId;
    private String taskId;
    private String parentTaskId;
    private String result;
    private String taskName;
    private Integer status;
}
