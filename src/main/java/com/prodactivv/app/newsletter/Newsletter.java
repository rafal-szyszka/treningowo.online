package com.prodactivv.app.newsletter;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Newsletter {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate signUpDate;

    private String email;

    private String code;

    private boolean isTermsOfUseAccepted;

    private boolean isPrivacyPolicyAccepted;

    private boolean allowedMarketingMessages;

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Subscription {
            private String email;
            private boolean isTermsOfUseAccepted;
            private boolean isPrivacyPolicyAccepted;
            private boolean allowedMarketingMessages;

            public boolean areNecessaryRegulationsAccepted() {
                return isTermsOfUseAccepted && isPrivacyPolicyAccepted && allowedMarketingMessages;
            }
        }
    }
}