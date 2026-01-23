package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.repository.report.SeanceRecetteProjection;
import mg.working.cinema.service.report.SeanceRecetteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final SeanceRecetteService seanceRecetteService;

    public ReportController(SeanceRecetteService seanceRecetteService) {
        this.seanceRecetteService = seanceRecetteService;
    }

    @GetMapping("/recettes-seances")
    public String recettesSeances(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());

        List<SeanceRecetteProjection> rows = seanceRecetteService.getRecettesParSeance();

        model.addAttribute("rows", rows);
        model.addAttribute("totalPub", seanceRecetteService.totalPub(rows));
        model.addAttribute("totalResa", seanceRecetteService.totalResa(rows));
        model.addAttribute("totalGeneral", seanceRecetteService.totalGeneral(rows));

        return "report/recettes-seances";
    }
}
