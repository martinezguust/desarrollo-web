package mx.uv.fei.jurismagna.modelo;

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

// Enlace guardado dentro de una carpeta
@Entity
@Table(name = "liga")
public class Liga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Si se borra la carpeta, la BD borra sus ligas (ON DELETE CASCADE)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "carpeta_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Carpeta carpeta;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String url;

    public Integer getCarpetaId() {
        if (carpeta == null) {
            return null;
        }

        return carpeta.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Carpeta getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(Carpeta carpeta) {
        this.carpeta = carpeta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
