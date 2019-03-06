package com.mycompany.myapp.web.rest;
import com.mycompany.myapp.domain.Payment;
import com.mycompany.myapp.repository.PaymentRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.domain.User;
import com.stripe.Stripe;
import java.util.HashMap;
import java.util.Map;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import io.github.jhipster.web.util.ResponseUtil;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Payment.
 */
@RestController
@RequestMapping("/api")
public class PaymentResource {

    private final Logger log = LoggerFactory.getLogger(PaymentResource.class);

    private static final String ENTITY_NAME = "payment";


  	/**
  	 * PUT /payments/currentuser : Updates an existing payment.
  	 *
  	 * @param payment the payment to create with the current connected user
  	 * @return the ResponseEntity with status 200 (OK) and with body the updated
  	 *         payment, or with status 400 (Bad Request) if the payment is not
  	 *         valid, or with status 500 (Internal Server Error) if the payment
  	 *         couldn't be updated
  	 * @throws URISyntaxException if the Location URI syntax is incorrect
  	 */
  	@PutMapping("/payments/currentuser")
  	public ResponseEntity<Payment> createPaymentCurrentUser(@Valid @RequestBody Payment payment)
  			throws URISyntaxException {
  		log.debug("REST request to update Payment : {}", payment);
  		ResponseEntity<Payment> p;
  		if (payment.getId() == null) {
  			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
  		}
  		System.out.println("TEST");
  		Optional<String> userstr = SecurityUtils.getCurrentUserLogin();
  		if (userstr.isPresent()) {
  			Optional<User> user = userRepository.findOneByLogin(userstr.get());
  			payment.setUser(user.get());
  		}

  		// Set your secret key: remember to change this to your live secret key in
  		// production
  		// See your keys here: https://dashboard.stripe.com/account/apikeys
  		Stripe.apiKey = "sk_test_xxxxxxxxxxxxxxxxxxxxxxxx";

  		// Token is created using Checkout or Elements!
  		// Get the payment token ID submitted by the form:
  		// String token = request.getParameter("stripeToken");

  		Map<String, Object> params = new HashMap<>();
  		params.put("amount", payment.getAmount());
  		params.put("currency", payment.getCurrency());
  		params.put("description", payment.getDescription());
  		params.put("source", payment.getToken());
  		params.put("capture", payment.isCapture());

  		try {
  			Charge charge = Charge.create(params);
  			// System.out.println(charge);
        Payment result = paymentRepository.save(payment);

  			p = ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, payment.getId().toString()))
  					.body(result);
        result.setReceipt(charge.toJson());
        this.updatePayment(result);
  		} catch (CardException e) {
  			// Since it's a decline, CardException will be caught
  			System.out.println("Status is: " + e.getCode());
  			System.out.println("Message is: " + e.getMessage());
  			throw new BadRequestAlertException("CardException", ENTITY_NAME, "");

  		} catch (RateLimitException e) {
  			// Too many requests made to the API too quickly
  			throw new BadRequestAlertException("RateLimitException", ENTITY_NAME,
  					"Too many requests made to the API too quickly");
  		} catch (InvalidRequestException e) {
  			// Invalid parameters were supplied to Stripe's API
  			throw new BadRequestAlertException("InvalidRequestException", ENTITY_NAME,
  					"Invalid parameters were supplied to Stripe's API");
  		} catch (AuthenticationException e) {
  			// Authentication with Stripe's API failed
  			// (maybe you changed API keys recently)
  			throw new BadRequestAlertException("AuthenticationException", ENTITY_NAME,
  					"Authentication with Stripe's API failed, maybe you changed API keys recently");
  		} catch (StripeException e) {
  			// Display a very generic error to the user, and maybe send
  			// yourself an email
  			throw new BadRequestAlertException("An error occured", ENTITY_NAME, "Error");
  		} catch (Exception e) {
  			// Something else happened, completely unrelated to Stripe
  			throw new BadRequestAlertException("An error occured", ENTITY_NAME, "Error");
  		}
  		return p;
  	}
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public PaymentResource(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * POST  /payments : Create a new payment.
     *
     * @param payment the payment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new payment, or with status 400 (Bad Request) if the payment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/payments")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) throws URISyntaxException {
        log.debug("REST request to save Payment : {}", payment);
        if (payment.getId() != null) {
            throw new BadRequestAlertException("A new payment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Payment result = paymentRepository.save(payment);
        return ResponseEntity.created(new URI("/api/payments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /payments : Updates an existing payment.
     *
     * @param payment the payment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated payment,
     * or with status 400 (Bad Request) if the payment is not valid,
     * or with status 500 (Internal Server Error) if the payment couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/payments")
    public ResponseEntity<Payment> updatePayment(@Valid @RequestBody Payment payment) throws URISyntaxException {
        log.debug("REST request to update Payment : {}", payment);
        if (payment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Payment result = paymentRepository.save(payment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, payment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /payments : get all the payments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of payments in body
     */
    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        log.debug("REST request to get all Payments");
        return paymentRepository.findAll();
    }

    /**
     * GET  /payments/:id : get the "id" payment.
     *
     * @param id the id of the payment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the payment, or with status 404 (Not Found)
     */
    @GetMapping("/payments/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        log.debug("REST request to get Payment : {}", id);
        Optional<Payment> payment = paymentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(payment);
    }

    /**
     * DELETE  /payments/:id : delete the "id" payment.
     *
     * @param id the id of the payment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.debug("REST request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
