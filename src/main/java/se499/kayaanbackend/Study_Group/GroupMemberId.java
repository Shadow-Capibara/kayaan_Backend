package se499.kayaanbackend.Study_Group;

import java.io.Serializable;

import lombok.Data;

@Data
public class GroupMemberId implements Serializable {
    private Integer groupId;
    private Integer userId;
}
