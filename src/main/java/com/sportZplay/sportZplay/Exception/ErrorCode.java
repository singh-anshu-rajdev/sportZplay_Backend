package com.sportZplay.sportZplay.Exception;

public enum ErrorCode implements ErrorHandle{

    /**
     * The Error_sportZplay_2000
     */
    ERR_AP_2000(2000,"Internal Server Error"),

    /**
     * The Error_sportZplay_2001
     */
    ERR_AP_2001(2001,"Invalid Request Content"),

    /**
     * The Error_sportZplay_2002
     */
    ERR_AP_2002(2002,"Error in uploading Image"),

    /**
     * The Error_sportZplay_2003
     */
    ERR_AP_2003(2003,"Error in downloading Image"),

    /**
     * The Error_sportZplay_2004
     */
    ERR_AP_2004(2004,"File Not Found"),

    /**
     * The Error_sportZplay_2005
     */
    ERR_AP_2005(2005,"Invalid Otp"),

    /**
     * The Error_sportZplay_2006
     */
    ERR_AP_2006(2006,"Invalid Request"),

    /**
     * The Error_sportZplay_2007
     */
    ERR_AP_2007(2007,"Email Not Found"),

    /**
     * The Error_sportZplay_2008
     */
    ERR_AP_2008(2008,"Phone Number not Found"),

    /**
     * The Error_sportZplay_2008
     */
    ERR_AP_2009(2009,"Email is already Verified"),

    /**
     * The Error_sportZplay_2008
     */
    ERR_AP_2010(2010,"Phone Number is already Verified"),

    /**
     * The Error_sportZplay_2011
     */
    ERR_AP_2011(2011,"Image not found"),

    /**
     * The Error_sportZplay_2012
     */
    ERR_AP_2012(2012,"Invalid Username or password"),

    /**
     * The Error_sportZplay_2013
     */
    ERR_AP_2013(2013,"Invalid Username or password"),

    /**
     * The Error_sportZplay_2014
     */
    ERR_AP_2014(2014,"User already Registered"),

    ;
    /**
     * The ErrCode of type Integer
     */
    private final Integer errCode;

    /**
     * The message of type String
     */
    private final String message;

    /**
     *
     * @param errCode
     * @param message
     */
    ErrorCode(Integer errCode,String message){
        this.errCode = errCode;
        this.message = message;
    }

    /**
     *
     * @return ErrCode
     */
    @Override
    public Integer getErrorCode() {
        return this.errCode;
    }

    /**
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return this.message;
    }
}
