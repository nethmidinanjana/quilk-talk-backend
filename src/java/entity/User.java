package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "mobile", length = 12, nullable = false)
    private String mobile;

    @Column(name = "username", length = 45, nullable = false)
    private String username;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @Column(name = "registered_datetime", nullable = false)
    private Date registered_datetime;

    @ManyToOne
    @JoinColumn(name = "user_status_id")
    private User_Status user_Status;

    public User() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the registered_datetime
     */
    public Date getRegistered_datetime() {
        return registered_datetime;
    }

    /**
     * @param registered_datetime the registered_datetime to set
     */
    public void setRegistered_datetime(Date registered_datetime) {
        this.registered_datetime = registered_datetime;
    }

    /**
     * @return the user_Status
     */
    public User_Status getUser_Status() {
        return user_Status;
    }

    /**
     * @param user_Status the user_Status to set
     */
    public void setUser_Status(User_Status user_Status) {
        this.user_Status = user_Status;
    }

}
