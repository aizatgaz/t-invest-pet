package org.invest.constants;

public class TInvestProperties {
    //app uri
    public static final String APP_BASE_PATH = "/api/v1/sandbox/accounts";
    public static final String ACCOUNT_SYNC = APP_BASE_PATH + "/sync";
    public static final String ACCOUNT_PAYINS = APP_BASE_PATH + "/{accountId}/pay-ins";
    public static final String ACCOUNT_REFRESH = APP_BASE_PATH + "/{accountId}/portfolio/refresh";
    public static final String ACCOUNT_ORDERS = APP_BASE_PATH + "/{accountId}/orders";
    public static final String ACCOUNT_ORDERS_SYNC = APP_BASE_PATH + "/{accountId}/orders/sync";
    public static final String ACCOUNT_ORDERS_BY_ID = APP_BASE_PATH + "/{accountId}/orders/{orderId}";
}
