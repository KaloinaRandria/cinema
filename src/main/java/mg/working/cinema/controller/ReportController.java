package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.service.report.RecetteSeanceGroupDto;
import mg.working.cinema.service.report.SeanceRecetteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final SeanceRecetteService seanceRecetteService;

    public ReportController(SeanceRecetteService seanceRecetteService) {
        this.seanceRecetteService = seanceRecetteService;
    }

    @GetMapping("/recettes-seances")
    public String recettesSeances(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentUri", request.getRequestURI());

        int m = (month == null ? 12 : month);
        int y = (year == null ? 2025 : year);

        List<RecetteSeanceGroupDto> seances = seanceRecetteService.getRecettesParSeance(y, m);

        model.addAttribute("month", m);
        model.addAttribute("year", y);

        model.addAttribute("seances", seances);

        model.addAttribute("totalPub", seanceRecetteService.totalPub(seances));
        model.addAttribute("totalResa", seanceRecetteService.totalResa(seances));
        model.addAttribute("totalGeneral", seanceRecetteService.totalGeneral(seances));

        // ✅ nouveaux indicateurs pub payée/restante
        model.addAttribute("totalPubPaye", seanceRecetteService.totalPubPaye(seances));
        model.addAttribute("totalPubReste", seanceRecetteService.totalPubReste(seances));

//      //extras
        model.addAttribute("totalExtra", seanceRecetteService.totalExtra(seances));

        return "report/recettes-seances";
    }
}
