package com.matrixdeveloper.battle.club.tournaments.config;

import com.matrixdeveloper.battle.club.tournaments.BuildConfig;

public class config {

    //   http://159.65.128.120/battleworld/paytm/generateChecksum.php
    public static final String main = "https://matrixdeveloper.com/";
//    public static String mainurl = "http://www.battleworld.in/play/";
    public static final String mainurl = "https://matrixdeveloper.com/battleworld/";
    public static final String mainimg = mainurl + "matchimg/";
    public static final String howtojoin = "https://matrixdeveloper.com/?page_id=444";
    public static final String privacypolicy = "https://sites.google.com/view/mohmadprivacypolicy"; //"http://159.65.128.120/?page_id=3";
    public static final String paytmchecksum = mainurl + "paytm/";
    public static final String getOrderID = mainurl +"razorpay.php";
    public static final String bannerImages = mainurl +"get_banner.php";

    public static final String apkupdateurl = "https://matrixdeveloper.com/battleworld/app.apk";

    public static final String youtubechannel = "https://www.youtube.com/channel/UCMaEQ1Q9ES5970Br8oWo7jA";


    //Payment Gateway
    //Type true to visible
    //Type false to invisible
    public static final boolean paytm = true;
    public static final boolean paypal = true;
    public static final boolean instamojo = true;

    // RazorPay Production
    public static final String RAZOR_KEY_ID = "rzp_live_CWek9buoUXIXDc";
    public static final String RAJOR_KEY_SECRET = "qOAhqzjqezXAA8iFEFs2hwNI";

    // Paytm
//    // Test API Details
//    public static String MID = "JHKOET16717750843118";
//    public static String WEBSITE = "WEBSTAGING";
//    public static String INDUSTRY_TYPE_ID = "Retail";
//    public static String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";

    // Production
    public static final String MID = "xmHOCa32667710380797";
    public static final String WEBSITE = "DEFAULT";
    public static final String INDUSTRY_TYPE_ID = "Retail";
    public static final String CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";

}
