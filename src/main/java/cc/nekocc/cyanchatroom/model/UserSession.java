package cc.nekocc.cyanchatroom.model;

import cc.nekocc.cyanchatroom.domain.User;

public final class UserSession
{
    private static UserSession instance_;
    private User current_user_;

    private UserSession()
    {  }

    public static UserSession getInstance()
    {
        if (instance_ == null)
        {
            instance_ = new UserSession();
        }
        return instance_;
    }

    public void loginUser(User user)
    {
        this.current_user_ = user;
    }

    public void logoutUser()
    {
        this.current_user_ = null;
    }

    public User getCurrentUser()
    {
        if (current_user_ == null)
        {
            throw new IllegalStateException("没有用户登录，无法获取当前用户。");
        }
        return current_user_;
    }
}