package com.sukaiyi.weedclient;

import com.sukaiyi.weedclient.model.*;

import java.io.*;
import java.util.Map;

public interface WeedClientService {

    /**
     * 初始化
     *
     * @param seaweedSource 连接参数
     */
    void init(SeaweedSource seaweedSource);

    /**
     * 分配FileId
     *
     * @return AssignedFileId
     */
    AssignedFileId assignFileId() throws IOException;

    AssignedFileId assignFileId(Map<String, String> params) throws IOException;

    /**
     * 查找 volume
     *
     * @return VolumeLocation
     */
    VolumeLocation lookupVolume(String volumeId) throws IOException;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    FileInfo upload(File file) throws IOException;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    FileInfo upload(String fileName, File file) throws IOException;

    /**
     * 上传文件
     *
     * @param is 输入流
     * @return 文件信息
     */
    FileInfo upload(String fileName, InputStream is) throws IOException;

    /**
     * 读取文件流
     *
     * @param fileId 文件ID
     * @return 文件流
     */
    InputStream fileStream(String fileId) throws IOException;

    /**
     * 获取文件地址
     *
     * @param fileId 文件ID
     * @return 文件地址
     */
    String fileUrl(String fileId) throws IOException;

    /**
     * 读取文件信息
     *
     * @param fileId 文件ID
     * @return FileInfo
     */
    FileInfo fileInfo(String fileId) throws IOException;

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     */
    void delete(String fileId) throws IOException;
}
