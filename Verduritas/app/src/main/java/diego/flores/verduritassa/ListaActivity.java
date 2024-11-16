package diego.flores.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CultivoAdapter cultivoAdapter;
    private List<Verduras> cultivosList;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cultivosList = new ArrayList<>();

        // Inicializar vistas
        welcomeMessage = findViewById(R.id.welcomeMessage);
        RecyclerView recyclerView = findViewById(R.id.cultivosRecyclerView);
        FloatingActionButton addButton = findViewById(R.id.addCultivoButton);

        // Verificar que recyclerView no sea nulo
        if (recyclerView == null) {
            Toast.makeText(this, "Error: RecyclerView no encontrado", Toast.LENGTH_LONG).show();
            return;
        }

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cultivoAdapter = new CultivoAdapter(cultivosList, this::showPopupMenu);
        recyclerView.setAdapter(cultivoAdapter);

        // Configurar botones
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListaActivity.this, CrearVerActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> logout());

        // Mostrar mensaje de bienvenida
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            welcomeMessage.setText("Bienvenido, " + currentUser.getEmail());
        }



        // Cargar cultivos
        loadCultivos();
    }

    private void loadCultivos() {
        db.collection("Verduras")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cultivosList.clear();  // Limpia la lista para evitar duplicados
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Verduras verdura = document.toObject(Verduras.class);
                        verdura.setId(document.getId());  // Almacenar el ID del documento
                        cultivosList.add(verdura);  // Agrega cada usuario a la lista
                    }
                    cultivoAdapter.notifyDataSetChanged();  // Refresca el adaptador con los nuevos datos
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show());
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.cultivo_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            Verduras verdura = cultivosList.get(position);
            if (item.getItemId() == R.id.menu_edit) {
                // Implementar edición
                Intent intent = new Intent(ListaActivity.this, EditarVerActivity.class);
                intent.putExtra("id", verdura.getId());
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                deleteCultivo(verdura.getId());  // Usa el ID correcto para la eliminación
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void deleteCultivo(String cultivoId) {
        db.collection("Verduras").document(cultivoId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cultivo eliminado", Toast.LENGTH_SHORT).show();
                    loadCultivos();  // Refresca la lista después de eliminar
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar cultivo", Toast.LENGTH_SHORT).show());
    }



    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(ListaActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}