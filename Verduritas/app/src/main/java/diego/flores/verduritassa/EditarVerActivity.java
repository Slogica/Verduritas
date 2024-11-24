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

public class EditarVerActivity extends AppCompatActivity {

    private EditText alias, fecha;
    private Spinner spinnerCultivo;
    private Button editar;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat;
    private String cultivoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ver);

        alias = findViewById(R.id.alias);
        fecha = findViewById(R.id.fecha);
        spinnerCultivo = findViewById(R.id.spinnerCultivo);
        editar = findViewById(R.id.editar);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipos_de_cultivos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCultivo.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Recuperar el ID del cultivo desde el Intent
        Intent intent = getIntent();
        cultivoId = intent.getStringExtra("id");

        // Cargar los datos del cultivo para editar
        loadCultivoData();

        // Configurar el botón de editar
        editar.setOnClickListener(view -> editarCultivo());

        Button volver = findViewById(R.id.volver);
        volver.setOnClickListener(v -> {
            Intent backIntent = new Intent(EditarVerActivity.this, ListaActivity.class);
            startActivity(backIntent);
        });
    }

    private void loadCultivoData() {
        db.collection("Verduras").document(cultivoId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Verduras verdura = documentSnapshot.toObject(Verduras.class);
                        if (verdura != null) {
                            alias.setText(verdura.getAlias());
                            fecha.setText(verdura.getFecha());
                            spinnerCultivo.setSelection(getSpinnerPosition(verdura.getPlanta()));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EditarVerActivity.this, "Error al cargar cultivo", Toast.LENGTH_SHORT).show());
    }

    private int getSpinnerPosition(String planta) {
        String[] cultivos = getResources().getStringArray(R.array.Tipos_de_cultivos);
        for (int i = 0; i < cultivos.length; i++) {
            if (cultivos[i].equalsIgnoreCase(planta)) {
                return i;
            }
        }
        return 0; // Default position if not found
    }

    private String calcularFechaCosecha(String fechaSiembra, String cultivo) {
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

    private void editarCultivo() {
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

        Verduras verdura = new Verduras(cultivoId, aliasText, fechaText, fechaCosecha, cultivoSeleccionado);

        Map<String, Object> verduras = new HashMap<>();
        verduras.put("alias", verdura.getAlias());
        verduras.put("fecha", verdura.getFecha());
        verduras.put("calcFecha", verdura.getCalcFecha());
        verduras.put("planta", verdura.getPlanta());

        db.collection("Verduras").document(cultivoId)
                .update(verduras)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cultivo actualizado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditarVerActivity.this, ListaActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar cultivo", Toast.LENGTH_SHORT).show());
    }
}
