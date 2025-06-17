<%@ page import="java.awt.Desktop, java.net.URI" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mở Dashboard Power BI</title>
</head>
<body>
    <h2>Dashboard Power BI</h2>
    <iframe 
        title="WebBanVeXemPhim" 
        width="1140" 
        height="541.25" 
        src="https://app.powerbi.com/reportEmbed?reportId=bf0dafe6-fdf4-4273-8e60-476de3f85a7a&autoAuth=true&ctid=447080b4-b9c6-4b0b-92fd-b543a68b4e97" 
        frameborder="0" 
        allowFullScreen="true">
    </iframe>

    <script>
        // JavaScript để thêm access token vào yêu cầu (nếu cần thiết)
        // Lưu ý: Access token nên được xử lý ở phía server để bảo mật, không nên nhúng trực tiếp trong mã client
        const accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL2FuYWx5c2lzLndpbmRvd3MubmV0L3Bvd2VyYmkvYXBpIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvNDQ3MDgwYjQtYjljNi00YjBiLTkyZmQtYjU0M2E2OGI0ZTk3LyIsImlhdCI6MTc0NTI2NTc0MSwibmJmIjoxNzQ1MjY1NzQxLCJleHAiOjE3NDUyNjk2NDEsImFpbyI6ImsyUmdZTERJTXk1My9uem55NlV6VE90RkJDVFlBUT09IiwiYXBwaWQiOiJlNzdjZGZhYi1kNDJiLTQwOGEtODZhZS1hYWQ4MjRiYjIzODAiLCJhcHBpZGFjciI6IjEiLCJpZHAiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC80NDcwODBiNC1iOWM2LTRiMGItOTJmZC1iNTQzYTY4YjRlOTcvIiwiaWR0eXAiOiJhcHAiLCJvaWQiOiJiMTExMzUzNS04ZTY1LTQxMTUtOTBkYi0zMTAyOGJmYzE1NjQiLCJyaCI6IjEuQVZRQXRJQndSTWE1QzB1U19iVkRwb3RPbHdrQUFBQUFBQUFBd0FBQUFBQUFBQUJVQUFCVUFBLiIsInN1YiI6ImIxMTEzNTM1LThlNjUtNDExNS05MGRiLTMxMDI4YmZjMTU2NCIsInRpZCI6IjQ0NzA4MGI0LWI5YzYtNGIwYi05MmZkLWI1NDNhNjhiNGU5NyIsInV0aSI6IkpfYm95X0l4RGstb3Z0a0NWbGNxQUEiLCJ2ZXIiOiIxLjAiLCJ4bXNfaWRyZWwiOiIzMCA3In0.RCjE2EtWVP7jvlQ0ZSLk_2oM_NrjP0B-BXZHI7x9oEEhfKlsgAXmMIIALiXhgO5qp4Gj4Vip8dwvwvSr6zt8MHBl7xTrtaIUk6tF1WDclmx9UjAg8ge0kz_oSX_dbnHMbrydlNbT6nXlsso4d5OF6R51kLy-xkPWmkcl3lYH-dZSfAMYavCuQcBX47sEyrJbA8UwbVLe0hM_-ko6uwLBUtZGaKQ36Q37ecZBc69ymOEYi-vCtHgJPluztFpbBVC4NNLMRVLECJVv5_5knn1BtQn97ASFNFzMOqVhuvBPuKyDgxjfYe4gZwzhtg36JQ2ekdU-voxhsOm6SL5MnnSRHg";
        
        // Nếu cần gửi access token qua header, sử dụng fetch hoặc XMLHttpRequest
        // Ví dụ: Gửi yêu cầu đến Power BI API để xác thực
        function fetchPowerBIReport() {
            fetch('https://app.powerbi.com/reportEmbed?reportId=bf0dafe6-fdf4-4273-8e60-476de3f85a7a', {
                headers: {
                    'Authorization': 'Bearer ' + accessToken
                }
            })
            .then(response => response.json())
            .then(data => console.log(data))
            .catch(error => console.error('Error:', error));
        }

        // Gọi hàm nếu cần
        // fetchPowerBIReport();
    </script>
</body>
</html>