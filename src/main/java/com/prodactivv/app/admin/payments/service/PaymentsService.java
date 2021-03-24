package com.prodactivv.app.admin.payments.service;

import com.google.gson.JsonObject;
import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.admin.payments.model.PaymentRequest;
import com.prodactivv.app.admin.payments.model.PaymentRequestRepository;
import com.prodactivv.app.admin.payments.model.PaymentVerification;
import com.prodactivv.app.config.P24Defaults;
import com.prodactivv.app.core.events.EventService;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.security.TokenValidityService;
import com.prodactivv.app.user.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentsService {

    private final P24Defaults p24Defaults;
    private final PaymentRequestRepository repository;
    private final MailNotificationService mailService;
    private final TokenValidityService tokenValidityService;
    private final UserSubscriptionService subscriptionService;
    private final EventService eventService;

    public PaymentRequest.Dto.Confirmation orderPayment(String paymentRequestToken, Integer p24Method) throws NotFoundException, IOException {
        PaymentRequest paymentRequest = repository.findByToken(paymentRequestToken)
                .orElseThrow(new NotFoundException(String.format("Payment request %s not found!", paymentRequestToken)));

        paymentRequest.setP24Method(p24Method);

        PaymentRequest.Dto.P24Object body = PaymentRequest.Dto.P24Object.fromPaymentRequest(paymentRequest, p24Defaults);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(p24Defaults.getBaseUrl())
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        P24Api p24api = retrofit.create(P24Api.class);

        Call<JsonObject> register = p24api.register(p24Defaults.getRegisterTransactionUrl(), p24Defaults.getAuthorization(), body);
        Response<JsonObject> execute = register.execute();

        if (execute.body() != null) {
            paymentRequest.setP24Token(execute.body().getAsJsonObject("data").getAsJsonPrimitive("token").getAsString());
            PaymentRequest.Dto.Confirmation confirmation = PaymentRequest.Dto.Confirmation.fromPaymentRequest(repository.save(paymentRequest));
            confirmation.setTrnRequestUrl(p24Defaults.getTrnRequestUrl() + paymentRequest.getP24Token());
            return confirmation;
        }

        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Failed to connect to P24 api!");
    }

    public String getPaymentMethods() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(p24Defaults.getBaseUrl())
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        P24Api p24api = retrofit.create(P24Api.class);
        Call<JsonObject> paymentMethods = p24api.getP24Methods(p24Defaults.getPaymentMethodsUrl(), p24Defaults.getAuthorization());
        Response<JsonObject> execute = paymentMethods.execute();

        if (execute.body() != null) {
            return execute.body().toString();
        }

        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "Failed to connect to P24 api!");
    }

    public void receiveNotificationAndVerifyPayment(PaymentVerification verification) throws IOException, NoSuchAlgorithmException, NotFoundException, MessagingException {
        PaymentRequest paymentRequest = repository.findByToken(verification.getSessionId())
                .orElseThrow(new NotFoundException(String.format("Payment request %s not found!", verification.getSessionId())));

        paymentRequest.setP24OrderId(verification.getOrderId());
        paymentRequest.setP24ControlSign(PaymentRequest.Dto.OrderControl.fromPaymentRequest(paymentRequest).calculateSign());
        repository.save(paymentRequest);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(p24Defaults.getBaseUrl())
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        P24Api p24api = retrofit.create(P24Api.class);
        Call<JsonObject> statusCall = p24api.verify(
                p24Defaults.getPaymentVerificationUrl(),
                p24Defaults.getAuthorization(),
                PaymentRequest.Dto.Verification.fromPaymentRequest(paymentRequest)
        );

        Response<JsonObject> statusResponse = statusCall.execute();
        if (statusResponse.body() != null) {
            String status = statusResponse.body().getAsJsonObject("data").getAsJsonPrimitive("status").getAsString();
            paymentRequest.setP24Status(status);
            paymentRequest.setIsVerified(true);
            paymentRequest.setIsFinalized(status.equalsIgnoreCase("success"));
            repository.save(paymentRequest);
            subscriptionService.subscribe(paymentRequest.getUser(), paymentRequest.getPlan(), LocalDate.now().plusMonths(paymentRequest.getPlan().getIntermittency()));

            String shortToken = tokenValidityService.createTokenValidityForUser(paymentRequest.getUser()).getShortToken();
            HashMap<String, String> variables = new HashMap<>();
            variables.put("{redirect.url}", p24Defaults.getQuestionnaireUrl() + shortToken);
            mailService.sendPurchaseConfirmationEmail(paymentRequest.getUser().getEmail(), variables);
            eventService.createUserBasedEvent(EventService.EventType.FILL_QUESTIONNAIRE, paymentRequest.getUser(), shortToken);
        } else {
            paymentRequest.setIsVerified(false);
            paymentRequest.setIsFinalized(false);
            log.info(statusResponse.message());
            repository.save(paymentRequest);
            mailService.sendNotification(paymentRequest.getUser().getEmail(), "Błąd płatności", "Wystąpił błąd podczas przetwarzania płatności!");
        }

    }

    public interface P24Api {
        @POST
        Call<JsonObject> register(@Url String url, @Header(value = "Authorization") String auth, @Body PaymentRequest.Dto.P24Object p24Object);

        @PUT
        Call<JsonObject> verify(@Url String url, @Header(value = "Authorization") String auth, @Body PaymentRequest.Dto.Verification verification);

        @GET
        Call<JsonObject> getP24Methods(@Url String url, @Header(value = "Authorization") String auth);
    }
}
