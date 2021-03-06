package com.reddivestor.reddivestorapi.web.security;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import io.jsonwebtoken.Claims;

@Builder
@ToString
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    private final String JWT_SIGNING_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            if (checkJWTToken(request, response)) {
                Claims claims = validateToken(request);
                if (claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims);
                } else {
                    System.out.println("authorities was null, rejecting request.");
                    SecurityContextHolder.clearContext();
                }
            }
            else {
                System.out.println("Token was not a JWT token.");
                SecurityContextHolder.clearContext();
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse response) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
            return false;
        return true;
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
        try {
            Claims result = Jwts
                    .parser()
                    .setSigningKey(JWT_SIGNING_KEY.getBytes())
                    .parseClaimsJws(jwtToken).getBody();
            return result;
        } catch (Exception e){
            System.out.println("Exception in validateToken !! Exception="+e.toString());
            throw e;
        }

    }

    private void setUpSpringAuthentication(Claims claims) {
        List<String> authorities = (List) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
