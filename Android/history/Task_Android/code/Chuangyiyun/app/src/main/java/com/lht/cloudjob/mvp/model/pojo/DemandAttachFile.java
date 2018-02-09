package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.util.file.FileUtils;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandAttachFile
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/22.
 */

public class DemandAttachFile {
    private String file_path;

    private String file_name;

    private String file_ext;

    private long file_size;

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_ext() {
        return file_ext;
    }

    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public static DemandAttachFile fromLocal(String localPath) {
        DemandAttachFile ret = new DemandAttachFile();
        File file = new File(localPath);
        ret.setFile_name(FileUtils.getFileName(localPath));
        ret.setFile_size(file.length());
        ret.setFile_ext(FileUtils.getFileType(localPath));
        return ret;
    }
}
