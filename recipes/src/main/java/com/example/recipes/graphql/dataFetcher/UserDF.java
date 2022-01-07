package com.example.recipes.graphql.dataFetcher;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.naming.AuthenticationException;

import com.example.recipes.data.entity.Role;
import com.example.recipes.data.entity.User;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.data.repos.UserRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class UserDF {
    //repos
    @Autowired 
    UserRepo userR; 

    @Autowired 
    RolesRepo rolesR; 

    //my security unlogic
    @Autowired 
    SecurityDataFetcher securityDF;

    //PUBLIC: Authorization JWT
    public DataFetcher <String> signIn(){
        return dataFetchingEnvironment -> {
            String name = dataFetchingEnvironment.getArgument("name");
            String password = dataFetchingEnvironment.getArgument("password");

            if (name ==null || password == null)return null;

            //get user by name
            User user = userR.findFirstByName(name);
            if (user==null) throw new AuthenticationException("No Autorized!");

            //check pass
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(!encoder.matches(password, user.getPassword())){
                throw new AuthenticationException("No Autorized!");
            }

            //create JWT for header Authorization: Bearer xxx.yyyy.zzzz
            return securityDF.createAndSaveJWT(user);
        };
    }
    
    //PUBLIC: Create user with USER role (author)
    public DataFetcher <User> singUp(){
        return dataFetchingEnvironment -> {

            String name = dataFetchingEnvironment.getArgument("name");
            String email = dataFetchingEnvironment.getArgument("email");
            String password = dataFetchingEnvironment.getArgument("password");

            //save new user in the repository
            User user = new User(name,email,password);
            if (userR.existsByName(name)){
                throw new Exception("Username must be unique");
            }
            if (userR.existsByEmail(email)){
                throw new Exception("Email must be unique");
            }

            //new user
            user = userR.save(user);

            //asing rol USER
            ArrayList<String> rol = new ArrayList<>(Arrays.asList("USER"));
            Set<Role> roles = RoleDF.valideAndGetRolesByNames(rol,rolesR);
            user.setRoles(roles);
            userR.save(user);
            
            return user;
        };
    }

    //QUERIES
    public DataFetcher<List<User>> getUserDF() {
        return dataFetchingEnvironment -> {

            String id = dataFetchingEnvironment.getArgument("id");
            String name = dataFetchingEnvironment.getArgument("name");
            String rolname = dataFetchingEnvironment.getArgument("rolename");
            
            List<User> users = new ArrayList<>();

            // byID (one user)
            if (id!=null){
                Long uid;
                try{
                    uid = Long.parseLong(id);
                }catch(NumberFormatException e){
                    return users;
                }
                User user = userR.findById(uid).orElse(null);
                if (user!=null){
                    securityDF.onlyADMINorOWNER(user); //security
                    users.add(user);
                }
                return users;
            }

            // all 
            securityDF.onlyADMIN(); //security

            if (id==null && name==null && rolname == null){
                for (User user: userR.findAll()){
                    users.add(user);
                }
                return users;
            }
            // By Name
            if(name!= null){
                users = userR.findByNameContaining(name);
            }

            // With Role
            if (rolname!=null){
                Role role = rolesR.findFirstByName(rolname);

                // Solo roles?
                if (name==null){
                    if (role==null) return new ArrayList<>();
                    users = role.getUsers();

                }else{
                    // Nombre y Rol
                    if (role==null) return new ArrayList<>();
                    List<User> roleUsers = new ArrayList<>();
                    for (User user:users){
                        for (Role rolUser:user.getRoles()){
                            if (role.getId() == rolUser.getId()){
                                roleUsers.add(user);
                                break;
                            }
                        }
                    }
                    users = roleUsers;
                }
            }
            return users;
        };
    }

    //MUTATIONS
    public DataFetcher <User> createUserDF(){
        return dataFetchingEnvironment -> {

            //security
            securityDF.onlyADMIN();

            //get
            String name = dataFetchingEnvironment.getArgument("name");
            String email = dataFetchingEnvironment.getArgument("email");
            String password = dataFetchingEnvironment.getArgument("password");
            ArrayList<String> rolenames = dataFetchingEnvironment.getArgument("roles");

            //validate role names before saving
            Set<Role> roles = RoleDF.valideAndGetRolesByNames(rolenames,rolesR);

            //save new user in the repository
            User user = new User(name,email,password);
            if (userR.existsByName(name) || userR.existsByEmail(email)){
                throw new Exception("Username and email must be unique");
            }

            //new user
            user = userR.save(user);

            //asing roles
            if (roles!=null && roles.size()>0){
                user.setRoles(roles);
                userR.save(user);
            }
            
            return user;
        };
    }

    public DataFetcher <User> updateUserDF(){
        return dataFetchingEnvironment -> {

            //check if user exist
            String strUserId = dataFetchingEnvironment.getArgument("id");
            User user = UserDF.getUserById(strUserId,userR);
            if (user == null) return null;

            //security
            securityDF.onlyADMINorOWNER(user);

            //get params
            String name = dataFetchingEnvironment.getArgument("name");
            String email = dataFetchingEnvironment.getArgument("email");
            String password = dataFetchingEnvironment.getArgument("password");
            ArrayList<String> rolenames = dataFetchingEnvironment.getArgument("roles");

            //name and email uniques?
            if (name!=null){
                if (!name.equals(user.getName())){
                    if (userR.existsByName(name)) throw new Exception("Username must be unique");
                    user.setName(name);
                }
            }
            if (email!=null){
                if (!email.equals(user.getEmail())){
                    if (userR.existsByEmail(email)) throw new Exception("Email must be unique");
                    user.setEmail(email);
                }
            }
            if (password!=null){
                user.setPassword(password);
            }

            //valide roles before update
            Set<Role> roles = RoleDF.valideAndGetRolesByNames(rolenames,rolesR);
            if (roles!= null && roles.size()>0){
                user.setRoles(roles);
            }

            //update user
            user = userR.save(user);
            return user;
        };
    }

    public DataFetcher <User> deleteUserDF(){
        return dataFetchingEnvironment -> {

            User user = UserDF.getUserById(dataFetchingEnvironment.getArgument("id"),userR);

            //security
            securityDF.onlyADMINorOWNER(user);

            //delete
            if (user!=null){
                userR.delete(user);
            } 
            return user;
        };
    }

    //AUX
    public static User getUserById(String strId, UserRepo userR){
        if (strId == null)return null;
        long id;

        try{
            id = Long.parseLong(strId);
        }catch(NumberFormatException e){
            return null;
        }

        User user = userR.findById(id).orElse(null);
        return user;
    }
}
