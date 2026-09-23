package vn.laivu.jobhunter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.laivu.jobhunter.service.ServiceImpl.UserServiceImpl;
import vn.laivu.jobhunter.unity.Permission;
import vn.laivu.jobhunter.unity.Role;
import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.util.error.PermissionException;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    UserServiceImpl userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        // Log request details
        logger.debug("Processing permission check for request");
        logger.debug("Path: {}", path);
        logger.debug("HTTP Method: {}", httpMethod);
        logger.debug("Request URI: {}", requestURI);


        // check permission
        // lấy email từ spring security
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email != null && !email.isEmpty()) {
            logger.debug("Checking permissions for user: {}", email);
            User user = userService.handleGetUserByUserName(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    logger.debug("User has role: {}", role.getName());
                    List<Permission> permissions = role.getPermissions(); // error if haven't session => fix: @Transactional create session

                    boolean isAllow = permissions
                            .stream()
                            .anyMatch(
                                    item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));

                    if (isAllow) {
                        logger.debug("Permission granted for {} {} to user {}", httpMethod, path, email);
                    } else {
                        logger.warn("Permission denied for {} {} to user {} with role {}", httpMethod, path, email, role.getName());
                        throw new PermissionException("Bạn ko co quyền truy cập endpoint này!");
                    }
                } else {
                    logger.warn("User {} has no role assigned", email);
                    throw new PermissionException("Bạn ko có quyền truy cập endpoint này!");
                }
            } else {
                logger.warn("User not found for email: {}", email);
            }
        } else {
            logger.debug("No authenticated user found for request");
        }
        // không cần xử lý email null vì nếu email null thì đã bị chặn
        // Security (.anyRequest().authenticated())
        // Trả về true cho phép đi tới controller, false thì chặn lại (chặn đã xử lý Exception trên)
        return true;
    }
}
