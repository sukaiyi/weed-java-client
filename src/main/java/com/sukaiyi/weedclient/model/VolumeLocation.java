package com.sukaiyi.weedclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolumeLocation {
    private String volumeId;
    private List<Location> locations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private String publicUrl;
        private String url;
    }

}
