package com.huidou.edgeinsight.core.domain.auth;

import java.util.List;

public interface UserService {

    Object createUser(Object user);

    Object updateUser(Long id, Object user);

    void deleteUser(Long id);

    Object findById(Long id);

    List<Object> findAll();
}
