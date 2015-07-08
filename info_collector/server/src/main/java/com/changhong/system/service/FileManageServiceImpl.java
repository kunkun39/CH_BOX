package com.changhong.system.service;

import com.changhong.common.exception.CHDocumentOperationException;
import com.changhong.system.domain.AppIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by kerio on 2015/7/7.
 */
@Service("fileManageServicce")
public class FileManageServiceImpl implements FileManageService {

    private static final Log logger = LogFactory.getLog(FileManageServiceImpl.class);
    //文件存储路径
    @Value("${project.upload.file.path}")
    private String baseStorePath;

    @Override
    public void uploadAppIconFile(AppIcon file) {
        if (file != null) {
            //create save file path
            File directory = new File(baseStorePath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            //save the file
            File saveFile = new File(directory, file.getActualFileName());
            logger.info(baseStorePath);
            try {
                OutputStream dataOut = new FileOutputStream(saveFile);
                FileCopyUtils.copy(file.getFile().getInputStream(), dataOut);
            } catch (Exception e) {
                logger.error(e);
                throw new CHDocumentOperationException("exception update file", e);
            }
        }
    }

    @Override
    public void deleteAppIconFile(AppIcon file) {
        try {
            File deleteFile = new File(baseStorePath + File.separatorChar + file.getActualFileName());
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
        } catch (Exception e) {
            logger.error("file not delete OK", e);
        }
    }
}
