package net.banking.accountservice.service.specification;

import jakarta.persistence.criteria.Predicate;
import net.banking.accountservice.model.BankAccountTransaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OperationSpecification {

    public static Specification<BankAccountTransaction> filterWithoutConditions() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<BankAccountTransaction> amountEqual(Double amount) {
        return (root, query, criteriaBuilder) ->
                amount == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("amount"),amount);
    }
    public static Specification<BankAccountTransaction> amountBetween(Double minAmount,Double maxAmount){
        return (root, query, criteriaBuilder) ->
                minAmount == null || maxAmount == null ? criteriaBuilder.conjunction() : criteriaBuilder.between(root.get("amount"),minAmount,maxAmount);
    }

    public static Specification<BankAccountTransaction> transactionTypeEqual(String transactionType){
        return (root, query, criteriaBuilder) ->
                transactionType == null || transactionType.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("transactionType")),
                                transactionType.toLowerCase());
    }

    public static Specification<BankAccountTransaction> ribEqual(String rib){
        return (root, query, criteriaBuilder) ->
                rib == null || rib.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("bankAccount").get("rib"),rib);
    }
    public static Specification<BankAccountTransaction> customerEqual(String customerIdentity) {
        return (root, query, criteriaBuilder) ->
                customerIdentity == null || customerIdentity.trim().isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("bankAccount").get("customerIdentity"),customerIdentity);
    }
    public static Specification<BankAccountTransaction> transactionDateBetween(LocalDateTime startDate,LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (startDate != null){
                LocalDateTime truncatedStartDate = startDate.truncatedTo(ChronoUnit.MINUTES);
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),truncatedStartDate));
            }
            if (endDate != null){
                LocalDateTime truncatedEndDate = endDate.truncatedTo(ChronoUnit.MINUTES);
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"),truncatedEndDate));
            }
            return predicate;
        };
    }

    public static Specification<BankAccountTransaction> transactionDateLike(String createdAt) {
        if (createdAt == null || createdAt.trim().isEmpty() ){
            return null;
        } else {
            return (root, query, criteriaBuilder) -> {
                Predicate dateP = criteriaBuilder.conjunction();
                switch (createdAt.length()) {
                    case 4 -> dateP = criteriaBuilder.and(dateP,
                            criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, root.get("createdAt")), Integer.parseInt(createdAt)));
                    case 7 -> {
                        String[] dateParts = createdAt.split("-");
                        dateP = criteriaBuilder.and(dateP,
                                criteriaBuilder.equal(criteriaBuilder.function("year", Integer.class, root.get("createdAt")), Integer.parseInt(dateParts[0])),
                                criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, root.get("createdAt")), Integer.parseInt(dateParts[1])));
                    }
                    case 10 -> {
                        String[] dateParts = createdAt.split("-");
                        dateP = criteriaBuilder.and(dateP,
                                criteriaBuilder.equal(criteriaBuilder.function("year",Integer.class, root.get("createdAt")), Integer.parseInt(dateParts[0])),
                                criteriaBuilder.equal(criteriaBuilder.function("month", Integer.class, root.get("createdAt")), Integer.parseInt(dateParts[1])),
                                criteriaBuilder.equal(criteriaBuilder.function("day", Integer.class, root.get("createdAt")), Integer.parseInt(dateParts[2])));
                    }
                    default -> {}
                }
                return dateP;
            };
        }
    }
}
