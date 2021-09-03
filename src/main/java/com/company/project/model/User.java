package com.company.project.model;

import javax.persistence.*;

public class User {
    @Id
    private String uuid;

    private String email;

    private String username;

    private String userrole;

    private String avatar;

    private Float userrank;

    private String passwd;

    /**
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    public String getUserRole() {
        return userrole;
    }

    public void setUserRole(String userRole) {
        this.userrole = userRole;
    }

    public Float getUserRank() {
        return userrank;
    }

    public void setUserRank(Float userRank) {
        this.userrank = userRank;
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