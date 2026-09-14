package com.rural.agri.interceptor;

import com.rural.agri.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            writeError(response);
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(auth.substring(7));
            request.setAttribute("userId", claims.getSubject());
            request.setAttribute("role", claims.get("role"));

            // 后台管理接口仅限管理员角色
            if (request.getRequestURI().startsWith("/api/admin/")) {
                String role = String.valueOf(claims.get("role"));
                if (!"ADMIN".equals(role)) {
                    writeForbidden(response);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            writeError(response);
            return false;
        }
    }

    private void writeError(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
    }

    private void writeForbidden(HttpServletResponse response) throws Exception {
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
    }
}
