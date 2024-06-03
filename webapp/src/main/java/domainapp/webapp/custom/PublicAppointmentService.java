package domainapp.webapp.custom;

import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.petowner.dom.petowner.PetOwners;
import domainapp.modules.visit.contributions.PetOwner_visits;
import domainapp.modules.visit.dom.visit.Visit;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.apache.causeway.applib.services.iactnlayer.InteractionContext;
import org.apache.causeway.applib.services.iactnlayer.InteractionService;
import org.apache.causeway.applib.services.user.UserMemento;
import org.apache.causeway.applib.services.wrapper.WrapperFactory;
import org.apache.causeway.applib.services.xactn.TransactionalProcessor;
import org.springframework.stereotype.Service;

/**
 * An API for a public UI built for pet owners to check their appointment times.
 *
 * Backend calls run as "sven", so no login is required. Note that this API is not
 * published as such, but can be injected into Vaadin UI classes.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {
    @Inject})
public class PublicAppointmentService {

    private final InteractionService interactionService;
    private final TransactionalProcessor transactionalProcessor;
    private final WrapperFactory wrapperFactory;
    private final PetOwners petOwners;
    
    public List<Visit> findByOwnerAndPet(String ownerName, String petName) {
        Optional<List<Visit>> visits = callAsSven(() -> {
            PetOwner petOwner = petOwners.findByNameExact(ownerName);
            if(petOwner != null) {
                PetOwner_visits petOwnerVisits = wrapperFactory.wrapMixin(PetOwner_visits.class, petOwner);
                return petOwnerVisits.coll().stream()
                        .filter(v -> v.getPet().getName().equals(petName))
                        .toList();
            }
            return null;
        });
        return visits.orElse(Collections.emptyList());
    }

    private <T> Optional<T> callAsSven(final Callable<T> callable) {
        return interactionService.call(
                InteractionContext.ofUserWithSystemDefaults(UserMemento.ofName("sven")),
                () -> transactionalProcessor.callWithinCurrentTransactionElseCreateNew(callable)
        )
                .ifFailureFail()
                .getValue();
    }
}
