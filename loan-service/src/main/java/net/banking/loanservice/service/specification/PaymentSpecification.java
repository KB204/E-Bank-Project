package net.banking.loanservice.service.specification;

import net.banking.loanservice.entities.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class PaymentSpecification {

    public static Specification<Payment> filterWithoutConditions(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
    public static Specification<Payment> identifierEqual(String identifier){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(identifier)
                        .map(loan -> criteriaBuilder.equal(root.get("loan").get("loanApplication").get("identifier"),identifier))
                        .orElse(null);
    }
    public static Specification<Payment> amountEqual(Double amount){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(amount)
                        .map(paidAmount -> criteriaBuilder.equal(root.get("amountPaid"),amount))
                        .orElse(null);
    }
    public static Specification<Payment> amountBetween(Double minAmount,Double maxAmount){
        return (root, query, criteriaBuilder) ->
                minAmount == null || maxAmount == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.between(root.get("amountPaid"),minAmount,maxAmount);
    }
    public static Specification<Payment> statusLike(String status){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(status)
                        .map(paymentStatus -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")),
                                status.toLowerCase()))
                        .orElse(null);
    }
    public static Specification<Payment> dateLike(String date){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(date)
                        .map(paymentDate -> criteriaBuilder.like(root.get("paymentDate").as(String.class),date + "%"))
                        .orElse(null);
    }
}
