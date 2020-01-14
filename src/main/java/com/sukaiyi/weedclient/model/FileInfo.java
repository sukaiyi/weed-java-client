package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    private String fileId;
    private String contentType;
    private String fileName;
    private Long size;
    private Long lastModified;
}
