package com.company.project.model;

import javax.persistence.*;

public class User {
    @Id
    private String uuid;

    private String email;

    private String username;

    private String role;

    private String avatar;

    private Float rank;

    private String passwd;

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
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return rank
     */
    public Float getRank() {
        return rank;
    }

    /**
     * @param rank
     */
    public void setRank(Float rank) {
        this.rank = rank;
    }

    /**
     * @return passwd
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * @param passwd
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}