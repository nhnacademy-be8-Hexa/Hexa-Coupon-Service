package entity;

import entity.eum.Target;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long coupon_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    @NotNull
    private Coupon_Policy couponPolicy;

    @NotNull
    private String coupon_name;

    @NotNull
    private Target target;

    private int coupon_target_id;

    private ZonedDateTime coupon_deadline;

    private ZonedDateTime coupon_created_at;

    private boolean coupon_is_active;

    private ZonedDateTime coupon_used_at;

}
