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

    public double calculerPrix(Siege siege, boolean enfant) {
        if (siege == null) {
            throw new IllegalArgumentException("Siège invalide.");
        }
        if (siege.getTypeSiege() == null) {
            throw new IllegalStateException("Type de siège non défini pour le siège : " + siege.getId());
        }

        double prixBase = siege.getTypeSiege().getPrix();

        // Enfant = -50% sur tous les types
        return enfant ? (prixBase * 0.5) : prixBase;
    }

    private double calculerCA(List<ReservationFille> lignes) {
        double ca = 0.0;
        for (ReservationFille fille : lignes) {
            ca += fille.getPrix();
        }
        return ca;
    }

    @Transactional
    public String createReservation(String seanceId,
                                    Utilisateur user,
                                    List<String> seatIds,
                                    Map<String, String> seatCategory) {

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

        // Vérifier sièges déjà pris
        for (String seatId : clean) {
            if (filleRepo.existsBySeanceAndSiege(seanceId, seatId)) {
                throw new IllegalStateException("Un des sièges sélectionnés est déjà réservé. Veuillez recommencer.");
            }
        }

        Seance seance = seanceService.getById(seanceId)
                .orElseThrow(() -> new IllegalArgumentException("Séance introuvable : " + seanceId));

        // Création de la mère
        ReservationMere mere = new ReservationMere();
        mere.setId(idGenerator);
        mere.setReference("RES-" + mere.getId());
        mere.setDateReservation(LocalDateTime.now());
        mere.setUtilisateur(user);
        mere.setSeance(seance);
        mereRepo.save(mere);

        // Création des lignes
        List<ReservationFille> lignes = new ArrayList<>();

        for (String seatId : clean) {
            Siege siege = siegeRepo.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Siège introuvable : " + seatId));

            String cat = (seatCategory != null) ? seatCategory.get(seatId) : null;
            boolean enfant = "CHILD".equalsIgnoreCase(cat);  // ✅ enfant uniquement si CHILD

            double prix = calculerPrix(siege, enfant);

            // debug (à enlever après)
            System.out.println("seat=" + seatId + " cat=" + cat + " enfant=" + enfant + " prix=" + prix);

            ReservationFille rf = new ReservationFille();
            rf.setId(idGenerator); // à adapter si generate()
            rf.setReservationMere(mere);
            rf.setSiege(siege);
            rf.setPrix(prix);

            lignes.add(rf);
        }

        filleRepo.saveAll(lignes);

        // CA (total)
        double ca = calculerCA(lignes);

        // Optionnel : stocker ca dans ReservationMere si tu ajoutes un champ total
        // mere.setTotal(ca);
        // mereRepo.save(mere);

        return mere.getId();
    }


}
