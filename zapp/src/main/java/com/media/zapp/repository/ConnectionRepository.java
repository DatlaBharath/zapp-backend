package com.media.zapp.repository;

import com.media.zapp.entity.Connection;
import com.media.zapp.entity.ConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, ConnectionId> {

    @Query("SELECT c FROM Connection c WHERE c.userId1 IN :ids OR c.userId2 IN :ids")
    List<Connection> findConnectionsByUserIds(@Param("ids") Set<Integer> ids);

    @Query("SELECT c FROM Connection c WHERE c.userId1 = :userId OR c.userId2 = :userId")
    List<Connection> findByUserId1OrUserId2(@Param("userId") Integer userId);
}

