package diego.flores.verduritassa;

public class Usuario {
    private String correo;
    private String nombre;
    private String pais;
    private String genero;

    public Usuario() {}

    public Usuario(String correo, String nombre, String pais, String genero) {
        this.correo = correo;
        this.nombre = nombre;
        this.pais = pais;
        this.genero = genero;
    }

    public String getCorreo() { return correo; }
    public String getNombre() { return nombre; }
    public String getPais() { return pais; }
    public String getGenero() { return genero; }
}

