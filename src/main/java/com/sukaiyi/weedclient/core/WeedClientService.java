package com.sukaiyi.weedclient.core;

import com.sukaiyi.weedclient.model.*;

import java.io.*;

public interface WeedClientService {

    /**
     * 分配FileId
     *
     * @return AssignedFileId
     */
    AssignedFileId assignFileId();

    AssignedFileId assignFileId(AssignFileIdParam param);

    /**
     * 查找 volume
     *
     * @return VolumeLocation
     */
    VolumeLocation lookupVolume(String volumeId);

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    FileInfo write(File file);

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    FileInfo write(String fileName, File file);

    /**
     * 上传文件
     *
     * @param is 输入流
     * @return 文件信息
     */
    FileInfo write(String fileName, InputStream is, AssignedFileId assignedFileId);

    /**
     * 读取文件流
     *
     * @param fileId 文件ID
     * @return 文件流
     */
    InputStream read(String fileId);

    /**
     * 获取文件地址
     *
     * @param fileId 文件ID
     * @return 文件地址
     */
    String fileUrl(String fileId);

    /**
     * 读取文件信息
     *
     * @param fileId 文件ID
     * @return FileInfo
     */
    FileInfo fileInfo(String fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     */
    void delete(String fileId);
}
