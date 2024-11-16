package diego.flores.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText email, nombre, pais, genero;
    EditText pass;
    TextView mensaje;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        nombre = findViewById(R.id.nombre);
        pais = findViewById(R.id.pais);
        genero = findViewById(R.id.genero);
        pass = findViewById(R.id.pass);

        mensaje = findViewById(R.id.mensaje);

        db = FirebaseFirestore.getInstance();

        Button registrar = findViewById(R.id.registrar);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validarDatos()){
                    registrarse();

                }

            }
        });

        Button logear = findViewById(R.id.logear);
        logear.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void guardarFicha() {
        String correo = email.getText().toString().trim();
        String nombreU = nombre.getText().toString().trim();
        String paisU = pais.getText().toString().trim();
        String generoU = genero.getText().toString().trim();

        if (correo.isEmpty() || nombreU.isEmpty() || paisU.isEmpty() || generoU.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("correo", correo);
        usuario.put("nombre", nombreU);
        usuario.put("pais", paisU);
        usuario.put("genero", generoU);

        db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ficha guardada correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar la ficha", Toast.LENGTH_SHORT).show();
                });
    }

    private void registrarse(){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    guardarFicha();
                } else {

                    mensaje.setText("No se registró correctamente");
                }
            }
        });
    }

    private boolean validarDatos(){

        if(email.getText().toString().isEmpty() || nombre.getText().toString().isEmpty() || pais.getText().toString().isEmpty() || genero.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
            return false;
        }else{
            return true;
        }

    }


}