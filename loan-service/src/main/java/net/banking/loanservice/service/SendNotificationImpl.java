package net.banking.loanservice.service;

import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dto.EmailDetails;
import net.banking.loanservice.entities.LoanApplication;
import net.banking.loanservice.exceptions.ResourceNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendNotificationImpl implements SendNotificationService{
    private final RabbitTemplate rabbitTemplate;
    private final LoanApplicationRepository repository;

    @Value("${rabbitmq.exchange.notification.name}")
    private String emailExchange;
    @Value("${rabbitmq.binding.notification.name}")
    private String emailRoutingKey;

    public SendNotificationImpl(RabbitTemplate rabbitTemplate, LoanApplicationRepository repository) {
        this.rabbitTemplate = rabbitTemplate;
        this.repository = repository;
    }
    @Override
    public void loanPendingNotification(String identifier) {
        LoanApplication loanApplication = repository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));

        String email = loanApplication.getCustomerEmail();
        EmailDetails emailDetails = EmailDetails.builder()
                .to(email)
                .body(String.format("""
                        Madame/Monsieur,
                                                
                        Nous vous confirmons que votre demande de crédit de %s , du montant de %sMAD et d'une durée de %s Mois a bien été reçue et qu'elle est actuellement en cours de traitement.
                                                
                        Votre dossier est en cours d'analyse par nos équipes. Nous nous engageons à vous informer dès que le traitement sera terminé. Vous recevrez une notification dans les prochains jours pour vous indiquer l'état de votre demande.
                                                
                        Pour suivre l'évolution de votre demande, vous pouvez utiliser l'identifiant suivant : [%s]. En cas de besoin, n'hésitez pas à nous contacter avec cet identifiant pour toute question ou information complémentaire.
                                                
                        Nous vous remercions de la confiance que vous nous accordez et restons à votre disposition pour toute assistance.
                                                
                        Cordialement,
                        BCP
                        
                        """,loanApplication.getLoanType(),loanApplication.getRequestedAmount(),loanApplication.getLoanTerm(),loanApplication.getIdentifier()))
                .subject("Confirmation de réception de votre demande de crédit")
                .build();
        rabbitTemplate.convertAndSend(emailExchange,emailRoutingKey,emailDetails);
    }

    @Override
    public void loanApprovedNotification(String identifier) {
        LoanApplication loanApplication = repository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));

        String email = loanApplication.getCustomerEmail();
        EmailDetails emailDetails = EmailDetails.builder()
                .to(email)
                .body(String.format("""
                        Madame/Monsieur,
                                                
                        Nous avons le plaisir de vous informer que votre demande de crédit a été approuvée.
                                                
                        Votre crédit de %s d'un montant de %sMAD et d'une durée de %s Mois a été accepté. Vous recevrez les détails complets du contrat dans les prochains jours, y compris les informations concernant le taux d'intérêt, les échéances de paiement, et les conditions générales.
                                                
                        Pour finaliser le processus, veuillez prendre rendez-vous pour signer les documents.
                                                
                        Si vous avez des questions ou besoin de plus d'informations, n'hésitez pas à nous contacter. Nous sommes à votre disposition pour vous accompagner.
                                                
                        Merci de votre confiance.
                                                
                        Cordialement,
                        BCP
                 
                        """,loanApplication.getLoanType(),loanApplication.getRequestedAmount(),loanApplication.getLoanTerm()))
                .subject("Approbation de votre demande de crédit")
                .build();
        rabbitTemplate.convertAndSend(emailExchange,emailRoutingKey,emailDetails);
    }

    @Override
    public void loanDeclinedNotification(String identifier) {
        LoanApplication loanApplication = repository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));

        String email = loanApplication.getCustomerEmail();
        EmailDetails emailDetails = EmailDetails.builder()
                .to(email)
                .body(String.format("""
                        Madame/Monsieur ,
                                                
                        Après une analyse approfondie de votre demande de crédit de %s, nous regrettons de vous informer que celle-ci n'a pas pu être approuvée à ce jour.
                                                
                        Cette décision est basée sur des informations financières manquantes. Nous comprenons que cela puisse être une déception et nous vous invitons à nous contacter pour discuter des raisons de ce refus. Nous restons à votre disposition pour vous aider à mieux comprendre cette décision et vous proposer des alternatives si possible.
                                                
                        Nous vous remercions pour l'intérêt que vous portez à notre banque et restons à votre disposition pour toute question supplémentaire.
                                                
                        Cordialement,
                        BCP
                        
                        """,loanApplication.getLoanType()))
                .subject("Mise à jour concernant votre demande de crédit")
                .build();
        rabbitTemplate.convertAndSend(emailExchange,emailRoutingKey,emailDetails);

    }
}
