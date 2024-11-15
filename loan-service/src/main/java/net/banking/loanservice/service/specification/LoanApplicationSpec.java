package net.banking.loanservice.service.specification;

import net.banking.loanservice.entities.LoanApplication;
import org.springframework.data.jpa.domain.Specification;

public class LoanApplicationSpec {

    public static Specification<LoanApplication> filterWithoutAnyConditions(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<LoanApplication> identifierEqual(String identifier){
        return (root, query, criteriaBuilder) ->
                identifier == null || identifier.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("identifier"),identifier);
    }
    public static Specification<LoanApplication> loanTypeLike(String loanType){
        return (root, query, criteriaBuilder) ->
                loanType == null || loanType.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("loanType")),loanType.toLowerCase());
    }
    public static Specification<LoanApplication> loanTermEqual(Integer loanTerm){
        return (root, query, criteriaBuilder) ->
                loanTerm == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("loanTerm"),loanTerm);
    }
    public static Specification<LoanApplication> amountEqual(Double amount){
        return (root, query, criteriaBuilder) ->
                amount == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("requestedAmount"),amount);
    }
    public static Specification<LoanApplication> amountBetween(Double minAmount,Double maxAmount){
        return (root, query, criteriaBuilder) ->
                minAmount == null || maxAmount == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.between(root.get("requestedAmount"),minAmount,maxAmount);
    }
    public static Specification<LoanApplication> statusLike(String status){
        return (root, query, criteriaBuilder) ->
                status == null || status.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")),status.toLowerCase());
    }
    public static Specification<LoanApplication> customerEqual(String customerIdentity) {
        return (root, query, criteriaBuilder) ->
                customerIdentity == null || customerIdentity.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("customerIdentity"),customerIdentity);
    }

}
