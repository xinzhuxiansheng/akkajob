package io.akkajob.common.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorkerResponse implements Serializable {
    private Long deliveryId;

    /**
     * Non arg constructor for Serializable.
     */
    @SuppressWarnings("unused")
    public WorkerResponse() {
    }

    public WorkerResponse(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
}
