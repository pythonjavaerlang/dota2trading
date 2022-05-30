package in.dota2.security;

import java.io.IOException;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

public class SteamAuthenticationFailureHandler implements AuthenticationFailureHandler {
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                        throws IOException, ServletException {

                response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/users/signin"));
        }
}
