package io.akkajob.common.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Data
public class WorkerJobInstanceTaskLogFieldRequest implements Serializable {
    private String name;
    private String value;

    /**
     * Non arg constructor for Serializable.
     */
    @SuppressWarnings("unused")
    public WorkerJobInstanceTaskLogFieldRequest() {

    }

    public WorkerJobInstanceTaskLogFieldRequest(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
