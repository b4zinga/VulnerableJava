package com.example.vulnerablejava.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.example.vulnerablejava.utils.CSRFUtil;

@Component
public class CSRFFilter implements Filter{

    String[] checkUrlList = {"/csrf/safe2"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String url = httpServletRequest.getRequestURI();
        if (!needTobeChecked(url)) {
            chain.doFilter(request, response);
            return;
        }
        String csrfFormToken = httpServletRequest.getParameter("_csrf");
        String csrfSessionToken = (String) httpServletRequest.getSession().getAttribute("csrftoken");
        if (csrfSessionToken == null) {
            csrfSessionToken = CSRFUtil.generateToken();
            httpServletRequest.getSession().setAttribute("csrftoken", csrfSessionToken);
            Cookie cookie = new Cookie("_csrf", csrfSessionToken);
            httpServletResponse.addCookie(cookie);
        }
        if (csrfFormToken != null && csrfFormToken.equals(csrfSessionToken)) {
            chain.doFilter(request, response);
        } else {
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().write("非法请求, from csrf filter");
        }
    }

    private boolean needTobeChecked(String uri) {
        for (String url : checkUrlList) {
            if (uri.startsWith(url, 0)) {
                return true;
            }
        }
        return false;
    }
}
