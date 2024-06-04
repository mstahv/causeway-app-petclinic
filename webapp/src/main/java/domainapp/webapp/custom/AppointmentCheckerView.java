package domainapp.webapp.custom;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import domainapp.modules.visit.dom.visit.Visit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

@Route("") // maps to root of Vaadin servlet
public class AppointmentCheckerView extends VerticalLayout {
    TextField name  = new TextField("Your name");
    TextField pet  = new TextField("Pet name");
    Button search = new Button("Search");
    public Div searchResults = new Div();

    public AppointmentCheckerView(PublicAppointmentService publicAppointmentService) {
        add(new H1("Check your appointment"));
        add(new Paragraph("Forgot your appointment time? No worries, as long as you still remember your own name and your pet's name, check out your appointments below."));
        var searchForm = new HorizontalLayout(name, pet, search);
        searchForm.setAlignItems(Alignment.END);
        add(searchForm);
        add(searchResults);

        search.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        search.addClickShortcut(Key.ENTER);

        search.addClickListener(e -> {
            searchResults.removeAll();
            List<Visit> visits = publicAppointmentService.findByOwnerAndPet(name.getValue(), pet.getValue());
            if(visits.isEmpty()) {
                searchResults.add(new H3("No appointments found, check your search terms or call customer service!"));
            } else {
                searchResults.add(new H3("Following appointment times for " + pet.getValue() + " found:"));
                visits.forEach(visit -> {
                    var dateTime = visit.getVisitAt();
                    var p = new Paragraph(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(dateTime));
                    if(dateTime.isBefore(LocalDateTime.now())) {
                        p.getStyle().setColor("gray");
                    }
                    searchResults.add(p);
                });
            }
        });
    }
}