package com.prodactivv.app.admin.payments.service;

import com.google.gson.JsonObject;
import com.prodactivv.app.admin.payments.model.PaymentRequest;
import com.prodactivv.app.admin.payments.model.PaymentRequestRepository;
import com.prodactivv.app.config.P24Defaults;
import com.prodactivv.app.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentRequestRepository repository;
    private final P24Defaults p24Defaults;

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

    public interface P24Api {
        @POST
        Call<JsonObject> register(@Url String url, @Header(value = "Authorization") String auth, @Body PaymentRequest.Dto.P24Object p24Object);

        @GET
        Call<JsonObject> getP24Methods(@Url String url, @Header(value = "Authorization") String auth);
    }
}
