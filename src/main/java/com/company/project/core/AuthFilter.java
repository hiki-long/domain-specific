package com.company.project.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "userFilter",urlPatterns = {"/user/list","/user/login","/user/changePasswd","/user/changeUsername","/user/forgetPasswd","/user/logout"})
public class AuthFilter implements Filter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private Auth auth;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("filter init");
        auth = new Auth(stringRedisTemplate);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        System.out.println("处理请求");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if(session!=null){
            if(session.getAttribute("uuid")!=null){
                String uuid = session.getAttribute("uuid").toString();
                System.out.println(uuid);
                if(auth.hasSession(uuid)){
                    chain.doFilter(request, response);
                }else{
                    response.sendError(401);
                }
            }else {
                response.sendError(401);
            }
        }else{
            response.sendError(401);
        }

    }

    @Override
    public void destroy() {

    }
}
