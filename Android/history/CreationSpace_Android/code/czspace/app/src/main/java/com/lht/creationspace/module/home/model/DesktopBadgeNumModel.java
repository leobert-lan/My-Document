//package com.lht.creationspace.module.home.model;
//
//import com.lht.creationspace.base.model.dbmodel.AbsDbModel;
//import com.lht.creationspace.base.model.dbmodel.IDataBaseExecutorHandle;
//import com.lht.creationspace.base.model.dbmodel.IModelTask;
//import com.lht.creationspace.cfg.DBConfig;
//import com.lht.creationspace.module.home.model.pojo.DesktopBadgeNumBean;
//
///**
// * <p><b>Package:</b> com.lht.creationspace.module.home.model </p>
// * <p><b>Project:</b> czspace </p>
// * <p><b>Classname:</b> DesktopBadgeNumModel </p>
// * <p><b>Description:</b> TODO </p>
// * Created by leobert on 2017/6/15.
// */
//
//public class DesktopBadgeNumModel extends AbsDbModel<DesktopBadgeNumBean>
//implements AbsDbModel.StringKeyDbModel{
//    public DesktopBadgeNumModel() {
//        super(DesktopBadgeNumBean.class);
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return DBConfig.BadgeNumberDb.DB_NAME;
//    }
//
//    @Override
//    public IDataBaseExecutorHandle queryById(final String key, OnQueryTaskFinishListener listener) {
//        return startTask(new IModelTask<DesktopBadgeNumBean>() {
//            @Override
//            public DesktopBadgeNumBean startTask() {
//                DesktopBadgeNumBean bean =
//                        getConnectedInstance().queryById(key,DesktopBadgeNumBean.class);
//                getConnectedInstance().close();
//                return bean;
//            }
//        });
//    }
//}
