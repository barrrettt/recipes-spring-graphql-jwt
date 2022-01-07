package com.example.recipes.graphql.dataFetcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.recipes.data.entity.Role;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class RoleDF {

    @Autowired
    RolesRepo rolesR;

	@Autowired
	SecurityDataFetcher auxSecurityDF; //my security unlogic
    
    public DataFetcher<List<Role>> getRolDF() {
        return dataFetchingEnvironment -> {
			
			auxSecurityDF.onlyADMIN();//security

            List<Role> roles = new ArrayList<>();
            for(Role r:  rolesR.findAll()){
                roles.add(r);
            }
            return roles;
        };
    }

    //aux
	public static Set<Role> valideAndGetRolesByNames(ArrayList<String> names, RolesRepo rolesR) throws Exception{
		Set<Role> roles = null;
		if (names != null){
			roles = new HashSet<Role>();
			for (String rolename : names){
				Role role = rolesR.findFirstByName(rolename);
				if (role==null){
					throw new Exception("Rolename no exist: "+ rolename);
				}else{
					roles.add(role);
				}
			}
		}
		return roles;
	}

}
