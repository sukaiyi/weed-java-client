package com.sukaiyi.weedclient;

import com.sukaiyi.weedclient.model.AssignedFileId;
import com.sukaiyi.weedclient.model.FileInfo;
import com.sukaiyi.weedclient.model.SeaweedSource;
import com.sukaiyi.weedclient.model.VolumeLocation;
import com.sukaiyi.weedclient.utils.IoUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeedClientServiceTest {

    private WeedClientService weedClientService = new WeedClientServiceImpl();
    private List<String> fileUploaded = new ArrayList<>();

    @BeforeAll
    void before() {
        fileUploaded.clear();
        SeaweedSource seaweedSource = new SeaweedSource();
        seaweedSource.setUrls(Arrays.asList("http://127.0.0.1:9333", "http://127.0.0.1:9334", "http://127.0.0.1:9335"));
        weedClientService.init(seaweedSource);
    }

    @AfterAll
    void after() throws IOException {
        for (String fileId : fileUploaded) {
            weedClientService.delete(fileId);
        }
    }

    @Test
    void assignFileId() throws IOException {
        AssignedFileId assignedFileId = weedClientService.assignFileId();
        Assertions.assertNotNull(assignedFileId);
        Assertions.assertEquals(1, assignedFileId.getCount());
        Assertions.assertNotNull(assignedFileId.getFid());
        Assertions.assertNotNull(assignedFileId.getPublicUrl());
        Assertions.assertNotNull(assignedFileId.getUrl());
        Assertions.assertTrue(assignedFileId.getFid().matches("\\d+,[a-z0-9]+"), "fileId:" + assignedFileId.getFid());
    }

    @Test
    void assignFileIdWithParam() throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("replication", "001");
        param.put("count", "2");
//        param.put("dataCenter", "dataCenter");
//        param.put("ttl", "3m");
//        param.put("collection", "test");
        AssignedFileId assignedFileId = weedClientService.assignFileId(param);

        Assertions.assertNotNull(assignedFileId);
        Assertions.assertEquals(2, assignedFileId.getCount());
        Assertions.assertNotNull(assignedFileId.getFid());
        Assertions.assertNotNull(assignedFileId.getPublicUrl());
        Assertions.assertNotNull(assignedFileId.getUrl());
        Assertions.assertTrue(assignedFileId.getFid().matches("\\d+,[a-z0-9]+"), "fileId:" + assignedFileId.getFid());
    }

    @Test
    void lookupVolume() throws IOException {
        String volumeId = "21";
        VolumeLocation location = weedClientService.lookupVolume(volumeId);

        Assertions.assertNotNull(location);
        Assertions.assertEquals(volumeId, location.getVolumeId());
    }

    @Test
    void upload() throws IOException {
        FileOutputStream fos = null;
        PrintStream printStream = null;
        try {
            File testFile = new File("test_upload_file.txt");
            fos = new FileOutputStream(testFile);
            printStream = new PrintStream(fos);
            printStream.print("Hello Seaweedfs");
            fos.close();
            printStream.close();

            FileInfo fileInfo = weedClientService.upload(testFile);

            Assertions.assertNotNull(fileInfo);
            Assertions.assertEquals("test_upload_file.txt", fileInfo.getFileName());
            Assertions.assertEquals(15, fileInfo.getSize());
            Assertions.assertTrue(fileInfo.getFileId().matches("\\d+,[a-z0-9]+"), "fileId:" + fileInfo.getFileId());

            fileUploaded.add(fileInfo.getFileId());

            testFile.delete();
        } finally {
            IoUtils.close(fos);
            IoUtils.close(printStream);
        }
    }

    @Test
    void uploadWithFileName() throws IOException {
        File testFile = new File("test.png");
        FileInfo fileInfo = weedClientService.upload("app.png",testFile);

        Assertions.assertNotNull(fileInfo);
        Assertions.assertEquals("app.png", fileInfo.getFileName());
        Assertions.assertEquals(19797, fileInfo.getSize());
        Assertions.assertTrue(fileInfo.getFileId().matches("\\d+,[a-z0-9]+"), "fileId:" + fileInfo.getFileId());

        fileUploaded.add(fileInfo.getFileId());

    }

    @Test
    void testUpload() {
    }

    @Test
    void fileStream() {
    }

    @Test
    void fileInfo() {
    }
}