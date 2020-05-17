package com.sukaiyi.weedclient.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sukaiyi.weedclient.exception.SeaweedfsException;
import com.sukaiyi.weedclient.model.*;
import com.sukaiyi.weedclient.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WeedClientServiceImpl implements WeedClientService {

    private final SeaweedMasterSource seaweedMasterSource;

    public WeedClientServiceImpl(SeaweedProperties seaweedProperties) {
        this.seaweedMasterSource = new SeaweedMasterSource(seaweedProperties.getUrls());
    }

    public AssignedFileId assignFileId() {
        return assignFileId(null);
    }

    public AssignedFileId assignFileId(AssignFileIdParam params) {
        String endpoint = seaweedMasterSource.choose() + WeedRestEndpoint.ASSIGN_FILE_KEY;
        Map<String, Object> paramMap = BeanUtil.beanToMap(params);
        String result = HttpUtil.get(endpoint, paramMap);
        return JSONUtil.toBean(result, AssignedFileId.class);
    }

    public VolumeLocation lookupVolume(String volumeId) {
        String endpoint = seaweedMasterSource.choose() + WeedRestEndpoint.LOOKUP_VOLUME;
        String endpointWithParam = endpoint + "?volumeId=" + volumeId;
        String result = HttpUtil.get(endpointWithParam);
        return JSONUtil.toBean(result, VolumeLocation.class);
    }

    public FileInfo write(File file) {
        return write(file.getName(), file);
    }

    @Override
    public FileInfo write(String fileName, File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return write(fileName, fis);
        } catch (IOException e) {
            throw ExceptionUtil.wrap(e, SeaweedfsException.class);
        }
    }

    public FileInfo write(String fileName, InputStream is) {
        return write(fileName, is, null);
    }

    public FileInfo write(String fileName, InputStream is, AssignedFileId assignedFileId) {
        try {
            assignedFileId = Optional.ofNullable(assignedFileId).orElseGet(this::assignFileId);
            String publicUrl = assignedFileId.getPublicUrl();
            String fileId = assignedFileId.getFid();
            boolean absolute = publicUrl.startsWith("http://") || publicUrl.startsWith("https://");
            String endpoint = (absolute ? "" : "http://") + publicUrl + "/" + fileId;
            String result = HttpUtil.post(endpoint, new HashMap<String, Object>() {{
                put(fileName, new InputStreamResource(is, fileName));
            }});
            UploadResult uploadResult = JSONUtil.toBean(result, UploadResult.class);
            return new FileInfo(fileId, null, fileName, uploadResult.getSize(), null);
        } finally {
            IoUtils.close(is);
        }
    }

    @Override
    public InputStream read(String fileId) {
        String endpoint = fileUrl(fileId);
        return HttpRequest.get(endpoint).executeAsync().bodyStream();
    }

    @Override
    public String fileUrl(String fileId) {
        VolumeLocation location = lookupVolume(fileId);
        String url = Optional.ofNullable(location)
                .map(VolumeLocation::getLocations)
                .filter(e -> !e.isEmpty())
                .map(e -> e.get(0))
                .map(VolumeLocation.Location::getPublicUrl)
                .orElse(null);
        if (url == null) {
            throw new SeaweedfsException("cant find file url");
        }
        boolean absolute = url.startsWith("http://") || url.startsWith("https://");
        return (absolute ? "" : "http://") + url + "/" + fileId;
    }

    @Override
    public FileInfo fileInfo(String fileId) {
        try {
            String endpoint = fileUrl(fileId);
            FileInfo fileInfo = new FileInfo();
            URLConnection con = new URL(endpoint).openConnection();
            String contentType = con.getHeaderField("Content-Type");
            String contentDisposition = con.getHeaderField("Content-Disposition");
            long lastModified = con.getHeaderFieldDate("Last-Modified", 0);
            long contentLength = con.getHeaderFieldLong("Content-Length", 0);
            fileInfo.setLastModified(lastModified);
            int fileNameStart = contentDisposition.indexOf("filename=\"");
            int fileNameEnd = contentDisposition.indexOf('"', fileNameStart + 10);
            String fileName = contentDisposition.substring(fileNameStart + 10, fileNameEnd);
            fileInfo.setFileName(URLUtil.decode(fileName));
            fileInfo.setContentType(contentType);
            fileInfo.setSize(contentLength);
            fileInfo.setFileId(fileId);
            return fileInfo;
        } catch (IOException e) {
            throw ExceptionUtil.wrap(e, SeaweedfsException.class);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            String endpoint = fileUrl(fileId);
            HttpRequest.delete(endpoint);
        } catch (Exception e) {
            ExceptionUtil.wrap(e, SeaweedfsException.class);
        }
    }
}
