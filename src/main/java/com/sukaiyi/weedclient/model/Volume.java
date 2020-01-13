package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Volume {
    private Integer Id;
    private Long Size;
    private String RepType;
    private String Version;
    private Long FileCount;
    private Long DeleteCount;
    private Long DeletedByteCount;
    private Boolean ReadOnly;
}
