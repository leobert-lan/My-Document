package com.lht.cloudjob.mvp.model.bean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> UploadResBean
 * <p><b>Description</b>: 上传文件response data
 * {
 * "ret": 13900,
 * "message": "文件上传成功",
 * "data": {
 * "file_id": 26926,// 文件编号
 * "file_url": "https://api.vsochina.com/data/uploads/2015/09/22/14878899555600a5c4a4bb1.jpg"   // 文件链接
 * }
 * }
 * <p/>
 * 返回码表： 13900 上传成功
 * <p/>
 * 错误码 => 错误信息
 * "13903" => "文件上传失败"
 * "9012" => "缺少用户名"
 * "13901" => "缺少对象类型"
 * "13902" => "缺少上传文件"
 * "13904" => "文件格式错误"
 * "13900" => "文件上传成功"
 * Created by leobert on 2016/7/27.
 */
public class UploadResBean {

    /**
     * 文件编号 我担心溢出
     */
    private String file_id;
    /**
     * 文件服务器地址
     */
    private String file_url;

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
