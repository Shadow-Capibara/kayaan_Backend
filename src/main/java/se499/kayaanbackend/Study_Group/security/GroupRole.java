package se499.kayaanbackend.Study_Group.security;

/**
 * Enum สำหรับบทบาทต่างๆ ที่ผู้ใช้สามารถมีในกลุ่มเรียน
 */
public enum GroupRole {
    OWNER(GroupPermission.values()),      // เจ้าของกลุ่ม - ทำได้ทุกอย่าง
    ADMIN(new GroupPermission[]{          // ผู้ดูแล - จัดการสมาชิกและเนื้อหา
        GroupPermission.VIEW_GROUP,
        GroupPermission.POST_CONTENT,
        GroupPermission.EDIT_OWN_CONTENT,
        GroupPermission.DELETE_OWN_CONTENT,
        GroupPermission.EDIT_ANY_CONTENT,
        GroupPermission.DELETE_ANY_CONTENT,
        GroupPermission.MANAGE_MEMBERS,
        GroupPermission.INVITE_MEMBERS,
        GroupPermission.VIEW_ANALYTICS,
        GroupPermission.MODERATE_CONTENT,
        GroupPermission.MANAGE_ROLES
    }),
    MODERATOR(new GroupPermission[]{      // ผู้ควบคุม - ควบคุมเนื้อหา
        GroupPermission.VIEW_GROUP,
        GroupPermission.POST_CONTENT,
        GroupPermission.EDIT_OWN_CONTENT,
        GroupPermission.DELETE_OWN_CONTENT,
        GroupPermission.EDIT_ANY_CONTENT,
        GroupPermission.DELETE_ANY_CONTENT,
        GroupPermission.VIEW_ANALYTICS,
        GroupPermission.MODERATE_CONTENT
    }),
    MEMBER(new GroupPermission[]{          // สมาชิกทั่วไป - โพสต์และดูเนื้อหา
        GroupPermission.VIEW_GROUP,
        GroupPermission.POST_CONTENT,
        GroupPermission.EDIT_OWN_CONTENT,
        GroupPermission.DELETE_OWN_CONTENT,
        GroupPermission.VIEW_ANALYTICS
    });

    private final GroupPermission[] permissions;

    GroupRole(GroupPermission[] permissions) {
        this.permissions = permissions;
    }

    public GroupPermission[] getPermissions() {
        return permissions;
    }

    public boolean hasPermission(GroupPermission permission) {
        for (GroupPermission perm : permissions) {
            if (perm == permission) {
                return true;
            }
        }
        return false;
    }
}
