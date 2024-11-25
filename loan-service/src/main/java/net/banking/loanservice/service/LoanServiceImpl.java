package net.banking.loanservice.service;

import net.banking.loanservice.client.BankAccountRestClient;
import net.banking.loanservice.client.CustomerRestClient;
import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dao.LoanRepository;
import net.banking.loanservice.dto.external_services.BankAccount;
import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanRequest;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import net.banking.loanservice.entities.*;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.enums.LoanStatus;
import net.banking.loanservice.exceptions.BankAccountException;
import net.banking.loanservice.exceptions.FileHandlingException;
import net.banking.loanservice.exceptions.ResourceAlreadyExists;
import net.banking.loanservice.exceptions.ResourceNotFoundException;
import net.banking.loanservice.mapper.LoanMapper;
import net.banking.loanservice.service.specification.LoanSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class LoanServiceImpl implements LoanService{
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRestClient restClient;
    private final BankAccountRestClient bankAccountRestClient;
    private final LoanMapper mapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public LoanServiceImpl(LoanRepository loanRepository, LoanApplicationRepository loanApplicationRepository, CustomerRestClient restClient, BankAccountRestClient bankAccountRestClient, LoanMapper mapper) {
        this.loanRepository = loanRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.restClient = restClient;
        this.bankAccountRestClient = bankAccountRestClient;
        this.mapper = mapper;
    }

    @Override
    public Page<LoanResponse> findAllLoans(String identifier, Double amount, String status, String started,String ended,
                                           LocalDate start, LocalDate end, Pageable pageable) {

        Specification<Loan> specification = LoanSpecification.filterWithoutAnyConditions()
                .and(LoanSpecification.identifierEqual(identifier))
                .and(LoanSpecification.amountEqual(amount))
                .and(LoanSpecification.statusEqual(status))
                .and(LoanSpecification.startedDateLike(started))
                .and(LoanSpecification.endDateLike(ended))
                .and(LoanSpecification.loanBetween(start, end));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startedDate").descending());

        return loanRepository.findAll(specification,pageable)
                .map(loan -> {
                    loan.setCustomer(restClient.fetchCustomerByIdentity(loan.getLoanApplication().getCustomerIdentity()));
                    return mapper.loanObjectToDtoResponse(loan);
                });
    }

    @Override
    public Page<SecuredLoanResponse> findAllSecuredLoans(String identifier, Double amount, String status, String started,String ended,
                                                         LocalDate start, LocalDate end, Pageable pageable) {

        Specification<Loan> specification = Specification.where(LoanSpecification.securedLoanOnly())
                .and(LoanSpecification.identifierEqual(identifier))
                .and(LoanSpecification.amountEqual(amount))
                .and(LoanSpecification.statusEqual(status))
                .and(LoanSpecification.startedDateLike(started))
                .and(LoanSpecification.endDateLike(ended))
                .and(LoanSpecification.loanBetween(start, end));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startedDate").descending());

        return loanRepository.findAll(specification,pageable)
                .map(loan -> {
                    SecuredLoan securedLoan = (SecuredLoan) loan;
                    securedLoan.setCustomer(restClient.fetchCustomerByIdentity(securedLoan.getLoanApplication().getCustomerIdentity()));
                    return mapper.securedLoanToDtoResponse(securedLoan);
                });
    }

    @Override
    public Page<UnsecuredLoanResponse> findAllUnsecuredLoans(String identifier, Double amount, String status, String started,String ended,
                                                             LocalDate start, LocalDate end, Pageable pageable) {

        Specification<Loan> specification = Specification.where(LoanSpecification.unsecuredLoansOnly())
                .and(LoanSpecification.identifierEqual(identifier))
                .and(LoanSpecification.amountEqual(amount))
                .and(LoanSpecification.statusEqual(status))
                .and(LoanSpecification.startedDateLike(started))
                .and(LoanSpecification.endDateLike(ended))
                .and(LoanSpecification.loanBetween(start, end));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startedDate").descending());

        return loanRepository.findAll(specification,pageable)
                .map(loan -> {
                    UnsecuredLoan unsecuredLoan = (UnsecuredLoan) loan;
                    unsecuredLoan.setCustomer(restClient.fetchCustomerByIdentity(unsecuredLoan.getLoanApplication().getCustomerIdentity()));
                    return mapper.unsecuredLoanToDtoResponse(unsecuredLoan);
                });
    }

    @Override
    public void createSecuredLoan(SecuredLoanRequest request,List<MultipartFile> files) {
        LoanApplication loanApplication = getLoanApplication(request.identifier());
        BankAccount bankAccount = getBankAccount(request.rib(),request.identity());
        checkLoanAlreadyExists(request.identifier());
        checkBusinessRules(loanApplication);

        String uploadDir = createUploadDirectory();
        List<Collateral> collaterals = saveFiles(files,uploadDir,request);

        SecuredLoan loan = SecuredLoan.builder()
                .status(LoanStatus.ACTIVE)
                .principleAmount(loanApplication.getRequestedAmount())
                .remainingBalance(loanApplication.getRequestedAmount())
                .interest(loanApplication.getInterest())
                .startedDate(LocalDate.now())
                .bankAccountRib(bankAccount.rib())
                .loanApplication(loanApplication)
                .collaterals(collaterals)
                .build();

        finalizeAndSaveLoan(loan);
    }

    @Override
    public void createUnsecuredLoan(UnsecuredLoanRequest request) {
        LoanApplication loanApplication = getLoanApplication(request.identifier());
        BankAccount bankAccount = getBankAccount(request.rib(),request.identity());
        checkLoanAlreadyExists(request.identifier());
        checkBusinessRules(loanApplication);

        UnsecuredLoan loan = UnsecuredLoan.builder()
                .status(LoanStatus.ACTIVE)
                .principleAmount(loanApplication.getRequestedAmount())
                .remainingBalance(loanApplication.getRequestedAmount())
                .interest(loanApplication.getInterest())
                .startedDate(LocalDate.now())
                .bankAccountRib(bankAccount.rib())
                .loanApplication(loanApplication)
                .build();

        finalizeAndSaveLoan(loan);
    }

    @Override
    public Resource getFile(String identifier, int fileIndex) {
        SecuredLoan loan = (SecuredLoan) loanRepository.findByLoanApplication_Identifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("La demande identifiée par %s n'existe pas", identifier)));

        List<Collateral> files = loan.getCollaterals();
        if (fileIndex < 0 || fileIndex >= files.size()) {
            throw new FileHandlingException("Fichier inexistant");
        }

        Collateral collateral = files.get(fileIndex);
        Path filePath = Paths.get(collateral.getUrl());
        Resource fileResource = new FileSystemResource(filePath);

        if (!fileResource.exists()){
            throw new FileHandlingException("Fichier inexistant");
        }

        return fileResource;
    }

    private LoanApplication getLoanApplication(String identifier) {
        return loanApplicationRepository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("La demande identifiée par %s n'existe pas", identifier)));
    }
    private BankAccount getBankAccount(String rib, String identity) {
        return bankAccountRestClient.findBankAccount(rib, identity);
    }
    private void checkLoanAlreadyExists(String identifier) {
        loanRepository.findByLoanApplication_Identifier(identifier)
                .ifPresent(loan -> {
                    throw new ResourceAlreadyExists(
                            String.format("Crédit identifié par %s exists déjà", identifier));
                });
    }
    private void finalizeAndSaveLoan(Loan loan) {
        Double monthlyInstallment = calculateMonthlyInstallment(loan);
        LocalDate endDate = calculateLoanEndingDate(loan);

        loan.setMonthlyInstallment(monthlyInstallment);
        loan.setEndDate(endDate);

        checkBusinessRules(loan);
        loanRepository.save(loan);
    }
    private LocalDate calculateLoanEndingDate(Loan loan){
        return loan.getStartedDate().plusMonths(loan.getLoanApplication().getLoanTerm());
    }
    private Double calculateMonthlyInstallment(Loan loan){
        double monthlyInterest = (loan.getInterest() / 100) / 12;
        int loanTerm = loan.getLoanApplication().getLoanTerm();
        double treat = (loan.getPrincipleAmount() * monthlyInterest * Math.pow(1 + monthlyInterest, loanTerm)) /
                (Math.pow(1 + monthlyInterest, loanTerm) - 1);

        return BigDecimal.valueOf(treat)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    private void checkBusinessRules(LoanApplication loanApplication){
        if (loanApplication.getStatus().equals(ApplicationStatus.REJECTED))
            throw new BankAccountException(String.format("La demande identifiée par %s a été rejetée",loanApplication.getIdentifier()));
        if (loanApplication.getStatus().equals(ApplicationStatus.PENDING))
            throw new BankAccountException(String.format("La demande identifiée par %s est en cours de traitement",loanApplication.getIdentifier()));
    }
    private void checkBusinessRules(Loan loan){
        Optional.ofNullable(loan.getRemainingBalance())
                .filter(remainingBalance -> Double.compare(remainingBalance,0.0) == 0)
                .ifPresent(remainingBalance -> loan.setStatus(LoanStatus.CLOSED));
    }
    private boolean isSupportedContentType(String contentType) {
        return "image/jpeg".equals(contentType) || "application/pdf".equals(contentType) || "image/png".equals(contentType);
    }
    private String createUploadDirectory(){
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new FileHandlingException("Erreur lors du stockage des fichiers: " + e.getMessage());
            }
        }
        return uploadDir;
    }
    private List<Collateral> saveFiles(List<MultipartFile> files, String uploadLocation,SecuredLoanRequest request){
        List<Collateral> collaterals = new ArrayList<>();
        long totalSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();
        if (totalSize > 20_000_000){
            throw new FileHandlingException("La taille totale des fichiers dépasse la limite");
        }

        files.stream()
                .map(file -> {
                    try {
                        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                        validateFile(file);
                        String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                        String filePath = Paths.get(uploadLocation, storedFileName).toString();
                        Files.copy(file.getInputStream(), Paths.get(filePath));
                        return Collateral.builder()
                                .url(filePath)
                                .description(request.description())
                                .value(request.value())
                                .isVerified(request.isVerified())
                                .type(request.type())
                                .build();
                    } catch (IOException ex){
                        throw new FileHandlingException("Erreur lors du stockage des fichiers: " + ex.getMessage());
                    }
                })
                .forEach(collaterals::add);

        return collaterals;
    }
    private void validateFile(MultipartFile file){
        String contentType = file.getContentType();
        if (!isSupportedContentType(contentType)) {
            throw new FileHandlingException("Type de fichier non valide. Seuls les formats PDF, PNG et JPEG sont autorisés");
        }
        if (file.getSize() > 5_000_000) {
            throw new FileHandlingException("La taille du fichier dépasse la limite maximale");
        }
    }
}
