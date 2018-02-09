package com.lht.cloudjob.mvp.model.bean;

/**
 * Created by chhyu on 2016/11/21.
 */

public class VersionResBean {

    private int ret;
    private String ver;

    private VersionInfoData data;

    public VersionInfoData getData() {
        return data;
    }

    public void setData(VersionInfoData data) {
        this.data = data;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * 版本信息
     */
    public static class VersionInfoData {
        private String info;
        private String md5;
        private String url;
        private String fileName;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
