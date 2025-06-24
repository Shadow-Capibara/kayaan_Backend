package se499.kayaanbackend.redesign.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class GroupMemberId implements Serializable {
    private Integer groupID;
    private Integer userID;
}
