package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.pub.PaiementPub;
import mg.working.cinema.service.facturation.FacturationPubService;
import mg.working.cinema.service.facturation.FacturationLineDto;
import mg.working.cinema.service.pub.SocietePubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pub/facturation")
public class PubFacturationController {

    private final FacturationPubService facturationPubService;
    private final SocietePubService societePubService;

    public PubFacturationController(FacturationPubService facturationPubService,
                                    SocietePubService societePubService) {
        this.facturationPubService = facturationPubService;
        this.societePubService = societePubService;
    }

    @GetMapping
    public String page(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "idSociete", required = false) String idSociete,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentUri", request.getRequestURI());

        int y = (year == null ? 2025 : year);
        int m = (month == null ? 12 : month);

        model.addAttribute("year", y);
        model.addAttribute("month", m);
        model.addAttribute("idSociete", idSociete == null ? "" : idSociete);

        // liste societes pour filtre
        model.addAttribute("societes", societePubService.getAll());

        // table facture (toutes sociétés)
        List<FacturationLineDto> rows = facturationPubService.getFacturation(y, m);
        model.addAttribute("rows", rows);

        // bloc historique (si société sélectionnée)
        if (idSociete != null && !idSociete.isBlank()) {
            double due = facturationPubService.totalDueSociete(rows, idSociete);
            double paid = facturationPubService.totalPaidSociete(y, m, idSociete);
            double taux = (due <= 0) ? 0 : Math.min(1, paid / due);
            double reste = due - (due * taux);

            model.addAttribute("dueSociete", due);
            model.addAttribute("paidSociete", paid);
            model.addAttribute("tauxSociete", taux);
            model.addAttribute("resteSociete", reste);

            List<PaiementPub> paiements = facturationPubService.historiquePaiements(y, m, idSociete);
            model.addAttribute("paiements", paiements);
        } else {
            model.addAttribute("dueSociete", 0);
            model.addAttribute("paidSociete", 0);
            model.addAttribute("tauxSociete", 0);
            model.addAttribute("resteSociete", 0);
            model.addAttribute("paiements", List.of());
        }

        return "pub/facturation";
    }
}
