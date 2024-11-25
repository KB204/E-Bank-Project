package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanRequest;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import net.banking.loanservice.service.LoanService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<LoanResponse> getAllLoans(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String started,
            @RequestParam(required = false) String ended,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable){
        return service.findAllLoans(identifier, amount, status, started, ended, start, end, pageable);}
    @GetMapping("/allSecuredLoans")
    @ResponseStatus(HttpStatus.OK)
    Page<SecuredLoanResponse> getAllSecuredLoans(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String started,
            @RequestParam(required = false) String ended,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable){
        return service.findAllSecuredLoans(identifier, amount, status, started, ended, start, end, pageable);
    }
    @GetMapping("/allUnsecuredLoans")
    @ResponseStatus(HttpStatus.OK)
    Page<UnsecuredLoanResponse> getAllUnsecuredLoans(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String started,
            @RequestParam(required = false) String ended,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable){
        return service.findAllUnsecuredLoans(identifier, amount, status, started, ended, start, end, pageable);}

    @PostMapping("/createUnsecuredLoan")
    ResponseEntity<String> saveNewUnsecuredLoan(@RequestBody @Valid UnsecuredLoanRequest request){
        service.createUnsecuredLoan(request);
        return new ResponseEntity<>("Crédit a été créé avec succès",HttpStatus.CREATED);
    }
    @PostMapping(value = "/createSecuredLoan",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> saveNewSecuredLoan(
            @ModelAttribute @Valid SecuredLoanRequest request,
            @RequestParam("files") List<MultipartFile> files){
        service.createSecuredLoan(request, files);
        return new ResponseEntity<>("Crédit a été créé avec succès",HttpStatus.CREATED);
    }
    @GetMapping("/loan/{identifier}/file/{fileIndex}")
    ResponseEntity<Resource> getLoanCollateral(@PathVariable String identifier,@PathVariable int fileIndex)
            throws IOException {
        Resource fileResource = service.getFile(identifier, fileIndex);

        String contentType = Optional.ofNullable(Files.probeContentType(Paths.get(fileResource.getURI())))
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(fileResource);
    }
}
