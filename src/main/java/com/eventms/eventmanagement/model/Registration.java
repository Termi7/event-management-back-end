//package com.eventms.eventmanagement.model;
//
//import jakarta.persistence.*;
//import java.time.Instant;
//
//@Entity
//@Table(
//        name = "registrations",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"})
//)
//public class Registration {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "event_id", nullable = false)
//    private Event event;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private RegistrationStatus status = RegistrationStatus.REGISTERED;
//
//    @Column(nullable = false, updatable = false)
//    private Instant createdAt = Instant.now();
//
//    public Registration() {}
//
//    public Registration(Event event, User user, RegistrationStatus status) {
//        this.event = event;
//        this.user = user;
//        this.status = status;
//    }
//
//    public Long getId() { return id; }
//
//    public Event getEvent() { return event; }
//    public void setEvent(Event event) { this.event = event; }
//
//    public User getUser() { return user; }
//    public void setUser(User user) { this.user = user; }
//
//    public RegistrationStatus getStatus() { return status; }
//    public void setStatus(RegistrationStatus status) { this.status = status; }
//
//    public Instant getCreatedAt() { return createdAt; }
//}


package com.eventms.eventmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"})
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
