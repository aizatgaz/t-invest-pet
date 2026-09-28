package org.invest.constants;

public class TInvestProperties {
    public static final String TINVEST_URL = "https://sandbox-invest-public-api.tbank.ru/rest";

    //sandbox tinvest uri
    public static final String SANDBOX_SERVICE = "/tinkoff.public.invest.api.contract.v1.SandboxService";
    public static final String OPEN_SANDBOX_ACCOUNT = SANDBOX_SERVICE + "/OpenSandboxAccount";
    public static final String GET_SANDBOX_ACCOUNT = SANDBOX_SERVICE + "/GetSandboxAccounts";
    public static final String GET_SANDBOX_PORTFOLIO = SANDBOX_SERVICE + "/GetSandboxPortfolio";

    //app uri
    public static final String APP_BASE_PATH = "/api/v1/sandbox/accounts";
    public static final String ACCOUNT_SYNC = APP_BASE_PATH + "/sync";
    public static final String ACCOUNT_PAYINS = APP_BASE_PATH + "/{accountId}/pay-ins";
    public static final String ACCOUNT_REFRESH = APP_BASE_PATH + "/{accountId}/portfolio/refresh";
    public static final String ACCOUNT_ORDERS = APP_BASE_PATH + "/{accountId}/orders";
    public static final String ACCOUNT_ORDERS_SYNC = APP_BASE_PATH + "/{accountId}/orders/sync";
    public static final String ACCOUNT_ORDERS_BY_ID = APP_BASE_PATH + "/{accountId}/orders/{orderId}";
}
