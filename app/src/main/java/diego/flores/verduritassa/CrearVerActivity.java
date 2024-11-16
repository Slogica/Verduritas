package diego.flores.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrearVerActivity extends AppCompatActivity {

    private EditText alias, fecha;
    private Spinner spinnerCultivo;
    private Button crear;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ver);

        alias = findViewById(R.id.alias);
        fecha = findViewById(R.id.fecha);
        spinnerCultivo = findViewById(R.id.spinnerCultivo);
        crear = findViewById(R.id.crear);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cultivos_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCultivo.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        crear.setOnClickListener(view -> guardar());

        Button volver = findViewById(R.id.volver);
        volver.setOnClickListener(v -> {
            Intent intent = new Intent(CrearVerActivity.this, ListaActivity.class);
            startActivity(intent);
        });
    }

    private String calcularFechaCosecha(String fechaSiembra, String cultivo){
        try {
            Date fecha = dateFormat.parse(fechaSiembra);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);

            switch (cultivo.toLowerCase()) {
                case "tomates":
                calendar.add(Calendar.DAY_OF_MONTH, 80);
                    break;
                case "cebollas":
                    calendar.add(Calendar.DAY_OF_MONTH, 120);
                    break;
                case "lechugas":
                    calendar.add(Calendar.DAY_OF_MONTH, 85);
                    break;
                case "apio":
                    calendar.add(Calendar.DAY_OF_MONTH, 150);
                    break;
                case "choclo":
                    calendar.add(Calendar.DAY_OF_MONTH, 90);
                    break;
            }

            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void guardar() {
        String aliasText = alias.getText().toString().trim();
        String fechaText = fecha.getText().toString().trim();
        String cultivoSeleccionado = spinnerCultivo.getSelectedItem().toString().trim();

        if (aliasText.isEmpty() || fechaText.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaCosecha = calcularFechaCosecha(fechaText, cultivoSeleccionado);
        if (fechaCosecha.isEmpty()) {
            Toast.makeText(this, "Error en el formato de fecha", Toast.LENGTH_SHORT).show();
        }

        Verduras verdura = new Verduras(aliasText, fechaText, fechaCosecha, cultivoSeleccionado);

        Map<String, Object> verduras = new HashMap<>();
        verduras.put("alias", verdura.getAlias());
        verduras.put("fecha", verdura.getFecha());
        verduras.put("calcFecha", verdura.getCalcFecha());
        verduras.put("planta", verdura.getPlanta());

        db.collection("Verduras")
                .add(verduras)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    documentReference.update("id", documentId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Verdura guardada correctamente con ID: " + documentId, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CrearVerActivity.this, ListaActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Evitar que el usuario regrese a CrearVerActivity
                                startActivity(intent);

                                // Reiniciar los campos
                                alias.setText("");
                                fecha.setText("");
                                spinnerCultivo.setSelection(0);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar el ID en el documento", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar la ficha", Toast.LENGTH_SHORT).show();
                });


    }
}