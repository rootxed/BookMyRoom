package be.atc.filters;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/register.xhtml"})
public class RegisterRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Subject currentUser = null;
        currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            // redirect to home page
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect(request.getServletContext().getContextPath() + "/app/index.xhtml");
        } else {
            // Not logged in
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Init filter
    }

    @Override
    public void destroy() {
        // Clean filter
    }
}