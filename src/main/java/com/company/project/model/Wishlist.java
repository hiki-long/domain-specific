package com.company.project.model;

import javax.persistence.*;

public class Wishlist {
    @Id
    private String owner;

    private String items;

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
}