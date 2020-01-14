package com.sukaiyi.weedclient;

import com.sukaiyi.weedclient.exception.SeaweedfsException;
import com.sukaiyi.weedclient.http.SeaweedJsonResultHttpClient;
import com.sukaiyi.weedclient.model.*;
import com.sukaiyi.weedclient.utils.IoUtils;
import com.sukaiyi.weedclient.utils.UrlUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class WeedClientServiceImpl implements WeedClientService {
    private List<String> urls;

    private SeaweedJsonResultHttpClient seaweedJsonResultHttpClient;

    @Override
    public void init(SeaweedSource seaweedSource) {
        this.urls = seaweedSource.getUrls();
        this.seaweedJsonResultHttpClient = new SeaweedJsonResultHttpClient();
    }

    public AssignedFileId assignFileId() throws IOException {
        return assignFileId(null);
    }

    public AssignedFileId assignFileId(Map<String, String> params) throws IOException {
        String endpoint = choose(this.urls) + WeedRestEndpoint.ASSIGN_FILE_KEY;
        String endpointWithParam = UrlUtils.concatParam(endpoint, params);
        return seaweedJsonResultHttpClient.get(endpointWithParam, AssignedFileId.class);
    }

    public VolumeLocation lookupVolume(String volumeId) throws IOException {
        String endpoint = choose(this.urls) + WeedRestEndpoint.LOOKUP_VOLUME;
        String endpointWithParam = endpoint + "?volumeId=" + volumeId;
        return seaweedJsonResultHttpClient.get(endpointWithParam, VolumeLocation.class);
    }

    public FileInfo upload(File file) throws IOException {
        return upload(file.getName(), file);
    }

    @Override
    public FileInfo upload(String fileName, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return upload(fileName, fis);
        }
    }

    public FileInfo upload(String fileName, InputStream is) throws IOException {
        AssignedFileId assignedFileId = this.assignFileId();
        String publicUrl = assignedFileId.getPublicUrl();
        String fileId = assignedFileId.getFid();
        boolean absolute = publicUrl.startsWith("http://") || publicUrl.startsWith("https://");
        String endpoint = (absolute ? "" : "http://") + publicUrl + "/" + fileId;
        Map<String, InputStream> binaryBody = new HashMap<>(1);
        binaryBody.put(fileName, is);
        try {
            UploadResult uploadResult = seaweedJsonResultHttpClient.postMultipart(endpoint, binaryBody, null, UploadResult.class);
            return new FileInfo(fileId, null, uploadResult.getName(), uploadResult.getSize(), null);
        } finally {
            IoUtils.close(is);
        }
    }

    @Override
    public InputStream fileStream(String fileId) throws IOException {
        String endpoint = fileUrl(fileId);
        URLConnection con = new URL(endpoint).openConnection();
        return con.getInputStream();
    }

    @Override
    public String fileUrl(String fileId) throws IOException {
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
    public FileInfo fileInfo(String fileId) throws IOException {
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
        fileInfo.setFileName(UrlUtils.urlDecode(fileName));
        fileInfo.setContentType(contentType);
        fileInfo.setSize(contentLength);
        return null;
    }

    @Override
    public void delete(String fileId) throws IOException {
        String endpoint = fileUrl(fileId);
        seaweedJsonResultHttpClient.delete(endpoint, Map.class);
    }

    private String choose(List<String> urls) {
        return Optional.ofNullable(urls).filter(e -> !e.isEmpty()).map(e -> e.get(0)).orElse("http:127.0.0.1:9333");
    }
}
