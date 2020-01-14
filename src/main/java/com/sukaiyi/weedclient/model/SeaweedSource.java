package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
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
public class SeaweedSource {
    private List<String> urls;
}
