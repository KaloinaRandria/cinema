package mg.working.cinema.service.reservation;



import jakarta.transaction.Transactional;
import mg.working.cinema.model.Siege;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.reservation.ReservationFille;
import mg.working.cinema.model.reservation.ReservationMere;
import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.repository.reservation.ReservationFilleRepo;
import mg.working.cinema.repository.reservation.ReservationMereRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMereRepo reservationMereRepo;
    private final ReservationFilleRepo reservationFilleRepo;

    public ReservationServiceImpl(
            ReservationMereRepo reservationMereRepo,
            ReservationFilleRepo reservationFilleRepo
    ) {
        this.reservationMereRepo = reservationMereRepo;
        this.reservationFilleRepo = reservationFilleRepo;
    }

    /**
     * Retourne la liste des ID de sièges déjà réservés pour une séance donnée
     */
    @Override
    public List<String> getOccupiedSeatIds(String idSeance) {

        if (idSeance == null || idSeance.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de la séance est obligatoire");
        }

        return reservationFilleRepo.findOccupiedSeatIdsBySeance(idSeance);
    }

    @Transactional
    public void createReservation(
            String idSeance,
            Utilisateur utilisateur,
            List<String> seatIdsDemandes
    ) {

        // 0️⃣ Sécurité basique
        if (seatIdsDemandes == null || seatIdsDemandes.isEmpty()) {
            throw new IllegalArgumentException("Aucun siège sélectionné");
        }

        // 1️⃣ RÈGLE MÉTIER CRITIQUE : vérifier les sièges déjà pris
        List<String> dejaPris = getOccupiedSeatIds(idSeance);

        for (String seatId : seatIdsDemandes) {
            if (dejaPris.contains(seatId)) {
                throw new IllegalStateException(
                        "Le siège " + seatId + " est déjà réservé"
                );
            }
        }

        // 2️⃣ Création ReservationMere
        ReservationMere rm = new ReservationMere();
        rm.setUtilisateur(utilisateur);
        rm.setSeance(new Seance(idSeance));
        rm.setDateReservation(LocalDateTime.now());
        rm.setReference("RES-" + System.currentTimeMillis());

        reservationMereRepo.save(rm);

        // 3️⃣ Création ReservationFille (1 par siège)
        for (String seatId : seatIdsDemandes) {
            ReservationFille rf = new ReservationFille();
            rf.setReservationMere(rm);
            rf.setSiege(new Siege(seatId));
            rf.setPrix(rm.getSeance().getPrix());

            reservationFilleRepo.save(rf);
        }
    }
}

