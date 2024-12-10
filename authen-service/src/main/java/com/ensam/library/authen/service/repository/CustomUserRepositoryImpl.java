package com.ensam.library.authen.service.repository;

import com.ensam.library.authen.service.model.Role;
import com.ensam.library.authen.service.model.User;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;


import lombok.AllArgsConstructor;


@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final EntityManager entityManager;

    @Override
    public User findByEmail(String email){
        HashMap<String, Integer> Role_map = new HashMap<String, Integer>();
        Role_map.put("ROLE_ADMIN", 1);
        Role_map.put("ROLE_USER", 2);

        String query = "SELECT u.id, u.name, u.email, u.password, r.name AS role_name " +
                "FROM user u " +
                "JOIN user_role ur ON u.id = ur.user_id " +
                "JOIN role r ON ur.role_id = r.id " +
                "WHERE u.email = ?1";
        List<Object[]> result = entityManager.createNativeQuery(query)
                .setParameter(1, email).getResultList();
        if (result.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Object[] row = result.get(0);
        User user = new User();
        user.setId((Long) row[0]);
        user.setName((String) row[1]);
        user.setEmail((String) row[2]);
        user.setPassword((String) row[3]);
        Role role = new Role();
        role.setId(Role_map.get((String) row[4]));
        role.setName((String) row[4]);
        user.getRoles().add(role);
        return user;
    }


    @Override
    public User findByIdAlt(Long id){
        System.out.println(id);
        HashMap<String, Integer> Role_map = new HashMap<String, Integer>();
        Role_map.put("ROLE_ADMIN", 1);
        Role_map.put("ROLE_USER", 2);

        String query = "SELECT u.id, u.name, u.email, u.password, r.name AS role_name " +
                "FROM user u " +
                "JOIN user_role ur ON u.id = ur.user_id " +
                "JOIN role r ON ur.role_id = r.id " +
                "WHERE u.id = ?1";
        List<Object[]> result = entityManager.createNativeQuery(query)
                .setParameter(1, id).getResultList();
        if (result.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Object[] row = result.get(0);
        User user = new User();
        user.setId((Long) row[0]);
        user.setName((String) row[1]);
        user.setEmail((String) row[2]);
        user.setPassword((String) row[3]);
        Role role = new Role();
        role.setId(Role_map.get((String) row[4]));
        role.setName((String) row[4]);
        user.getRoles().add(role);
        return user;
    }



}
