package diego.flores.verduritassa;

public class Verduras {
    private String id;
    private String alias;
    private String fecha;
    private String calcFecha;
    private String planta;
    private String userId;

    public Verduras() {}

    public Verduras(String alias, String fecha, String calcFecha, String planta, String userId) {
        this.alias = alias;
        this.fecha = fecha;
        this.calcFecha = calcFecha;
        this.planta = planta;
        this.userId = userId;
    }

    // Constructor con ID para cuando recuperamos de Firestore
    public Verduras(String id, String alias, String fecha, String calcFecha, String planta, String userId) {
        this.id = id;
        this.alias = alias;
        this.fecha = fecha;
        this.calcFecha = calcFecha;
        this.planta = planta;
        this.userId = userId;
    }

    public String getId() { return id; }
    public String getAlias() { return alias; }
    public String getFecha() { return fecha; }
    public String getCalcFecha() { return calcFecha; }
    public String getPlanta() { return planta; }
    public String getUserId() { return userId; }

    public void setId(String id) { this.id = id; }
    public void setAlias(String alias) { this.alias = alias; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setCalcFecha(String calcFecha) { this.calcFecha = calcFecha; }
    public void setPlanta(String planta) { this.planta = planta; }
    public void setUserId(String userId) { this.userId = userId; }
}
