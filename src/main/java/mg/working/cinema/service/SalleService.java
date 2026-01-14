package mg.working.cinema.service;

import mg.working.cinema.model.Salle;
import mg.working.cinema.repository.SalleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalleService {
    @Autowired
    SalleRepo salleRepo;

    public List<Salle> getAllSalle(){
        return salleRepo.findAll();
    }

    public void insertSalle(Salle salle){
        salleRepo.save(salle);
    }

    public Optional<Salle> getSalleById(String id){
        return this.salleRepo.findById(id);
    }
}
