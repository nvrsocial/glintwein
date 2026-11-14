package com.nvrsocial.glintwein.notification.repository;

import com.nvrsocial.glintwein.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    List<Notification> findByRecipientIdAndReadFalse(Long recipientId, Pageable pageable);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    long countByRecipientIdAndReadFalse(Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipientId = :recipientId AND n.read = false")
    int markAllAsRead(@Param("recipientId") Long recipientId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.workspaceId = :workspaceId AND n.recipientId = :recipientId")
    int deleteByWorkspaceIdAndRecipientId(@Param("workspaceId") Long workspaceId, @Param("recipientId") Long recipientId);
}
