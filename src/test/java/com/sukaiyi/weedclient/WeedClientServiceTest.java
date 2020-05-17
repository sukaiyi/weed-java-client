package com.sukaiyi.weedclient;

import com.sukaiyi.weedclient.core.WeedClientService;
import com.sukaiyi.weedclient.core.WeedClientServiceImpl;
import com.sukaiyi.weedclient.model.*;
import com.sukaiyi.weedclient.utils.IoUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WeedClientServiceTest {

    private WeedClientService weedClientService;
    private List<String> fileUploaded = new ArrayList<>();

    @BeforeEach
    void before() {
        fileUploaded.clear();
        SeaweedProperties seaweedProperties = new SeaweedProperties();
        seaweedProperties.setUrls(Arrays.asList("http://127.0.0.1:9333"));
        weedClientService = new WeedClientServiceImpl(seaweedProperties);
    }

    @AfterEach
    void after() {
        for (String fileId : fileUploaded) {
            weedClientService.delete(fileId);
        }
    }

    @Test
    @Order(1)
    void assignFileId() {
        AssignedFileId assignedFileId = weedClientService.assignFileId();
        Assertions.assertNotNull(assignedFileId);
        Assertions.assertEquals(1, assignedFileId.getCount());
        Assertions.assertNotNull(assignedFileId.getFid());
        Assertions.assertNotNull(assignedFileId.getPublicUrl());
        Assertions.assertNotNull(assignedFileId.getUrl());
        Assertions.assertTrue(assignedFileId.getFid().matches("\\d+,[a-z0-9]+"), "fileId:" + assignedFileId.getFid());
    }

    @Test
    @Order(2)
    void assignFileIdWithParam() {
        AssignFileIdParam param = new AssignFileIdParam();
        param.setReplication("001");
        param.setCount(2);
//        param.setDataCenter("dataCenter");
//        param.setTtl("3m");
//        param.setCollection("test");
        AssignedFileId assignedFileId = weedClientService.assignFileId(param);

        Assertions.assertNotNull(assignedFileId);
        Assertions.assertEquals(2, assignedFileId.getCount());
        Assertions.assertNotNull(assignedFileId.getFid());
        Assertions.assertNotNull(assignedFileId.getPublicUrl());
        Assertions.assertNotNull(assignedFileId.getUrl());
        Assertions.assertTrue(assignedFileId.getFid().matches("\\d+,[a-z0-9]+"), "fileId:" + assignedFileId.getFid());
    }

    @Test
    @Order(3)
    void lookupVolume() {
        String volumeId = "21";
        VolumeLocation location = weedClientService.lookupVolume(volumeId);

        Assertions.assertNotNull(location);
        Assertions.assertEquals(volumeId, location.getVolumeId());
    }

    @Test
    @Order(4)
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

            FileInfo fileInfo = weedClientService.write(testFile);

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
    @Order(5)
    void uploadWithFileName() {
        File testFile = new File("test.png");
        FileInfo fileInfo = weedClientService.write("app.png", testFile);

        Assertions.assertNotNull(fileInfo);
        Assertions.assertEquals("app.png", fileInfo.getFileName());
        Assertions.assertEquals(19797, fileInfo.getSize());
        Assertions.assertTrue(fileInfo.getFileId().matches("\\d+,[a-z0-9]+"), "fileId:" + fileInfo.getFileId());

        fileUploaded.add(fileInfo.getFileId());

    }

    @Test
    @Order(999)
    void fileInfo() {
        List<FileInfo> collect = new ArrayList<>();
        for (String s : fileUploaded) {
            FileInfo fileInfo = weedClientService.fileInfo(s);
            collect.add(fileInfo);
        }

        for (FileInfo fileInfo : collect) {
            Assertions.assertNotNull(fileInfo);
            Assertions.assertNotNull(fileInfo.getFileId());
            Assertions.assertNotNull(fileInfo.getFileId());
            Assertions.assertTrue(fileInfo.getSize() > 0, "file size:" + fileInfo.getSize());
            Assertions.assertNotNull(fileInfo.getFileName());
            Assertions.assertNotNull(fileInfo.getContentType());
            Assertions.assertNotNull(fileInfo.getLastModified());
        }
    }
}