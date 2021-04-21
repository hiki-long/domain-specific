package com.company.project.model;

import javax.persistence.*;

public class Wishlist {
    @Column(name = "ownerID")
    private String ownerid;

    @Column(name = "itemIDs")
    private String itemids;

    /**
     * @return ownerID
     */
    public String getOwnerid() {
        return ownerid;
    }

    /**
     * @param ownerid
     */
    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
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
}