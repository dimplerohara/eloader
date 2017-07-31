package com.hcl.neo.eloader.microservices.model;

import com.google.gson.Gson;
import java.util.List;

public class UserDetail {

    private List<BusinessGroup> bgList;
    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;

    public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public UserDetail() {

    }

    public List<BusinessGroup> getBgList() {
        return bgList;
    }

    public void setBgList(List<BusinessGroup> bgList) {
        this.bgList = bgList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
	public String toString() {
		return "UserDetail [bgList=" + bgList + ", userId=" + userId + ", userName=" + userName + ", userEmail="
				+ userEmail + ", userPassword=" + userPassword + "]";
	}
    
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
