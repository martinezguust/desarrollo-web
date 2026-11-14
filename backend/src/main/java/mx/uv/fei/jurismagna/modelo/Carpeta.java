package mx.uv.fei.jurismagna.modelo;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Carpeta del drive de un caso
@Entity
@Table(name = "carpeta")
public class Carpeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Si se borra el caso, la BD borra sus carpetas (ON DELETE CASCADE)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "caso_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Caso caso;

    @Column(length = 20)
    private String numero;

    @Column(length = 50)
    private String materia;

    @Column(length = 150)
    private String proyecto;

    @Column(length = 30)
    private String estado;

    @Column(length = 255)
    private String ligaUrl;

    @Column(nullable = false)
    private LocalDate fecha;

    public Integer getCasoId() {
        if (caso == null) {
            return null;
        }

        return caso.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Caso getCaso() {
        return caso;
    }

    public void setCaso(Caso caso) {
        this.caso = caso;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLigaUrl() {
        return ligaUrl;
    }

    public void setLigaUrl(String ligaUrl) {
        this.ligaUrl = ligaUrl;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
