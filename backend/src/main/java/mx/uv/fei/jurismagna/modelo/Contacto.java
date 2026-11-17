package mx.uv.fei.jurismagna.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Contacto agregado a mano por el admin o por un abogado.
// propietarioId es el id del usuario dueño del contacto.
@Entity
@Table(name = "contacto", indexes = @Index(name = "idx_contacto_propietario", columnList = "propietario_tipo, propietario_id"))
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ADMIN o ABOGADO
    @NotBlank(message = "Falta el propietarioTipo")
    @Column(nullable = false, length = 10)
    private String propietarioTipo;

    @NotNull(message = "Falta el propietarioId")
    @Column(nullable = false)
    private Integer propietarioId;

    @NotBlank(message = "Falta el nombre")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String notas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPropietarioTipo() {
        return propietarioTipo;
    }

    public void setPropietarioTipo(String propietarioTipo) {
        this.propietarioTipo = propietarioTipo;
    }

    public Integer getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Integer propietarioId) {
        this.propietarioId = propietarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
