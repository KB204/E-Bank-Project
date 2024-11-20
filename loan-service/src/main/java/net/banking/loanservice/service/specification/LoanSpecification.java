package net.banking.loanservice.service.specification;

import net.banking.loanservice.entities.Loan;
import net.banking.loanservice.entities.UnsecuredLoan;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.Optional;


public class LoanSpecification {

    public static Specification<Loan> filterWithoutAnyConditions(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
    public static Specification<Loan> unsecuredLoansOnly(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.type(), UnsecuredLoan.class);
    }
    public static Specification<Loan> identifierEqual(String identifier){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(identifier)
                        .map(loan -> criteriaBuilder.equal(root.get("loanApplication").get("identifier"),identifier))
                        .orElse(null);
    }
    public static Specification<Loan> amountEqual(Double amount){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(amount)
                        .map(loan -> criteriaBuilder.equal(root.get("principleAmount"),amount))
                        .orElse(null);
    }
    public static Specification<Loan> statusLike(String status){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(status)
                        .map(loan -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")),
                                "%" + status.toLowerCase() + "%"))
                        .orElse(null);
    }
    public static Specification<Loan> startedDateLike(LocalDate started){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(started)
                        .map(date -> criteriaBuilder.like(root.get("startedDate").as(String.class),date.toString()))
                        .orElse(null);
    }
    public static Specification<Loan> endDateLike(LocalDate ended){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(ended)
                        .map(date -> criteriaBuilder.like(root.get("endDate").as(String.class),date.toString()))
                        .orElse(null);
    }
    public static Specification<Loan> loanBetween(LocalDate start, LocalDate end) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (start != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("startedDate").as(String.class), start.toString()));
            }

            if (end != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("startedDate").as(String.class), end.toString()));
            }

            return predicate;
        };
    }
}
