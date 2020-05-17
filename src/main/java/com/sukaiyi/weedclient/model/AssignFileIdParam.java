package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sukaiyi
 * @date 2020/01/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignFileIdParam {
    private String replication;
    private int count;
    private String dataCenter;
    private String ttl;
    private String collection;
}
