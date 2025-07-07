package cc.nekocc.cyanchatroom.domain.userstatus;



public enum Status {
    ONLINE,
    BUSY,
    AWAY,
    DoNotDisturb,
    OFFLINE,
    INVISIBLE;

    @Override
    public String toString() {
        return switch (this) {
            case ONLINE -> "在线";
            case BUSY -> "忙碌";
            case AWAY -> "离开";
            case DoNotDisturb -> "请勿打扰";
            case OFFLINE, INVISIBLE -> "离线";
        };

    }

    public String getColor() {
        return switch (this)
                {
                    case ONLINE -> "#00FF00";
                    case BUSY -> "#696969";
                    case AWAY -> "#9400D3";
                    case DoNotDisturb -> "#191970";
                    case OFFLINE, INVISIBLE -> "#A52A2A";
                };
    }

    public static Status getRandomStatus() {
        return Status.values()[(int) (Math.random() * Status.values().length)];
    }
}
