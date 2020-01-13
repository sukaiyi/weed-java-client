package com.sukaiyi.weedclient;

import com.sukaiyi.weedclient.model.AssignedKey;
import com.sukaiyi.weedclient.model.FileStatus;
import com.sukaiyi.weedclient.model.VolumeLocation;

import java.io.File;
import java.io.InputStream;

public interface WeedClientService {

    AssignedKey assignFileId();

    AssignedKey assignFileId(Integer count, String replication, String dataCenter);

    VolumeLocation lookupVolume();

    FileStatus upload(File file);

    FileStatus upload(InputStream is);

}
