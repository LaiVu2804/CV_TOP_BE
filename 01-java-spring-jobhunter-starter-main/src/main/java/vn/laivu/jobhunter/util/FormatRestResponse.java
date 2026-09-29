package vn.laivu.jobhunter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.laivu.jobhunter.domain.response.RestResponse;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    // Tiêm ObjectMapper để giải quyết vấn đề String
    private final ObjectMapper objectMapper;

    public FormatRestResponse(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // [FIX BUG 2] KHÔNG can thiệp nếu Controller trả về File / Resource (để tải file không bị hỏng)
        return !Resource.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        // Bỏ qua các endpoint nội bộ của Swagger, OpenAPI, Actuator...
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith("/actuator")) {
            return body;
        }

        // [FIX] Nếu status >= 400, GlobalException đã lo việc tạo form chuẩn rồi, ta bỏ qua.
        if (status >= 400) {
            return body;
        }

        // Khởi tạo form chuẩn cho SUCCESS
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(status);
        restResponse.setData(body);

        // Đọc annotation message
        ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
        restResponse.setMessage(message != null ? message.value() : "Call API Success");

        // [FIX BUG 1] Giải quyết vấn đề String
        // Nếu Controller trả về String, ta vẫn gói nó vào RestResponse, 
        // nhưng sau đó DÙNG JACKSON (ObjectMapper) ÉP TOÀN BỘ VỀ STRING CHUẨN JSON
        if (body instanceof String) {
            // Thay đổi Content-Type thành Application JSON thủ công vì Spring đang dùng StringHttpMessageConverter
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                // Biến RestResponse thành chuỗi JSON chuẩn
                return objectMapper.writeValueAsString(restResponse);
            } catch (JsonProcessingException e) {
                return body; // Fallback an toàn
            }
        }

        return restResponse;
    }
}