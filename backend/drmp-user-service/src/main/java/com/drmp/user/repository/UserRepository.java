package com.drmp.user.repository;

import com.drmp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsernameAndDeletedFalse(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmailAndDeletedFalse(String email);
    
    /**
     * 根据机构ID查找用户列表
     */
    List<User> findByOrgIdAndDeletedFalse(Long orgId);
    
    /**
     * 分页查询机构下的用户
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false " +
           "AND u.orgId = :orgId " +
           "AND (:status IS NULL OR u.status = :status) " +
           "AND (:keyword IS NULL OR u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.email LIKE %:keyword%)")
    Page<User> findByOrgIdAndConditions(
            @Param("orgId") Long orgId,
            @Param("status") User.UserStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    /**
     * 分页查询所有用户（平台管理员用）
     */
    @Query("SELECT u FROM User u LEFT JOIN u.organization o WHERE u.deleted = false " +
           "AND (:orgId IS NULL OR u.orgId = :orgId) " +
           "AND (:status IS NULL OR u.status = :status) " +
           "AND (:keyword IS NULL OR u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.email LIKE %:keyword% OR o.name LIKE %:keyword%)")
    Page<User> findByConditions(
            @Param("orgId") Long orgId,
            @Param("status") User.UserStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    
    /**
     * 检查用户名是否已存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.deleted = false " +
           "AND u.username = :username AND (:id IS NULL OR u.id != :id)")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") Long id);
    
    /**
     * 检查邮箱是否已存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.deleted = false " +
           "AND u.email = :email AND (:id IS NULL OR u.id != :id)")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    /**
     * 更新用户最后登录信息
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp, " +
           "u.loginCount = u.loginCount + 1 WHERE u.id = :userId")
    void updateLastLoginInfo(@Param("userId") Long userId, 
                           @Param("loginTime") LocalDateTime loginTime, 
                           @Param("loginIp") String loginIp);
    
    /**
     * 更新用户密码
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password, u.passwordUpdateTime = :updateTime " +
           "WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, 
                       @Param("password") String password, 
                       @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 更新用户状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    void updateStatus(@Param("userId") Long userId, @Param("status") User.UserStatus status);
    
    /**
     * 查询机构下的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false AND u.orgId = :orgId")
    long countByOrgId(@Param("orgId") Long orgId);
    
    /**
     * 查询活跃用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false " +
           "AND u.status = com.drmp.user.entity.User$UserStatus.ACTIVE")
    long countActiveUsers();
}