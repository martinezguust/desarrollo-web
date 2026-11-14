package mx.uv.fei.jurismagna.modelo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Cliente del despacho; tiene su usuario (rol CLIENTE) y un abogado asignado
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Las relaciones no se mandan completas en el JSON; solo sus ids y nombres
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "abogado_id", nullable = false)
    private Abogado abogado;

    @Column(nullable = false, length = 100)
    private String nombre;

    private Integer edad;

    @Column(length = 150)
    private String estudios;

    @Column(length = 20)
    private String ine;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 500)
    private String descripcion;

    // Porcentaje de avance del caso (0 a 100)
    @Column(nullable = false)
    private Integer avance = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal honorarios = BigDecimal.ZERO;

    @Column(length = 255)
    private String driveUrl;

    // Texto que el cliente escucha con síntesis de voz
    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false, length = 10)
    private String estado = "Activo";

    // Solo llegan en el POST; no son columnas de la tabla
    @Transient
    private Integer abogadoId;

    @Transient
    private String username;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public Integer getUsuarioId() {
        if (usuario == null) {
            return null;
        }

        return usuario.getId();
    }

    public String getUsername() {
        if (usuario == null) {
            return username;
        }

        return usuario.getUsername();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Abogado getAbogado() {
        return abogado;
    }

    public void setAbogado(Abogado abogado) {
        this.abogado = abogado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getEstudios() {
        return estudios;
    }

    public void setEstudios(String estudios) {
        this.estudios = estudios;
    }

    public String getIne() {
        return ine;
    }

    public void setIne(String ine) {
        this.ine = ine;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getAvance() {
        return avance;
    }

    public void setAvance(Integer avance) {
        this.avance = avance;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public String getDriveUrl() {
        return driveUrl;
    }

    public void setDriveUrl(String driveUrl) {
        this.driveUrl = driveUrl;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
