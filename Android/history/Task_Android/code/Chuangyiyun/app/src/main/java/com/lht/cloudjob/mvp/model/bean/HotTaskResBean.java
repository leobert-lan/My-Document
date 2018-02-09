package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.HotTaskListModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> HotSearchResBean
 * <p><b>Description</b>: 最火需求……注意数组
 * {
 * "ret": 0,
 * "status": 1,
 * "message": "成功",
 * "data": [
 * {
 * "task_bn": "51941720",
 * "task_cash": "4",
 * "title": "多人参与的",
 * "model": "1",
 * "total_bids": "300",
 * "create_time": "1470125450",
 * "sub_end_time": "1470240000",
 * "sub_end_time_alias": ""
 * },
 * {},
 * ...
 * ]
 * }
 * <p/>
 * Created by leobert on 2016/8/16.
 * to see Model at {@link HotTaskListModel}
 * to see API at {@link IRestfulApi.HotTaskListApi}
 */
public class HotTaskResBean extends TaskResBean{

//    {
//        "task_bn": "51941720",
//            "task_cash": "4",
//            "title": "多人参与的",
//            "model": "1",
//            "is_mark": "0",
//            "status": 10,
//            "total_bids": "300",
//            "create_time": "1470125450",
//            "sub_end_time": "1470240000",
//            "model_name": "悬赏",
//            "status_alias": "公示中",
//            "sub_end_time_alias": "",
//            "top": "0",
//            "urgent": "0"
//    },
    //不存在差异性


}
