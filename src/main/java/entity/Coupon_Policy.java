package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

@Entity
public class Coupon_Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long coupon_policy_id;

    @NotNull
    private String coupon_policy_name;

    @NotNull
    private int min_purchase_amount;

    @NotNull
    private String discount_type;

    @NotNull
    private int discount_value;

    private int max_discount_amount;

    @NotNull
    private boolean is_deleted;

    private String event_type;

    @NotNull
    private ZonedDateTime created_at;

}
