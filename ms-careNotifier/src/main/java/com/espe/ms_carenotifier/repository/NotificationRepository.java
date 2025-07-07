package com.espe.ms_carenotifier.repository;

import com.espe.ms_carenotifier.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Puedes agregar métodos personalizados si es necesario
    // Por ejemplo, encontrar todas las notificaciones por tipo de evento o destinatario
}
