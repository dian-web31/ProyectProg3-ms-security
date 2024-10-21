package com.jdh.ms_security.Repositories;

import com.jdh.ms_security.Models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SessionRepository extends MongoRepository<Session,String> {
    @Query("{'user.$id': ObjectId(?0)}")
    public List<Session> getSessionsByUser(String userId);

    @Query("{'user.$id': ObjectId(?0), 'token': ?1}")
    public Session getSessionByUserId(String userId, String token);
}


