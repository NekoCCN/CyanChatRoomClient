package cc.nekocc.cyanchatroom.model.callback;

@FunctionalInterface
public interface ResponseCallback
{
    /**
     * Callback method to handle the response.
     *
     * @param response The response object, can be null if no response is available.
     */
    void onResponse(Object response);
}
