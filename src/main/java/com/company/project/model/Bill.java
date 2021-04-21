package com.company.project.model;

import javax.persistence.*;

public class Bill {
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "orderID")
    private String orderid;

    private Float price;

    private String type;

    @Column(name = "paymentID")
    private String paymentid;

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
     * @return orderID
     */
    public String getOrderid() {
        return orderid;
    }

    /**
     * @param orderid
     */
    public void setOrderid(String orderid) {
        this.orderid = orderid;
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
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return paymentID
     */
    public String getPaymentid() {
        return paymentid;
    }

    /**
     * @param paymentid
     */
    public void setPaymentid(String paymentid) {
        this.paymentid = paymentid;
    }
}