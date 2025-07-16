package cc.nekocc.cyanchatroom.features.chatpage.contact;

import cc.nekocc.cyanchatroom.domain.userstatus.Status;

@FunctionalInterface
public interface SyncDataCallBack {
    void syncContact(String  username, Status status);
}
