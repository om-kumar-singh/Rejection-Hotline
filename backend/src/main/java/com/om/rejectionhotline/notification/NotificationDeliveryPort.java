package com.om.rejectionhotline.notification;

import com.om.rejectionhotline.entity.Notification;

/**
 * Future delivery adapters (email, WebSocket) implement this port.
 */
public interface NotificationDeliveryPort {
    void deliver(Notification notification);
}
