package com.example.scorecards.ui;

public class FirebaseFriend {
    String friendHandle;
    String friendRating;
    String friendAvatar;

    public FirebaseFriend() {
    }

    public String getfriendHandle() {
        return friendHandle;
    }

    public void setfriendHandle(String friendHandle) {
        this.friendHandle = friendHandle;
    }

    public String getFriendRating() {
        return friendRating;
    }

    public void setFriendRating(String friendRating) {
        this.friendRating = friendRating;
    }

    public String getFriendAvatar() {
        return friendAvatar;
    }

    public void setFriendAvatar(String friendAvatar) {
        this.friendAvatar = friendAvatar;
    }

    public FirebaseFriend(String friendHandle, String friendRating, String friendAvatar) {
        this.friendHandle = friendHandle;
        this.friendRating = friendRating;
        this.friendAvatar = friendAvatar;
    }

    public Friend toFriend() {
        return new Friend(friendHandle, friendRating, friendAvatar);
    }
}
