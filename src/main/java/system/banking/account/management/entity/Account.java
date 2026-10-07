package system.banking.account.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String account_number;
    private String account_holder_name;
    private BigDecimal balance;

    @Enumerated(value = EnumType.STRING)
    private AccountStatus accountStatus;

    @Version
    private Long version;
}
