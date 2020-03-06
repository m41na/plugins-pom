package com.practicaldime.graphql.entity;

import javax.persistence.*;

@Entity
@Table(name = "tbl_account")
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    public Long id;
    @Column(name = "account_user", unique = true)
    public String username;
    @Column(name = "account_pass")
    public String password;
    @Column(name = "email_addr", unique = true)
    public String emailAddr;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_profile")
    public Profile profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
