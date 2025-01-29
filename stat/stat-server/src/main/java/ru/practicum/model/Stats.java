package ru.practicum.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    String app;
    String uri;
    String ip;
    @Column(name = "time_stamp")
    LocalDateTime timestamp;

//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null) return false;
//        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
//        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
//        if (thisEffectiveClass != oEffectiveClass) return false;
//        Stats stat = (Stats) o;
//        return getId() != null && Objects.equals(getId(), stat.getId());
//    }
//
//    @Override
//    public final int hashCode() {
//        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
//    }
}