package mg.working.cinema.service;

import mg.working.cinema.model.Salle;
import mg.working.cinema.model.Siege;
import mg.working.cinema.repository.SalleRepo;
import mg.working.cinema.service.film.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalleService {
    @Autowired
    SalleRepo salleRepo;
    @Autowired
    SiegeService siegeService;


    public List<Salle> getAllSalle(){
        return salleRepo.findAll();
    }

    public void insertSalle(Salle salle){
        salleRepo.save(salle);
    }

    public Optional<Salle> getSalleById(String id){
        return this.salleRepo.findById(id);
    }

    public double calculerValeurMax(String idSalle){
    List<Siege> sieges = siegeService.getSiegesBySalle(idSalle);
    double valeurMax = 0.0;
    for (Siege siege : sieges) {
        valeurMax += siege.getTypeSiege().getPrix();
    }
    return valeurMax;
    }


}
