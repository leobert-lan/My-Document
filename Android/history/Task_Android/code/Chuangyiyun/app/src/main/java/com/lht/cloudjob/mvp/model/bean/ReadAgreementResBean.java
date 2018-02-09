package com.lht.cloudjob.mvp.model.bean;

/**
 * Created by chhyu on 2017/1/10.
 */

public class ReadAgreementResBean {
    /**
     * 协议内容
     */
    private String protocol_content;

    /**
     * 甲方签订状态（1=>签订，2=>未签订）
     */

    private int publisher_signed_status;

    /**
     * 甲方签订时间，时间戳
     */
    private long publisher_signed_time;

    /**
     * 乙方签订状态（1=>签订，2=>未签订）
     */
    private int bidder_signed_status;

    /**
     * 乙方签订时间
     */
    private long bidder_signed_time;

    /**
     * 是否驳回（1=>驳回，2=>未驳回）
     */
    private int is_rebut;

    /**
     * 协议生成时间，时间戳
     */
    private long create_time;

    /**
     * 需求实际成交价格，用于提示
     */
    private double real_cash;

    public String getProtocol_content() {
        return protocol_content;
    }

    public void setProtocol_content(String protocol_content) {
        this.protocol_content = protocol_content;
    }

    public int getPublisher_signed_status() {
        return publisher_signed_status;
    }

    public void setPublisher_signed_status(int publisher_signed_status) {
        this.publisher_signed_status = publisher_signed_status;
    }

    public long getPublisher_signed_time() {
        return publisher_signed_time;
    }

    public void setPublisher_signed_time(long publisher_signed_time) {
        this.publisher_signed_time = publisher_signed_time * 1000;
    }

    public int getBidder_signed_status() {
        return bidder_signed_status;
    }

    public void setBidder_signed_status(int bidder_signed_status) {
        this.bidder_signed_status = bidder_signed_status;
    }

    public long getBidder_signed_time() {
        return bidder_signed_time;
    }

    public void setBidder_signed_time(long bidder_signed_time) {
        this.bidder_signed_time = bidder_signed_time * 1000;
    }

    public int getIs_rebut() {
        return is_rebut;
    }

    public void setIs_rebut(int is_rebut) {
        this.is_rebut = is_rebut;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time *1000;
    }

    public double getReal_cash() {
        return real_cash;
    }

    public void setReal_cash(double real_cash) {
        this.real_cash = real_cash;
    }
}
