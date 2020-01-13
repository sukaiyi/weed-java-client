package com.sukaiyi.weedclient;

import com.sukaiyi.weedclient.model.AssignedKey;
import com.sukaiyi.weedclient.model.FileStatus;
import com.sukaiyi.weedclient.model.VolumeLocation;

import java.io.*;

public class WeedClientServiceImpl implements WeedClientService {

    public AssignedKey assignFileId() {
        return null;
    }

    public AssignedKey assignFileId(Integer count, String replication, String dataCenter) {
        return null;
    }

    public VolumeLocation lookupVolume() {
        return null;
    }

    public FileStatus upload(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return upload(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FileStatus upload(InputStream is) {
        return null;
    }
}
