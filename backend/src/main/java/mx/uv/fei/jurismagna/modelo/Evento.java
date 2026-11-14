package mx.uv.fei.jurismagna.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Evento del calendario. alcance separa las agendas:
//   admin          agenda del administrador
//   abogado-{id}   agenda de cada abogado
@Entity
@Table(name = "evento", indexes = @Index(name = "idx_evento_alcance_fecha", columnList = "alcance, fecha"))
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false)
    private LocalDate fecha;

    // Formato HH:MM, como el campo de hora del formulario
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;

    @Column(length = 150)
    private String lugar;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 7)
    private String color = "#D4AF37";

    @Column(nullable = false, length = 20)
    private String alcance;

    // Usuario que creó el evento; si se borra, la BD borra sus eventos
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    // Solo llega en el POST; no es columna de la tabla
    @Transient
    private Integer usuarioId;

    public Integer getUsuarioId() {
        if (usuario == null) {
            return usuarioId;
        }

        return usuario.getId();
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
