package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class LoginVO {
    private String token;
    private String expireAt;
    private UserInfoVO userInfo;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getExpireAt() { return expireAt; }
    public void setExpireAt(String expireAt) { this.expireAt = expireAt; }
    public UserInfoVO getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfoVO userInfo) { this.userInfo = userInfo; }

    public static class UserInfoVO {
        private Long userId;
        private String username;
        private String realName;
        private List<String> roles;
        private List<String> perms;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public List<String> getPerms() { return perms; }
        public void setPerms(List<String> perms) { this.perms = perms; }
    }
}