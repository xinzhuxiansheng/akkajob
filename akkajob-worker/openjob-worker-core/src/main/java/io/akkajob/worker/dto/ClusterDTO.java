package io.akkajob.worker.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClusterDTO {
    private List<String> servers;
}
