package mg.working.cinema.service.facturation;

import java.time.LocalDateTime;

public class FacturationLineDto {
    private String idSociete;
    private String nomSociete;
    private String idSeance;
    private LocalDateTime debut;
    private String titre;

    private int nbDiffusions;
    private double totalDiffusion;

    private double tauxPaiement;   // 0..1
    private double resteAPayer;    // totalDiffusion - totalDiffusion*taux

    public FacturationLineDto() {}

    // getters/setters
    public String getIdSociete() { return idSociete; }
    public void setIdSociete(String idSociete) { this.idSociete = idSociete; }

    public String getNomSociete() { return nomSociete; }
    public void setNomSociete(String nomSociete) { this.nomSociete = nomSociete; }

    public String getIdSeance() { return idSeance; }
    public void setIdSeance(String idSeance) { this.idSeance = idSeance; }

    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public int getNbDiffusions() { return nbDiffusions; }
    public void setNbDiffusions(int nbDiffusions) { this.nbDiffusions = nbDiffusions; }

    public double getTotalDiffusion() { return totalDiffusion; }
    public void setTotalDiffusion(double totalDiffusion) { this.totalDiffusion = totalDiffusion; }

    public double getTauxPaiement() { return tauxPaiement; }
    public void setTauxPaiement(double tauxPaiement) { this.tauxPaiement = tauxPaiement; }

    public double getResteAPayer() { return resteAPayer; }
    public void setResteAPayer(double resteAPayer) { this.resteAPayer = resteAPayer; }
}
