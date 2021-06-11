package com.company.project.model;

import javax.persistence.*;

public class Bill {
    @Id
    private String uuid;

    private String orderuuid;

    private Float price;

    private String type;

    private String payment;

    /**
     * @return uuid
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
     * @return order
     */
    public String getOrder() {
        return orderuuid;
    }

    /**
     * @param orderuuid
     */
    public void setOrder(String orderuuid) {
        this.orderuuid = orderuuid;
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
     * @return payment
     */
    public String getPayment() {
        return payment;
    }

    /**
     * @param payment
     */
    public void setPayment(String payment) {
        this.payment = payment;
    }
}