package com.example.recipes.security;

import java.util.Set;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import com.example.recipes.data.entity.Role;
import com.example.recipes.data.entity.User;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.data.repos.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

// MY CRACY SECURITY LOGIC
@Component
public class SecurityDataFetcher {
    
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserRepo userR;

    @Autowired
    RolesRepo roleR;

    //cache
    private Role rAdmin, rSuper, rUser;

    //call manually on start, before poblate database
    public void init() {
        this.rAdmin = roleR.findFirstByName("ADMIN");
        this.rSuper = roleR.findFirstByName("SUPER");
        this.rUser = roleR.findFirstByName("USER");
    }
    
    //SECURITY CHECKS
    public void noAnomimous() throws AuthenticationException{
        //valid token?
        User user = getValidUserFromRequest();
        //check anonymous
        if (user== null)throw new AuthenticationException("Anonymous no Autorized!");
    }

    public void onlyADMIN() throws AuthenticationException{
        //valid token?
        User user = getValidUserFromRequest();
        //check roles
        if (!user.getRoles().contains(rAdmin)) 
            throw new AuthenticationException("No Autorized!");
    }

    public void onlySUPER() throws AuthenticationException{
        //valid token?
        User user = getValidUserFromRequest();
        //check roles
        if (!user.getRoles().contains(rSuper)) 
            throw new AuthenticationException("No Autorized!");
    }

    public void onlyOWER(User otherUser) throws AuthenticationException {
        //valid token?
        User me = getValidUserFromRequest();
        //ckeck if owner 
        if (!me.equals(otherUser)) throw new AuthenticationException("No Autorized!");
    }

    public void onlySUPERorUSER()throws AuthenticationException {
        //valid token?
        User user = getValidUserFromRequest();
        //check
        Set<Role> roles = user.getRoles();
        if (!roles.contains(rSuper)) throw new AuthenticationException("No Autorized!");
        if (!roles.contains(rUser)) throw new AuthenticationException("No Autorized!");
   }

    public void onlySUPERorOWNER(User otherUser) throws AuthenticationException {
        //valid token?
        User me = getValidUserFromRequest();
        //check
        Set<Role> roles = me.getRoles();
        if (!roles.contains(rSuper)){
            if (!me.equals(otherUser))  throw new AuthenticationException("No Autorized!"); 
        }
    }

    public void onlyADMINorOWNER(User otherUser) throws AuthenticationException {
        //valid token?
        User me = getValidUserFromRequest();
        //check
        Set<Role> roles = me.getRoles();
        if (!roles.contains(rAdmin)){
            if (!me.equals(otherUser))  throw new AuthenticationException("No Autorized!"); 
        }
    }
    
    //create JWT 
    public String createAndSaveJWT(User user){
        user.jwt = jwtUtils.getJWT(user.getName());
        userR.save(user);
        return user.jwt;
    }

    //get JWT from the  Authorization header removing Bearer prefix and valide token
	private User getValidUserFromRequest()  throws AuthenticationException{
        //get header and substring
        String headerAuth = request.getHeader("Authorization");
        String jwt = null;
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			jwt = headerAuth.substring(7, headerAuth.length());
		}
        //no expired and valid format
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)){
            throw new AuthenticationException("No Autorized!");
        }

        //compare with database jwt, only valid if equals
        //String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String name=  jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userR.findFirstByName(name);
        if (user==null || user.jwt==null || !user.jwt.equals(jwt)) 
            throw new AuthenticationException("No Autorized!");

		return user;
	}
    
}
