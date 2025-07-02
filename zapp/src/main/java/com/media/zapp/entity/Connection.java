package com.media.zapp.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "connections")
@IdClass(ConnectionId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {

    @Id
    @Column(name = "user_id1")
    private Integer userId1;

    @Id
    @Column(name = "user_id2")
    private Integer userId2;
}