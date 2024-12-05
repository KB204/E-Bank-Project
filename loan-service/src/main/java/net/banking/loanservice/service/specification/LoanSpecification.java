package net.banking.loanservice.service.specification;

import net.banking.loanservice.entities.Loan;
import net.banking.loanservice.entities.SecuredLoan;
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
    public static Specification<Loan> securedLoanOnly(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.type(), SecuredLoan.class);
    }
    public static Specification<Loan> identifierEqual(String identifier){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(identifier)
                        .map(loan -> criteriaBuilder.equal(root.get("loanApplication").get("identifier"),identifier))
                        .orElse(criteriaBuilder.conjunction());
    }
    public static Specification<Loan> amountEqual(Double amount){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(amount)
                        .map(loan -> criteriaBuilder.equal(root.get("principleAmount"),amount))
                        .orElse(criteriaBuilder.conjunction());
    }
    public static Specification<Loan> statusEqual(String status){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(status)
                        .map(loan -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")),
                                status.toLowerCase()))
                        .orElse(criteriaBuilder.conjunction());
    }
    public static Specification<Loan> startedDateLike(String started){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(started)
                        .map(date -> criteriaBuilder.like(root.get("startedDate").as(String.class),started + "%"))
                        .orElse(criteriaBuilder.conjunction());
    }
    public static Specification<Loan> endDateLike(String ended){
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(ended)
                        .map(date -> criteriaBuilder.like(root.get("endDate").as(String.class),ended + "%"))
                        .orElse(criteriaBuilder.conjunction());
    }
    public static Specification<Loan> loanBetween(LocalDate start, LocalDate end) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (start != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("startedDate"),start));
            }

            if (end != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("startedDate"), end));
            }

            return predicate;
        };
    }
}
