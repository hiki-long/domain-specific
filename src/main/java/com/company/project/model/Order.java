package com.company.project.model;

import java.util.Date;
import javax.persistence.*;

public class Order {
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "itemIDs")
    private String itemids;

    @Column(name = "sellerID")
    private String sellerid;

    @Column(name = "buyerID")
    private String buyerid;

    private Float price;

    @Column(name = "billID")
    private String billid;

    @Column(name = "deliveryID")
    private String deliveryid;

    private Date time;

    private Boolean paid;

    private Boolean finish;

    @Column(name = "commentID")
    private String commentid;

    /**
     * @return UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return itemIDs
     */
    public String getItemids() {
        return itemids;
    }

    /**
     * @param itemids
     */
    public void setItemids(String itemids) {
        this.itemids = itemids;
    }

    /**
     * @return sellerID
     */
    public String getSellerid() {
        return sellerid;
    }

    /**
     * @param sellerid
     */
    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    /**
     * @return buyerID
     */
    public String getBuyerid() {
        return buyerid;
    }

    /**
     * @param buyerid
     */
    public void setBuyerid(String buyerid) {
        this.buyerid = buyerid;
    }

    /**
     * @return price
     */
    public Float getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
     * @return billID
     */
    public String getBillid() {
        return billid;
    }

    /**
     * @param billid
     */
    public void setBillid(String billid) {
        this.billid = billid;
    }

    /**
     * @return deliveryID
     */
    public String getDeliveryid() {
        return deliveryid;
    }

    /**
     * @param deliveryid
     */
    public void setDeliveryid(String deliveryid) {
        this.deliveryid = deliveryid;
    }

    /**
     * @return time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return paid
     */
    public Boolean getPaid() {
        return paid;
    }

    /**
     * @param paid
     */
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    /**
     * @return finish
     */
    public Boolean getFinish() {
        return finish;
    }

    /**
     * @param finish
     */
    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    /**
     * @return commentID
     */
    public String getCommentid() {
        return commentid;
    }

    /**
     * @param commentid
     */
    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}