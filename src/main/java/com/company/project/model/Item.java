package com.company.project.model;

import javax.persistence.*;

public class Item {
    @Column(name = "UUID")
    private String uuid;

    private String name;

    private String owner;

    private Integer remain;

    @Column(name = "onSale")
    private Boolean onsale;

    private String description;

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return remain
     */
    public Integer getRemain() {
        return remain;
    }

    /**
     * @param remain
     */
    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    /**
     * @return onSale
     */
    public Boolean getOnsale() {
        return onsale;
    }

    /**
     * @param onsale
     */
    public void setOnsale(Boolean onsale) {
        this.onsale = onsale;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}