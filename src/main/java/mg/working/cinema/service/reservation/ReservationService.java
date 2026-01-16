package mg.working.cinema.service.reservation;

import jakarta.transaction.Transactional;
import mg.working.cinema.model.Siege;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.reservation.ReservationFille;
import mg.working.cinema.model.reservation.ReservationMere;
import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.repository.SiegeRepo;
import mg.working.cinema.repository.reservation.ReservationFilleRepo;
import mg.working.cinema.repository.reservation.ReservationMereRepo;
import mg.working.cinema.service.film.SeanceService;
import mg.working.cinema.service.user.UtilisateurService;
import mg.working.cinema.service.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationMereRepo mereRepo;
    private final ReservationFilleRepo filleRepo;
    private final SiegeRepo siegeRepo;
    private final SeanceService seanceService;
    private final UtilisateurService utilisateurService;
    private final IdGenerator idGenerator;

    public ReservationService(ReservationMereRepo mereRepo,
                              ReservationFilleRepo filleRepo,
                              SiegeRepo siegeRepo,
                              SeanceService seanceService,
                              UtilisateurService utilisateurService,
                              IdGenerator idGenerator) {
        this.mereRepo = mereRepo;
        this.filleRepo = filleRepo;
        this.siegeRepo = siegeRepo;
        this.seanceService = seanceService;
        this.utilisateurService = utilisateurService;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public String createReservation(String seanceId, Utilisateur user, List<String> seatIds) {

        if (user == null || user.getId() == null) {
            throw new IllegalStateException("Utilisateur invalide.");
        }

        List<String> clean = (seatIds == null ? List.<String>of() : seatIds).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if (clean.isEmpty()) {
            throw new IllegalArgumentException("Veuillez sélectionner au moins un siège.");
        }

        for (String seatId : clean) {
            if (filleRepo.existsBySeanceAndSiege(seanceId, seatId)) {
                throw new IllegalStateException("Un des sièges sélectionnés est déjà réservé. Veuillez recommencer.");
            }
        }

        Seance seance = seanceService.getById(seanceId)
                .orElseThrow(() -> new IllegalArgumentException("Séance introuvable : " + seanceId));

        ReservationMere mere = new ReservationMere();
        mere.setId(idGenerator);
        mere.setReference("RES-" + mere.getId());
        mere.setDateReservation(LocalDateTime.now());
        mere.setUtilisateur(user);   // ✅ plus jamais null
        mere.setSeance(seance);
        mereRepo.save(mere);

        double prix = seance.getPrix();

        for (String seatId : clean) {
            Siege siege = siegeRepo.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Siège introuvable : " + seatId));

            ReservationFille rf = new ReservationFille();
            rf.setId(idGenerator);
            rf.setPrix(prix);
            rf.setReservationMere(mere);
            rf.setSiege(siege);
            filleRepo.save(rf);
        }

        return mere.getId();
    }

}
