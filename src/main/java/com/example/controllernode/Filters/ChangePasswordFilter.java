package com.example.controllernode.Filters;

import com.example.controllernode.Helper.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.Helper.FileManger;
import org.json.JSONObject;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

@Component
public class ChangePasswordFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        try {
            var filePath=MessageFormat.format("NoSqlDB/Users/{0}.json", CurrentUser.getUser().getUsername());
            String Result = Files.readString(Path.of(filePath));
            var user=new JSONObject(Result);

            if(user.has("isDefault") && user.get("isDefault").equals(true)){
                ((HttpServletResponse) response).setStatus(400);
                response.getOutputStream().write(new ResponseModel.Builder<String>(false).message("You Should change your password").build().toString().getBytes(StandardCharsets.UTF_8));
                return;
            }
        }catch (Exception ex){
            ((HttpServletResponse) response).setStatus(400);
            response.getOutputStream().write(new ResponseModel.Builder<String>(false).message("Error").build().toString().getBytes(StandardCharsets.UTF_8));
            return;
        }

        chain.doFilter(request, response);
    }

    @Bean
    public FilterRegistrationBean<ChangePasswordFilter> ChangePasswordFilter(){
        FilterRegistrationBean<ChangePasswordFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ChangePasswordFilter());
        registrationBean.addUrlPatterns("/api/Reader/*","/api/Writer/*","/api/User/addUser","/api/Schema/*");
        registrationBean.setOrder(2);

        return registrationBean;
    }
}
