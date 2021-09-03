package com.company.project.model;

import java.util.Date;
import javax.persistence.*;

public class Orderlist {
    @Id
    @Column(name = "UUID")
    private String uuid;

    private String items;

    private String seller;

    private String buyer;

    private Float price;

    private String bill;

    private String delivery;

    private Date ordertime;

    private Boolean paid;

    private Boolean finish;

    private String comment;

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
     * @return items
     */
    public String getItems() {
        return items;
    }

    /**
     * @param items
     */
    public void setItems(String items) {
        this.items = items;
    }

    /**
     * @return seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * @param seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * @return buyer
     */
    public String getBuyer() {
        return buyer;
    }

    /**
     * @param buyer
     */
    public void setBuyer(String buyer) {
        this.buyer = buyer;
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
     * @return bill
     */
    public String getBill() {
        return bill;
    }

    /**
     * @param bill
     */
    public void setBill(String bill) {
        this.bill = bill;
    }

    /**
     * @return delivery
     */
    public String getDelivery() {
        return delivery;
    }

    /**
     * @param delivery
     */
    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    /**
     * @return time
     */
    public Date getTime() {
        return ordertime;
    }

    /**
     * @param time
     */
    public void setTime(Date time) {
        this.ordertime = time;
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
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}