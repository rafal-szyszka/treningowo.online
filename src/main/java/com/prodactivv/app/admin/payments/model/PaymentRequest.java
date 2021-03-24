package com.prodactivv.app.admin.payments.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodactivv.app.config.P24Defaults;
import com.prodactivv.app.subscription.model.PromoCode;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.user.model.User;
import lombok.*;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    public static final boolean PERSIST_TOKEN = true;
    public static final boolean OVERRIDE_TOKEN = false;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Scope {
        REGISTRATION("reg"), PURCHASE("pur");

        @Getter
        private final String name;

        public static boolean isRegistration(String scope) {
            return scope.equalsIgnoreCase(REGISTRATION.name);
        }
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private SubscriptionPlan plan;

    @ManyToOne
    @JoinColumn(name = "code_id", referencedColumnName = "id")
    private PromoCode promoCode;

    private LocalDate validUntil;

    private String token;

    private Integer p24MerchantId;

    private String p24Token;

    private Integer p24OrderId;

    private String p24ControlSign;

    private String p24Crc;

    private Integer p24Method;

    private String p24Status;

    private Integer finalPrice;

    private Boolean isVerified;

    private Boolean isFinalized;

    private LocalDateTime datePlaced;

    private String scope;

    @PrePersist
    public void setTokenAndDate() throws NoSuchAlgorithmException, JsonProcessingException {
        if (validUntil == null && token == null && p24ControlSign == null && datePlaced == null) {
            recalculate(OVERRIDE_TOKEN);
        }
    }

    public void recalculate(boolean persistToken) throws NoSuchAlgorithmException, JsonProcessingException {
        datePlaced = LocalDateTime.now();
        validUntil = LocalDate.now().plusDays(7);
        MessageDigest digest = MessageDigest.getInstance("SHA-384");
        byte[] hash = digest.digest(
                (datePlaced + user.getEmail() + (plan != null ? plan.getId() : "") + validUntil + (promoCode != null ? promoCode.getId() : "") + finalPrice).getBytes(StandardCharsets.UTF_8)
        );
        if (!persistToken) {
            token = new String(Hex.encode(hash));
        }

        if (plan != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            p24ControlSign = new String(
                    Hex.encode(
                            digest.digest(
                                    objectMapper.writeValueAsString(
                                            Dto.PaymentControl.builder()
                                                    .sessionId(token)
                                                    .merchantId(p24MerchantId)
                                                    .amount(finalPrice)
                                                    .currency(getPlan().getCurrency())
                                                    .crc(p24Crc)
                                                    .build()
                                    ).getBytes(StandardCharsets.UTF_8)
                            )
                    )
            );
        }
    }

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Confirmation {
            private Long id;
            private String p24Token;
            private Integer p24OrderId;
            private Boolean isVerified;
            private Boolean isFinalized;
            private String trnRequestUrl;

            public static Confirmation fromPaymentRequest(PaymentRequest paymentRequest) {
                return builder()
                        .id(paymentRequest.id)
                        .p24Token(paymentRequest.p24Token)
                        .p24OrderId(paymentRequest.p24OrderId)
                        .isVerified(paymentRequest.isVerified)
                        .isFinalized(paymentRequest.isFinalized)
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PaymentControl {
            private String sessionId;
            private Integer merchantId;
            private Integer amount;
            private String currency;
            private String crc;
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class OrderControl {
            private String sessionId;
            private Integer orderId;
            private Integer amount;
            private String currency;
            private String crc;

            public static OrderControl fromPaymentRequest(PaymentRequest paymentRequest) {
                return builder()
                        .sessionId(paymentRequest.token)
                        .orderId(paymentRequest.p24OrderId)
                        .amount(paymentRequest.finalPrice)
                        .currency(paymentRequest.getPlan().getCurrency())
                        .crc(paymentRequest.p24Crc)
                        .build();
            }

            public String calculateSign() throws NoSuchAlgorithmException, JsonProcessingException {
                MessageDigest digest = MessageDigest.getInstance("SHA-384");
                ObjectMapper objectMapper = new ObjectMapper();
                return new String(
                        Hex.encode(
                                digest.digest(
                                        objectMapper.writeValueAsString(
                                                builder()
                                                        .sessionId(sessionId)
                                                        .orderId(orderId)
                                                        .amount(amount)
                                                        .currency(currency)
                                                        .crc(crc)
                                                        .build()
                                        ).getBytes(StandardCharsets.UTF_8)
                                )
                        )
                );
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class P24Object {
            private int merchantId;
            private int posId;
            private String sessionId;
            private int amount;
            private String currency;
            private String description;
            private String email;
            private String client;
            private String country;
            private String language;
            private int method;
            private String urlStatus;
            private String urlReturn;
            private int timeLimit;
            private int channel;
            private boolean waitForResult;
            private boolean regulationAccept;
            private int shipping;
            private String sign;
            private String encoding;

            public static P24Object fromPaymentRequest(PaymentRequest paymentRequest, P24Defaults defaults) {
                return builder()
                        .merchantId(paymentRequest.getP24MerchantId())
                        .posId(paymentRequest.getP24MerchantId())
                        .sessionId(paymentRequest.getToken())
                        .amount(paymentRequest.getFinalPrice())
                        .currency(paymentRequest.getPlan().getCurrency().toUpperCase())
                        .description(paymentRequest.getPlan().getName())
                        .email(paymentRequest.getUser().getEmail())
                        .client(String.format("%s %s", paymentRequest.getUser().getName(), paymentRequest.getUser().getLastName()))
                        .country(defaults.getCountry())
                        .language(defaults.getLanguage())
                        .method(paymentRequest.getP24Method())
                        .urlStatus(defaults.getUrlStatus())
                        .urlReturn(defaults.getUrlReturn())
                        .timeLimit(defaults.getTimeLimit())
                        .channel(defaults.getChannel())
                        .waitForResult(defaults.isWaitForResult())
                        .regulationAccept(defaults.isRegulationAccept())
                        .shipping(defaults.getShipping())
                        .sign(paymentRequest.getP24ControlSign())
                        .encoding(defaults.getEncoding())
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Verification {
            private Integer merchantId;
            private Integer posId;
            private String sessionId;
            private Integer amount;
            private String currency;
            private Integer orderId;
            private String sign;

            public static Verification fromPaymentRequest(PaymentRequest paymentRequest) {
                return builder()
                        .merchantId(paymentRequest.p24MerchantId)
                        .posId(paymentRequest.p24MerchantId)
                        .sessionId(paymentRequest.token)
                        .amount(paymentRequest.finalPrice)
                        .currency(paymentRequest.getPlan().getCurrency())
                        .orderId(paymentRequest.p24OrderId)
                        .sign(paymentRequest.p24ControlSign)
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Information {
            private User.Dto.Simple user;
            private SubscriptionPlan.Dto.QuestionnairesLess plan;
            private PromoCode.Dto.Details promoCode;
            private Integer finalPrice;

            public static Information fromPaymentRequest(PaymentRequest paymentRequest) {
                SubscriptionPlan.Dto.QuestionnairesLess plan = null;
                PromoCode.Dto.Details promoCode = null;

                if (paymentRequest.getPlan() != null) {
                    plan = SubscriptionPlan.Dto.QuestionnairesLess.fromSubscriptionPlan(paymentRequest.getPlan());
                }

                if (paymentRequest.getPromoCode() != null) {
                    promoCode = PromoCode.Dto.Details.fromPromoCode(paymentRequest.getPromoCode());
                }

                return builder()
                        .user(User.Dto.Simple.fromUser(paymentRequest.user))
                        .plan(plan)
                        .promoCode(promoCode)
                        .finalPrice(paymentRequest.finalPrice)
                        .build();
            }
        }
    }
}
