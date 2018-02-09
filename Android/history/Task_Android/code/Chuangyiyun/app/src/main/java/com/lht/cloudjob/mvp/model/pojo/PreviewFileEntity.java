package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.util.string.StringUtil;

import static com.lht.cloudjob.util.md5.Md5Util.getStringMd5;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PreviewFileEntity
 * <p><b>Description</b>: 预览文件
 * <p>Created by leobert on 2016/11/2.
 */

public class PreviewFileEntity {
    private static final String STRING_DOT = ".";
    /**
     * the unique identify code of the task that the attachment belongs to
     */
    private String taskBn;

    /**
     * the real name of the file
     */
    private String fileName;

    /**
     * the standard MIME-type of the attachment,e.g. avi video/x-msvideo
     */
    private String mimeType;

    /**
     * the extension of the attachment,no dot contains,e.g. jpg,psd,rar
     */
    private String extension;

    /**
     * bytes of the attachment
     */
    private long fileSize;

    /**
     * url of the attachment
     */
    private String fileUrl;

//    /**
//     * the username of the user who request preview actions
//     */
//    private String requestUser;

    /**
     * the last modify time of the task,suspect attachment may be changed when task be changed
     */
    private String taskLastModifyTime;

    /**
     * the last modify time of the attachment,attachment must be changed when the time be changed
     */
    private String fileLastModifyTime;

    public String getTaskBn() {
        return taskBn;
    }

    public void setTaskBn(String taskBn) {
        this.taskBn = taskBn;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        if (extension.contains(STRING_DOT)) {
            extension = extension.replaceAll(STRING_DOT, "");
        }
        this.extension = extension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

//    public String getRequestUser() {
//        return requestUser;
//    }
//
//    public void setRequestUser(String requestUser) {
//        this.requestUser = requestUser;
//    }

    public String getTaskLastModifyTime() {
        return taskLastModifyTime;
    }

    public void setTaskLastModifyTime(String taskLastModifyTime) {
        this.taskLastModifyTime = taskLastModifyTime;
    }

    public String getFileLastModifyTime() {
        return fileLastModifyTime;
    }

    public void setFileLastModifyTime(String fileLastModifyTime) {
        this.fileLastModifyTime = fileLastModifyTime;
    }

    public String getUniqueName(String requestUser) {
        StringBuilder stringBuilder = new StringBuilder();
        //res = taskBn+fileName+fileUrl+requestUser+taskLastModifyTime+fileLastModifyTime
        //enc = appendExtension(md5(res));
        stringBuilder.append(getTaskBn()).append(getFileName()).append(getFileUrl()).append(requestUser)
                .append(getTaskLastModifyTime()).append(getFileLastModifyTime());
        String enc = getStringMd5(stringBuilder.toString());
        return appendExtension(enc);
    }

    private String appendExtension(String name) {
        final String _name = StringUtil.nullStrToEmpty(name);
        if (StringUtil.isEmpty(getExtension())) {
            return _name;
        } else {
            return _name + STRING_DOT + getExtension();
        }
    }

    public static PreviewFileEntity copyFromAttachmentExt(DemandInfoResBean.AttachmentExt attachmentExt) {
        PreviewFileEntity entity = new PreviewFileEntity();

        entity.setFileName(attachmentExt.getFile_name());
        entity.setExtension(attachmentExt.getFile_ext());
        entity.setFileSize(attachmentExt.getFile_size());
        entity.setFileUrl(attachmentExt.getUrl_download());
        entity.setMimeType(attachmentExt.getMime());

        return entity;
    }
}
