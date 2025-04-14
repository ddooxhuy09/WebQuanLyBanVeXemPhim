package movie.config;

import javax.servlet.http.HttpServletRequest;

public class VNPayConfig {
    public static final String vnp_TmnCode = "8LOX36Y3"; 
    public static final String vnp_HashSecret = "ASDLOP3CSDTZ2ZJ921UBFHAFF6YFY8EM";
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    
    // Phương thức để tạo URL động dựa trên context path
    public static String getReturnUrl(HttpServletRequest request) {
        String serverName = request.getServerName();
        String serverPort = request.getServerPort() == 80 ? "" : ":" + request.getServerPort();
        String contextPath = request.getContextPath();
        return "http://" + serverName + serverPort + contextPath + "/booking/vnpay-payment-return";
    }
    
    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";
    public static final String vnp_CurrCode = "VND";
    public static final String vnp_Locale = "vn";
}