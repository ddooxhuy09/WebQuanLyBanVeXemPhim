package movie.config;

import javax.servlet.http.HttpServletRequest;

import io.github.cdimascio.dotenv.Dotenv;

public class VNPayConfig {
	private static final Dotenv dotenv = Dotenv.load();
	public static final String vnp_TmnCode = dotenv.get("VNP_TMN_CODE");
    public static final String vnp_HashSecret = dotenv.get("VNP_HASH_SECRET");
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    
    public static String getReturnUrl(HttpServletRequest request) {
        String scheme = request.getScheme(); // http or https
        String serverName = request.getServerName(); // localhost or domain
        int serverPort = request.getServerPort(); // 8080 or 80
        String contextPath = request.getContextPath(); // /WebBanVeXemPhim
        return scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort) 
               + contextPath + "/booking/vnpay-payment-return";
    }
    
    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";
    public static final String vnp_CurrCode = "VND";
    public static final String vnp_Locale = "vn";
}