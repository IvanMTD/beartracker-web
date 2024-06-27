package ru.beartrack.web.utils;

public class CookieUtil {
    private static CookieUtil instance = null;

    private final String REFRESH;
    private final String ACCESS;
    private final String SESSION;

    protected CookieUtil(){
        REFRESH = "app-bt-rt";
        ACCESS = "app-bt-at";
        SESSION = "app-bt-st";
    }

    public static CookieUtil getInstance(){
        if(instance == null){
            instance = new CookieUtil();
        }
        return instance;
    }

    public String getREFRESH() {
        return REFRESH;
    }

    public String getACCESS() {
        return ACCESS;
    }

    public String getSESSION() {
        return SESSION;
    }
}
