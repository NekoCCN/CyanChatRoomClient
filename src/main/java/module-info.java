module cc.nekocc.cyanchatroom
{
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires com.google.gson;
    requires gson.extras;
    requires jbcrypt;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.javafx;
    requires AnimateFX;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.mybatis;
    requires jdk.compiler;

    opens cc.nekocc.cyanchatroom.features.login to javafx.fxml;
    opens cc.nekocc.cyanchatroom.features.chatpage to javafx.fxml;
    opens cc.nekocc.cyanchatroom.features.chatpage.chattab to javafx.fxml;
    opens cc.nekocc.cyanchatroom.features.chatpage.contact to javafx.fxml;
    opens cc.nekocc.cyanchatroom.features.chatpage.chattab.chatwindow to javafx.fxml;
    opens cc.nekocc.cyanchatroom.features.turntochatpage to javafx.fxml;
    opens cc.nekocc.cyanchatroom.features.setting to javafx.fxml;
    opens cc.nekocc.cyanchatroom to javafx.fxml;
    exports cc.nekocc.cyanchatroom;

    opens cc.nekocc.cyanchatroom.domain to com.google.gson;
    opens cc.nekocc.cyanchatroom.domain.client to com.google.gson;
    opens cc.nekocc.cyanchatroom.domain.goods to com.google.gson;
    opens cc.nekocc.cyanchatroom.domain.order to com.google.gson;
    opens cc.nekocc.cyanchatroom.domain.userstatus to com.google.gson;
    opens cc.nekocc.cyanchatroom.domain.messages to com.google.gson;
}