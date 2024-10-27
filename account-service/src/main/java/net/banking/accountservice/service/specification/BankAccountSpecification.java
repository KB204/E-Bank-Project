package net.banking.accountservice.service.specification;

import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.model.CurrentAccount;
import net.banking.accountservice.model.SavingAccount;
import org.springframework.data.jpa.domain.Specification;

public class BankAccountSpecification {

    public static Specification<BankAccount> filterWithoutConditions() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
    public static Specification<BankAccount> currentAccountsOnly() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.type(), CurrentAccount.class);
    }
    public static Specification<BankAccount> savingAccountOnly() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.type(), SavingAccount.class);
    }
    public static Specification<BankAccount> ribEqual(String rib){
        if (rib == null || rib.trim().isEmpty()){
            return null;
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("rib"),rib);
        }
    }
    public static Specification<BankAccount> branchLike(String branch){
        if (branch == null || branch.trim().isEmpty()){
            return null;
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("branch")),
                            "%" + branch.toLowerCase() + "%");
        }
    }
    public static Specification<BankAccount> statusEqual(String accountStatus){
        if (accountStatus == null || accountStatus.trim().isEmpty()){
            return null;
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("accountStatus")),
                            accountStatus.toLowerCase());
        }
    }
    public static Specification<BankAccount> customerIdentityEqual(String identity){
        if (identity == null || identity.trim().isEmpty()){
            return null;
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("customerIdentity")),
                            identity.toLowerCase());
        }
    }
}
