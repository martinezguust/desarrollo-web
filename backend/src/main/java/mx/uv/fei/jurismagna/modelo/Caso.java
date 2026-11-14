package mx.uv.fei.jurismagna.modelo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Caso (expediente); el backend genera el número EXP-0001, EXP-0002, ...
@Entity
@Table(name = "caso")
public class Caso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 10)
    private String numeroExpediente;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "abogado_id", nullable = false)
    private Abogado abogado;

    @Column(nullable = false, length = 40)
    private String materia;

    // Activo, EnProceso, Ganado, Perdido o Cancelado
    @Column(nullable = false, length = 10)
    private String estado = "Activo";

    @Column(nullable = false)
    private LocalDate fechaInicio;

    // Se llena cuando el caso se cierra (Ganado, Perdido o Cancelado)
    private LocalDate fechaFin;

    @Column(length = 255)
    private String driveUrl;

    // Solo llegan en el POST; no son columnas de la tabla
    @Transient
    private Integer clienteId;

    @Transient
    private Integer abogadoId;

    public Integer getClienteId() {
        if (cliente == null) {
            return clienteId;
        }

        return cliente.getId();
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        if (cliente == null) {
            return null;
        }

        return cliente.getNombre();
    }

    public Integer getAbogadoId() {
        if (abogado == null) {
            return abogadoId;
        }

        return abogado.getId();
    }

    public void setAbogadoId(Integer abogadoId) {
        this.abogadoId = abogadoId;
    }

    public String getAbogadoNombre() {
        if (abogado == null) {
            return null;
        }

        return abogado.getNombre();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Abogado getAbogado() {
        return abogado;
    }

    public void setAbogado(Abogado abogado) {
        this.abogado = abogado;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDriveUrl() {
        return driveUrl;
    }

    public void setDriveUrl(String driveUrl) {
        this.driveUrl = driveUrl;
    }
}
