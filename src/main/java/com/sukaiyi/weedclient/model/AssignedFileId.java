package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedFileId{
    private Integer count;
    private String fid;
    private String url;
    private String publicUrl;
}
