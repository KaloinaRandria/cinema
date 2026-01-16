package mg.working.cinema.service.film;

import mg.working.cinema.model.film.Seance;
import mg.working.cinema.repository.film.SeanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeanceService {
    @Autowired
    SeanceRepo seanceRepo;


        public Seance creerSeance(Seance seance) {

            // 0️⃣ Vérifications null (sécurité)
            if (seance == null) {
                throw new IllegalArgumentException("La séance est invalide");
            }

            if (seance.getDebut() == null) {
                throw new IllegalArgumentException("La date de début est obligatoire");
            }

            if (seance.getFilm() == null) {
                throw new IllegalArgumentException("Le film est obligatoire");
            }

            if (seance.getSalle() == null) {
                throw new IllegalArgumentException("La salle est obligatoire");
            }

            // 1️⃣ Prix >= 0
            if (seance.getPrix() < 0) {
                throw new IllegalArgumentException("Le prix de la séance doit être positif");
            }

            // 2️⃣ Calcul automatique de la date de fin (recommandé)
            // Si tu préfères la saisir manuellement, supprime ce bloc
            long dureeFilm = (long) seance.getFilm().getDuree();
            seance.setFin(seance.getDebut().plusMinutes(dureeFilm));

            // 3️⃣ Validation métier : fin > début
            if (!seance.getFin().isAfter(seance.getDebut())) {
                throw new IllegalArgumentException(
                        "La date de fin doit être postérieure à la date de début"
                );
            }

            // 4️⃣ Séance dans le futur (optionnel)
            if (seance.getDebut().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException(
                        "Impossible de créer une séance dans le passé"
                );
            }

            // 5️⃣ Conflit de séance dans la même salle
            boolean conflitSalle = seanceRepo.existsConflitSalle(
                    seance.getSalle().getId(),
                    seance.getDebut(),
                    seance.getFin()
            );

            if (conflitSalle) {
                throw new IllegalStateException(
                        "Conflit : une autre séance est déjà programmée dans cette salle sur ce créneau"
                );
            }

            // 6️⃣ Sauvegarde
            return seanceRepo.save(seance);
        }
    public List<Seance> listerSeances() {
        return seanceRepo.findAllOrderByDebutDesc();
    }

    public Optional<Seance> getById(String id) {
        return seanceRepo.findById(id);
    }
}

