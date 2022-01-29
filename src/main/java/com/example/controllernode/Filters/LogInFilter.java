package com.example.controllernode.Filters;

import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Model.ResponseModel;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LogInFilter implements javax.servlet.Filter {
    @Bean
    public FilterRegistrationBean<LogInFilter> logIn1Filter(){
        FilterRegistrationBean<LogInFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new LogInFilter());
        registrationBean.addUrlPatterns("/api/Reader/*","/api/Writer/*","/api/User/*","/api/Schema/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        var auth=req.getHeader("Authorization");

        var CurrentUser=JWT.validate(auth);

        if(CurrentUser.getUser() == null){
            ((HttpServletResponse) servletResponse).setStatus(401);
            servletResponse.getOutputStream().write(new ResponseModel.Builder<String>(false).message("unauthorized").build().toString().getBytes(StandardCharsets.UTF_8));
            return;
        }

        servletRequest.setAttribute("CurrentUser",CurrentUser);

        filterChain.doFilter(servletRequest,servletResponse);
    }
}
