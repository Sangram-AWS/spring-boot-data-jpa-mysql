package com.bezkoder.spring.datajpa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SmsNotificationListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOvertimeSettled(OvertimeSettledEvent event) {
        try {
            // SMS fires ONLY after transaction commits successfully
            // If transaction rolled back, this method never runs
            System.out.println("SMS SENT to worker " + event.getWorkerId() +
                    ": Your overtime for " + event.getMonth() +
                    " of ₹" + event.getTotalAmount() + " has been settled.");

            // Replace above with actual SMS service call:
            // smsService.send(workerId, message);

        } catch (Exception e) {
            // SMS failure must NOT affect settlement data
            // Log and queue for retry
            System.err.println("SMS failed for worker " + event.getWorkerId() +
                    ", month " + event.getMonth() +
                    ". Queue for retry. Error: " + e.getMessage());
        }
    }
}