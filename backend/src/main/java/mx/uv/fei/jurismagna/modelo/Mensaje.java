package mx.uv.fei.jurismagna.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

// Mensaje del chat. hilo identifica la conversación:
//   cliente-{clienteId}         abogado <-> cliente
//   admin-abogado-{abogadoId}   admin   <-> abogado
@Entity
@Table(name = "mensaje", indexes = @Index(name = "idx_mensaje_hilo", columnList = "hilo"))
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String hilo;

    // abogado, cliente o admin
    @Column(nullable = false, length = 10)
    private String remitente;

    // texto, audio, img o file
    @Column(nullable = false, length = 10)
    private String tipo = "texto";

    @Column(columnDefinition = "TEXT")
    private String texto;

    @Column(length = 255)
    private String archivoUrl;

    @Column(length = 150)
    private String nombreArchivo;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Boolean leido = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHilo() {
        return hilo;
    }

    public void setHilo(String hilo) {
        this.hilo = hilo;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }
}
