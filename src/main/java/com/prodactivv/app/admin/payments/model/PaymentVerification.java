package com.prodactivv.app.admin.payments.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerification {

    private Integer merchantId;
    private Integer posId;
    private String sessionId;
    private Integer amount;
    private Integer originAmount;
    private String currency;
    private Integer orderId;
    private Integer methodId;
    private String statement;
    private String sign;

}
