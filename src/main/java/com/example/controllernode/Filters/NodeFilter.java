package com.example.controllernode.Filters;

import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Model.ResponseModel;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class NodeFilter implements Filter {

    @Bean
    public FilterRegistrationBean<NodeFilter> NodeFilter(){
        FilterRegistrationBean<NodeFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new NodeFilter());
        registrationBean.addUrlPatterns("/Node/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        var auth=req.getHeader("Authorization");

        if(!JWT.validateNodeJWT(auth))
        {
            ((HttpServletResponse) servletResponse).setStatus(401);
            servletResponse.getOutputStream().write(new ResponseModel.Builder<String>(false).message("unauthorized").build().toString().getBytes(StandardCharsets.UTF_8));
            return;
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }
}
