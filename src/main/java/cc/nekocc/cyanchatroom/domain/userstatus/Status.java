package cc.nekocc.cyanchatroom.domain.userstatus;//package cc.nekocc.cyanchatroom.domain.userstatus;

public enum Status {
    ONLINE,
    BUSY,
    AWAY,
    DO_NOT_DISTURB,
    OFFLINE;

    @Override
    public String toString() {
        return switch (this) {
            case ONLINE -> "在线";
            case BUSY -> "忙碌";
            case AWAY -> "离开";
            case DO_NOT_DISTURB -> "请勿打扰";
            case OFFLINE -> "离线";
        };

    }

    // 用于好友栏显示
    public String toDisplayString(){
        return switch (this)
        {
            case ONLINE -> "在线";
            case BUSY -> "忙碌";
            case AWAY -> "离开";
            case DO_NOT_DISTURB -> "请勿打扰";
            case OFFLINE -> "离线";
        };
    }

    public String getColor() {
        return switch (this)
        {
            case ONLINE -> "#00FF00";
            case BUSY -> "#696969";
            case AWAY -> "#9400D3";
            case DO_NOT_DISTURB -> "#191970";
            case OFFLINE -> "#A52A2A";
        };
    }

    public static Status getRandomStatus() {
        return Status.values()[(int) (Math.random() * Status.values().length)];
    }

}

