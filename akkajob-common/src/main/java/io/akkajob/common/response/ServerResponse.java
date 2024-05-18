package io.akkajob.common.response;

import lombok.Data;

import java.io.Serializable;
@Data
public class ServerResponse implements Serializable {
    private Long deliveryId;

    /**
     * Non arg constructor for Serializable.
     */
    @SuppressWarnings("unused")
    public ServerResponse() {
    }

    public ServerResponse(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
}
