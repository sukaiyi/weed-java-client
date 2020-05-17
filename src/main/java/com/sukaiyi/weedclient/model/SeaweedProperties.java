package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sukaiyi
 * @date 2020/01/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeaweedProperties {
    private List<String> urls;
    private long timeout;
}
