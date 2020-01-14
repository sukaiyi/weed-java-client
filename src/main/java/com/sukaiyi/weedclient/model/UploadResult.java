package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sukaiyi
 * @date 2020/01/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {
    private String name;
    private Long size;
    private String eTag;
}
