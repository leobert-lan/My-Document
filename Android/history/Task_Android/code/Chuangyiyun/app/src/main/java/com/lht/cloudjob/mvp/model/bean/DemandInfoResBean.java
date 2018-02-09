package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.customview.WorkItemView;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.DemandInfoModel;
import com.lht.cloudjob.util.numeric.NumericalUtils;
import com.lht.cloudjob.util.time.TimeUtil;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandInfoResBean
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/23
 * <p/>
 * to see Model at {@link DemandInfoModel}
 * to see API at{@link IRestfulApi.DemandInfoApi}
 */
public class DemandInfoResBean {

    /**
     * 需求编号，新版
     */
    private String task_bn;

    /**
     * 发布者用户名
     */
    private String username;

    /**
     * 预算金额
     */
    private double task_cash;

    /**
     * 预算最小金额
     */
    private double min_cash;

    /**
     * 预算最大金额
     */
    private double max_cash;

    /**
     * 实际成交金额
     */
    private double real_cash;

    /**
     * 需求标题
     */
    private String title;

    /**
     * 招标类型（1=>明标，2=>暗标）
     */
    private int is_mark;

    /**
     * 是否购买隐藏需求服务
     */
    private boolean hide;

    /**
     * 置顶次数，大于0表示有购买置顶服务
     */
    private int top;

    /**
     * 加急次数，大于0表示有购买加急服务
     */
    private int urgent;

    /**
     * 需求类型（1=>悬赏，2=>招标，3=>雇佣）
     */
    private int model;

    public boolean isReward() {
        return model == 1;
    }

    /**
     * 状态
     */
    private int status;

    /**
     * 状态别名
     */
    private String status_alias;

    /**
     * 是否承诺选稿（1=>是，2=>否）
     */
    private int is_select;

    /**
     * 发布人联系方式
     */
    private String mobile;

    /**
     * 发布时间，时间戳
     */
    private long create_time;

    /**
     * 总投标数
     */
    private int total_bids;

    /**
     * 描述，具体要求
     */
    private String description;

    public static final int STEP_PUBLIC_NOTIFICATION = 4;

    /**
     * 阶段（1=>发布，2=>投稿，3=>选稿，4=>公示，5=>制作，6=>结束）
     */
    private int step;

    /**
     * 交稿截止时间，时间戳
     */
    private long sub_end_time;

    /**
     * 是否已托管
     */
    private boolean deposit;

    /**
     * 任务类型别名
     */
    private String model_name;

    /**
     * 交稿截止时间格式转换
     */
    private String sub_end_time_alias;

    /**
     * 发布者相关信息
     */
    private Publisher publisher;

    /**
     * 当前访问者是否已关注
     */
    private Favored favored;

    private ArrayList<Work> works;

    private ArrayList<AttachmentExt> attachment;

    private ArrayList<Integer> operate;

    /**
     * 驳回理由 暂不使用
     */
    private String rejection;

    private String link;

    public String getTask_bn() {
        return task_bn;
    }

    public void setTask_bn(String task_bn) {
        this.task_bn = task_bn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getTask_cash() {
        return task_cash;
    }

    public String getStatus_alias() {
        return status_alias;
    }

    public void setStatus_alias(String status_alias) {
        this.status_alias = status_alias;
    }

    public int getIs_mark() {
        return is_mark;
    }

    public void setIs_mark(int is_mark) {
        this.is_mark = is_mark;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public ArrayList<AttachmentExt> getAttachment() {
        return attachment;
    }

    public void setAttachment(ArrayList<AttachmentExt> attachment) {
        this.attachment = attachment;
    }

    public String getPriceString() {
        if (is_mark == 2) {
            return "暗标";
        }
        String _p = NumericalUtils.decimalFormat(getTask_cash(), 2);
        return new StringBuilder("￥").append(_p).toString();
    }

    public void setTask_cash(double task_cash) {
        this.task_cash = task_cash;
    }

    public double getMin_cash() {
        return min_cash;
    }

    public void setMin_cash(double min_cash) {
        this.min_cash = min_cash;
    }

    public double getMax_cash() {
        return max_cash;
    }

    public void setMax_cash(double max_cash) {
        this.max_cash = max_cash;
    }

    public double getReal_cash() {
        return real_cash;
    }

    public void setReal_cash(double real_cash) {
        this.real_cash = real_cash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIs_select() {
        return is_select;
    }

    public void setIs_select(int is_select) {
        this.is_select = is_select;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time * 1000;
    }

    public int getTotal_bids() {
        return total_bids;
    }

    public void setTotal_bids(int total_bids) {
        this.total_bids = total_bids;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public long getSub_end_time() {
        return sub_end_time;
    }

    public void setSub_end_time(long sub_end_time) {
        this.sub_end_time = sub_end_time * 1000;
    }

    public boolean isDeposit() {
        return deposit;
    }

    public String getDelegateString() {
        if (isDeposit()) {
            return "资金已托管";
        } else {
            return "资金未托管";
        }
    }

    public void setDeposit(boolean deposit) {
        this.deposit = deposit;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getSub_end_time_alias() {
        if (getStep() == 2) {
            // if (StringUtil.isEmpty(sub_end_time_alias)) {
            long temp = getSub_end_time() - TimeUtil.getCurrentTimeInLong();
            int d = (int) (temp / TimeUtil.DAY_MILLIS);
            long _millis = temp % TimeUtil.DAY_MILLIS;
            int h = (int) (_millis / TimeUtil.HOUR_MILLIS);
            if (d > 0) {
                return String.format("%d天%d小时后截稿", d, h);
            } else if (h > 0) {
                return String.format("%d小时后截稿", h);
            } else {
                return "已截稿";
            }
            //            return String.format("%d天%d小时后截稿", d, h);
            //}
        } else {
            return trans2StepAlias(getStep());
        }
//        return sub_end_time_alias;
    }

    public void setSub_end_time_alias(String sub_end_time_alias) {
        this.sub_end_time_alias = sub_end_time_alias;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Favored getFavored() {
        return favored;
    }

    public void setFavored(Favored favored) {
        this.favored = favored;
    }

    public ArrayList<Work> getWorks() {
        return works;
    }

    public void setWorks(ArrayList<Work> works) {
        this.works = works;
    }

    public ArrayList<Integer> getOperate() {
        return operate;
    }

    public void setOperate(ArrayList<Integer> operate) {
        this.operate = operate;
    }

    public String getRejection() {
        return rejection;
    }

    public void setRejection(String rejection) {
        this.rejection = rejection;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    //////////////////////////////////////////////////////////////////////////////////


    public static class Publisher {
        private String username;

        private String nickname;

        private String avatar;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    /**
     * 关注信息
     */
    public static class Favored {
        private boolean publisher;
        private boolean task;

        public boolean isPublisher() {
            return publisher;
        }

        public void setPublisher(boolean publisher) {
            this.publisher = publisher;
        }

        public boolean isTask() {
            return task;
        }

        public void setTask(boolean task) {
            this.task = task;
        }
    }


    public static class Work {
        private int id;

        private String username;

        private String description;

        /**
         * second to millis
         */
        private long create_time;

        /**
         * 是否隐藏稿件（1=>显示，2=>隐藏）
         */
        private int is_mark;

        private String attachments;

        private ArrayList<AttachmentExt> work_ext;

        /**
         * 1 未中标
         * 2 备选
         * 3 中标
         * 4 被举报
         */
        private int status;

        private String status_alias;

        private String nickname;

        private String avatar;

        private int days;

        private double price;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getCreate_time() {
            return create_time;
        }

        public void setCreate_time(long create_time) {
            this.create_time = create_time * 1000;
        }

        public int getIs_mark() {
            return is_mark;
        }

        public boolean isHidden() {
            return is_mark == 2;
        }

        public void setIs_mark(int is_mark) {
            this.is_mark = is_mark;
        }

        public String getAttachments() {
            return attachments;
        }

        public void setAttachments(String attachments) {
            this.attachments = attachments;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatus_alias() {
            return status_alias;
        }

        public void setStatus_alias(String status_alias) {
            this.status_alias = status_alias;
        }

        public ArrayList<AttachmentExt> getWork_ext() {
            return work_ext;
        }

        public void setWork_ext(ArrayList<AttachmentExt> work_ext) {
            this.work_ext = work_ext;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getPriceString() {
            return "￥" + NumericalUtils.decimalFormat(getPrice(), NumericalUtils.SplitMode.THOUSAND, 2);
        }

        public String getInfoAlias(WorkItemView.Type type) {
            switch (type) {
                case hideBid:
                    return "报价：" + getPriceString() + "    " + "工期：" + getDays() + "天";
                case openBid:
                    return "报价：" + getPriceString() + "    " + "工期：" + getDays() + "天";
                default:
                    return "";
            }
        }

        public boolean isOptionalWork() {
            return getStatus() == 2;
        }

        public boolean isHittedWork() {
            return getStatus() == 3;
        }
    }

    /**
     * 文件扩展信息
     */
    public static class AttachmentExt {
        /**
         * 源文件路径
         */
        private String file_path;

        /**
         * 名称
         */
        private String file_name;


        /**
         * 文件大小 bytes
         */
        private long file_size;

        /**
         * 文件大小别名，单位（B，KB，MB，TG，GB）
         */
        private String file_size_alias;

        /**
         * 文件类型
         */
        private String mime;

        /**
         * 下载路径
         */
        private String url_download;

        /**
         * 缩略图路径
         */
        private String url_thumbnail;


        /**
         * 预览图路径
         */
        private String url_preview;

        /**
         * 文件扩展名
         */
        private String file_ext;

        public String getFile_ext() {
            return file_ext;
        }

        public void setFile_ext(String file_ext) {
            this.file_ext = file_ext;
        }

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

        public long getFile_size() {
            return file_size;
        }

        public void setFile_size(long file_size) {
            this.file_size = file_size;
        }

        public String getFile_size_alias() {
            return file_size_alias;
        }

        public void setFile_size_alias(String file_size_alias) {
            this.file_size_alias = file_size_alias;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public String getUrl_download() {
            return url_download;
        }

        public void setUrl_download(String url_download) {
            this.url_download = url_download;
        }

        public String getUrl_thumbnail() {
            return url_thumbnail;
        }

        public void setUrl_thumbnail(String url_thumbnail) {
            this.url_thumbnail = url_thumbnail;
        }

        public String getUrl_preview() {
            return url_preview;
        }

        public void setUrl_preview(String url_preview) {
            this.url_preview = url_preview;
        }

//        @Deprecated
//        public boolean isSupportedByVsoPreviewRules() {
//            return !StringUtil.isEmpty(getUrl_preview());
//        }
    }


    /**
     * 1   发布
     * 2   投标
     * 3   选稿
     * 4   公示
     * 5   制作
     * 6   结束
     *
     * @param step step
     * @return alias
     */
    public static String trans2StepAlias(int step) {
        switch (step) {
            case 1:
                return "发布";
            case 2:
                return "投稿中";
            case 3:
                return "选稿中";
            case 4:
                return "公示中";
            case 5:
                return "制作中";
            case 6:
                return "结束";
            default:
                return "";
        }
    }
}
