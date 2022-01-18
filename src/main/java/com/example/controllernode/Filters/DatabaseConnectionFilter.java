package com.example.controllernode.Filters;

import com.example.controllernode.Helper.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DatabaseConnectionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        var database=CurrentUser.getDatabase();

        if(database == null || database.equals("")){
            ((HttpServletResponse) response).setStatus(401);
            response.getOutputStream().write(new ResponseModel.Builder<String>(false).message("You Should connect to database First").build().toString().getBytes(StandardCharsets.UTF_8));
            return;
        }

        chain.doFilter(request, response);
    }

    @Bean
    public FilterRegistrationBean<DatabaseConnectionFilter> DatabaseConnectionFilter(){
        FilterRegistrationBean<DatabaseConnectionFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new DatabaseConnectionFilter());
        registrationBean.addUrlPatterns("/api/Reader/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
