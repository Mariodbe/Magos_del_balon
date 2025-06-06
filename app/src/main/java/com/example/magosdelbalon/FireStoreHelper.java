package com.example.magosdelbalon;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.magosdelbalon.mensaje.Mensaje;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.functions.FirebaseFunctions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

public class FireStoreHelper {
    private static final String TAG="FireStoreHelper";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static FireStoreHelper instance;

    public FireStoreHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    public static synchronized FireStoreHelper getInstance() {
        if (instance == null) {
            instance = new FireStoreHelper();
        }
        return instance;
    }
    public void registerUser(String username, String email, String password, Context context) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("email", email);
                            userMap.put("siguiendo", new ArrayList<String>());
                            userMap.put("seguidores", new ArrayList<String>());

                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        saveFCMToken(userId, context);
                                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void authenticateUser(String emailOrUsername, String password, Context context, final AuthCallback callback) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                saveFCMToken(userId, context);
                                FirebaseFirestore.getInstance().collection("users").document(userId)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String username = documentSnapshot.getString("username");
                                                callback.onSuccess(username);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Error al obtener datos del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(context, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            FirebaseFirestore.getInstance().collection("users").whereEqualTo("username", emailOrUsername)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String userId = document.getId();
                            String email = document.getString("email");
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            saveFCMToken(userId, context);
                                            callback.onSuccess(emailOrUsername);
                                        } else {
                                            Toast.makeText(context, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error al autenticar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void signInWithGoogle(GoogleSignInAccount account, Context context, final AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String username = account.getDisplayName();
                    String email = account.getEmail();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            saveFCMToken(userId, context);
                            callback.onSuccess(username);
                        } else {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("email", email);
                            userMap.put("siguiendo", new ArrayList<String>());
                            userMap.put("seguidores", new ArrayList<String>());

                            db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        saveFCMToken(userId, context);
                                        callback.onSuccess(username);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }).addOnFailureListener(e ->
                            Toast.makeText(context, "Error al verificar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(context, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFCMToken(String userId, Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(tokenTask -> {
                    if (tokenTask.isSuccessful()) {
                        String token = tokenTask.getResult();
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                                .update("fcmToken", token)
                                .addOnSuccessListener(aVoid -> Log.d("FCM Token", "Token guardado exitosamente"))
                                .addOnFailureListener(e -> Log.e("FCM Token", "Error al guardar token", e));
                    } else {
                        Log.e("FCM Token", "Error al obtener token", tokenTask.getException());
                    }
                });
    }




    public interface AuthCallback {
        void onSuccess(String username);
    }


    public void createLigaInFirestore(int ligaId, String ligaName, String equipoName, String tipoLiga, FireStoreCallback callback) {
        String ligaCollection = "ligas";
        String usersCollection = "users";
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Crear un ID válido para Firestore basado en el nombre de la liga
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        // Crear la referencia a la liga en la colección "ligas"
        DocumentReference ligaRef = db.collection(ligaCollection).document(ligaIdHash);

        ligaRef.set(new Liga(ligaName, userId, new ArrayList<>(Collections.singletonList(equipoName)), tipoLiga))
                .addOnSuccessListener(aVoid -> {
                    // Obtener la referencia del usuario
                    DocumentReference userRef = db.collection(usersCollection).document(userId);

                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        Map<String, Object> userData = (documentSnapshot.exists()) ? documentSnapshot.getData() : new HashMap<>();

                        // Crear/Actualizar la estructura deseada dentro de users
                        Map<String, Object> ligaData = new HashMap<>();
                        ligaData.put("equipo", equipoName);
                        ligaData.put("tipoLiga",tipoLiga);
                        //Añadimos dinero inicial para el equipo cuando se crea la liga.
                        ligaData.put("dinero", 10000000);
                        // Crear el mapa de estadios
                        Map<String, Integer> estadio = new HashMap<>();
                        estadio.put("nivel_estadio", 0);
                        estadio.put("nivel_ciudad_deportiva", 0);
                        estadio.put("nivel_centro_medico", 0);
                        ligaData.put("estadio", estadio);

                        Map<String, Integer> tacticas = new HashMap<>();
                        tacticas.put("Agresividad", 3);
                        tacticas.put("Contraataques", 3);
                        tacticas.put("Posesión", 3);
                        tacticas.put("Presión", 3);
                        ligaData.put("tacticas", tacticas);

                        // Lista dinámica de equipos según la liga
                        List<String> todosLosEquipos;
                        if (tipoLiga.equalsIgnoreCase("La Liga")) {
                            todosLosEquipos = Arrays.asList("Atlético de Madrid", "Barcelona", "Real Madrid","Athletic Club");
                        } else {
                            todosLosEquipos = Arrays.asList("Manchester City", "Liverpool", "Chelsea", "Arsenal");
                        }

                        // Crear lista de rivales pendientes quitando el equipo elegido (comparando *directamente*)
                        HashMap<String, Integer> pendientesJugar = new HashMap<>();
                        for (String equipo : todosLosEquipos) {
                            if (!equipo.equalsIgnoreCase(equipoName.trim())) {
                                pendientesJugar.put(equipo,2);
                            }
                        }



                        // Crear el mapa de progreso de liga
                        Map<String, Object> progresoLiga = new HashMap<>();
                        progresoLiga.put("rivalesJugados", new ArrayList<String>());
                        progresoLiga.put("pendientesJugar", pendientesJugar);
                        progresoLiga.put("MIpartidosGanados", 0);
                        progresoLiga.put("MIpartidosEmpatados", 0);
                        progresoLiga.put("MIpartidosPerdidos", 0);
                        progresoLiga.put("MIgolesAFavor", 0);
                        progresoLiga.put("MIgolesEnContra", 0);

                        // Crear lista de clasificación inicial
                        List<Map<String, Object>> clasificacion = new ArrayList<>();
                        for (String equipo : pendientesJugar.keySet()) {
                            Map<String, Object> equipoStats = new HashMap<>();
                            equipoStats.put("equipo", equipo);
                            equipoStats.put("partidosGanados", 0);
                            equipoStats.put("partidosEmpatados", 0);
                            equipoStats.put("partidosPerdidos", 0);
                            equipoStats.put("golesAFavor", 0);
                            equipoStats.put("golesEnContra", 0);
                            clasificacion.add(equipoStats);
                        }

                        // Añadir la clasificación dentro del progresoLiga
                        progresoLiga.put("clasificacion", clasificacion);

                        // Añadir al mapa principal
                        ligaData.put("progresoLiga", progresoLiga);


                        // Aquí cambiamos la función para obtener jugadores dependiendo del equipo seleccionado
                        fetchPlayersForTeam(equipoName, new FireStoreHelper.PlayersCallback() {
                            @Override
                            public void onPlayersLoaded(List<Jugador> players) {
                                // Asegurarse de que players no sea nulo
                                if (players != null) {
                                    // Convertir la lista de jugadores a una lista de mapas para almacenarla en Firestore
                                    List<Map<String, Object>> playersMapList = new ArrayList<Map<String, Object>>();
                                    for (Jugador jugador : players) {
                                        Map<String, Object> playerMap = new HashMap<String, Object>();
                                        playerMap.put("nombre", jugador.getNombre());
                                        playerMap.put("posicion", jugador.getPosicion());
                                        playerMap.put("overall", jugador.getOverall());
                                        playerMap.put("precio", jugador.getPrecio());
                                        playerMap.put("url",jugador.getImageUrl());
                                        playersMapList.add(playerMap);
                                    }

                                    ligaData.put("jugadores", playersMapList);

                                    userData.put(ligaIdHash, ligaData);

                                    userRef.set(userData, SetOptions.merge())
                                            .addOnSuccessListener(unused -> callback.onSuccess("Liga creada y equipo guardado correctamente"))
                                            .addOnFailureListener(e -> callback.onFailure("Liga creada, pero error al actualizar usuario: " + e.getMessage()));
                                } else {
                                    callback.onFailure("Error al obtener los jugadores.");
                                }


                                // Al final del bloque fetchPlayersForTeam, dentro del onPlayersLoaded:
                                fetchMercadoPlayers(tipoLiga, new FireStoreHelper.PlayersCallback() {
                                    @Override
                                    public void onPlayersLoaded(List<Jugador> mercadoPlayers) {
                                        // Convertir a lista de mapas
                                        List<Map<String, Object>> mercadoMapList = new ArrayList<>();
                                        for (Jugador jugador : mercadoPlayers) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("nombre", jugador.getNombre());
                                            map.put("posicion", jugador.getPosicion());
                                            map.put("overall", jugador.getOverall());
                                            map.put("precio",jugador.getPrecio());
                                            map.put("url",jugador.getImageUrl());
                                            mercadoMapList.add(map);
                                        }

                                        // Guardar en el documento de la liga
                                        db.collection("ligas").document(ligaIdHash)
                                                .update("Mercado", mercadoMapList)
                                                .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Mercado creado correctamente"))
                                                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar el mercado: " + e.getMessage()));
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Log.e("Mercado", "Error: " + errorMessage);
                                    }
                                });


                            }

                            @Override
                            public void onError(String errorMessage) {
                                callback.onFailure("Error al obtener los jugadores: " + errorMessage);
                            }
                        });




                    }).addOnFailureListener(e -> callback.onFailure("Error al obtener usuario: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure("Error al crear la liga: " + e.getMessage()));
    }

    private void fetchPlayersForTeam(String equipoName, FireStoreHelper.PlayersCallback callback) {
        switch (equipoName) {
            case "Barcelona":
                fetchBarcelonaPlayers(callback);
                break;
            case "Real Madrid":
                fetchRealMadridPlayers(callback);
                break;
            case "Atlético de Madrid":
                fetchAtleticoPlayers(callback);
                break;
            case "Athletic Club":
                fetchAthleticClubPlayers(callback);
                break;
            case "Manchester City":
                fetchManCityPlayers(callback);
                break;
            case "Liverpool":
                fetchLiverpoolPlayers(callback);
                break;
            case "Chelsea":
                fetchChelseaPlayers(callback);
                break;
            case "Arsenal":
                fetchArsenalPlayers(callback);
                break;
            default:
                callback.onError("Equipo no reconocido.");
                break;
        }
    }

    public void fetchMercadoPlayers(String tipoLiga, PlayersCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userData = documentSnapshot.getData();
            List<String> nombresJugadoresEquipo = new ArrayList<>();

            if (userData != null && userData.containsKey(tipoLiga)) {
                Map<String, Object> ligaData = (Map<String, Object>) userData.get(tipoLiga);
                List<Map<String, Object>> jugadoresEquipo = (List<Map<String, Object>>) ligaData.get("jugadores");
                if (jugadoresEquipo != null) {
                    for (Map<String, Object> jugador : jugadoresEquipo) {
                        nombresJugadoresEquipo.add((String) jugador.get("nombre"));
                    }
                }
            }

            // Ya que tenemos los jugadores del equipo, obtenemos el mercado
            FirebaseFunctions functions = FirebaseFunctions.getInstance();

            functions.getHttpsCallable("getMercadoPlayers")
                    .call(Collections.singletonMap("tipoLiga", tipoLiga))
                    .addOnSuccessListener(result -> {
                        if (result.getData() instanceof Map) {
                            Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                            if (responseMap.containsKey("players")) {
                                List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    String nombre = (String) playerData.get("name");

                                    if (!nombresJugadoresEquipo.contains(nombre)) {
                                        Jugador jugador = new Jugador(
                                                nombre,
                                                (String) playerData.get("position"),
                                                ((Number) playerData.get("overall")).intValue(),
                                                ((Number) playerData.get("precio")).intValue(),
                                                (String) playerData.get("url")
                                        );
                                        players.add(jugador);
                                    }
                                }

                                callback.onPlayersLoaded(players);
                            } else {
                                callback.onError("El campo 'players' no existe en la respuesta del mercado.");
                            }
                        } else {
                            callback.onError("La respuesta del mercado no es válida.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onError("Error al obtener jugadores del mercado: " + e.getMessage());
                    });

        }).addOnFailureListener(e -> {
            callback.onError("Error al obtener datos del usuario: " + e.getMessage());
        });
    }



    private void fetchBarcelonaPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getBarcelonaPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")
                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchRealMadridPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getMadridPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")

                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchAtleticoPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getAtleticoPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")
                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchAthleticClubPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getAthleticClubPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")
                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchManCityPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getManCityPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")

                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchLiverpoolPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getLiverpoolPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")
                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchChelseaPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getChelseaPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")
                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    private void fetchArsenalPlayers(FireStoreHelper.PlayersCallback callback) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable("getArsenalPlayers")
                .call()
                .addOnSuccessListener(result -> {
                    // Log de la respuesta completa
                    Log.d(TAG, "Response: " + result.getData());

                    // Verificar si la respuesta es un Map
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();

                        // Verificar si el campo 'players' existe en la respuesta
                        if (responseMap.containsKey("players")) {
                            List<Map<String, Object>> playersData = (List<Map<String, Object>>) responseMap.get("players");

                            if (playersData != null) {
                                List<Jugador> players = new ArrayList<>();

                                for (Map<String, Object> playerData : playersData) {
                                    Jugador jugador = new Jugador(
                                            (String) playerData.get("name"),
                                            (String) playerData.get("position"),
                                            ((Number) playerData.get("overall")).intValue(),
                                            ((Number) playerData.get("precio")).intValue(),
                                            (String) playerData.get("url")
                                    );
                                    players.add(jugador);
                                }

                                // Llamar al callback con players
                                if (!players.isEmpty()) {
                                    callback.onPlayersLoaded(players);
                                } else {
                                    callback.onError("No se encontraron jugadores.");
                                }
                            } else {
                                callback.onError("La respuesta no contiene datos de jugadores.");
                            }
                        } else {
                            callback.onError("El campo 'players' no existe en la respuesta.");
                        }
                    } else {
                        callback.onError("La respuesta no es un objeto JSON válido.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                    Log.e(TAG, "Error al obtener jugadores: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }



    public interface PlayersCallback {
        void onPlayersLoaded(List<Jugador> players);
        void onError(String errorMessage);
    }


    public interface FireStoreCallback {
        void onSuccess(String message);
        void onFailure(String message);
    }
    public void checkUserHasLiga(int ligaSlot, FireStoreCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        String ligaKey = "liga_" + ligaSlot;
                        if (documentSnapshot.getData().containsKey(ligaKey)) {
                            callback.onFailure("Ya tienes una liga en este espacio.");
                            return;
                        }
                        callback.onSuccess("Puedes crear una liga.");
                    } else {
                        callback.onSuccess("Puedes crear una liga.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Error al consultar ligas: " + e.getMessage()));
    }
    public void getUserLigas(FireStoreHelper.LigasCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        // Obtener el documento del usuario en la colección "users"
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        List<Liga> ligas = new ArrayList<>();

                        if (userData != null) {
                            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                                // Cada clave del documento representa una liga
                                String ligaId = entry.getKey();
                                Object ligaValue = entry.getValue();

                                // Verificar si el valor es un Map antes de hacer el cast
                                if (ligaValue instanceof Map) {
                                    Map<String, Object> ligaData = (Map<String, Object>) ligaValue;

                                    if (ligaData.containsKey("equipo")) {
                                        String equipo = (String) ligaData.get("equipo");

                                        // Obtener el documento de la liga en la colección "ligas"
                                        db.collection("ligas").document(ligaId).get()
                                                .addOnSuccessListener(ligaSnapshot -> {
                                                    if (ligaSnapshot.exists()) {
                                                        Map<String, Object> ligaInfo = ligaSnapshot.getData();
                                                        if (ligaInfo != null) {
                                                            String nombreLiga = (String) ligaInfo.get("nombre");
                                                            String tipoLiga = (String) ligaInfo.get("tipoLiga");

                                                            // Crear un objeto Liga y agregarlo a la lista
                                                            ArrayList<String> equipos = new ArrayList<>();
                                                            equipos.add(equipo);  // Solo un equipo por usuario en la liga

                                                            ligas.add(new Liga(nombreLiga, uid, equipos, tipoLiga));
                                                        }
                                                    }

                                                    // Pasar las ligas cargadas al callback
                                                    callback.onLigasLoaded(ligas);
                                                })
                                                .addOnFailureListener(e -> {
                                                    // En caso de error, pasar el mensaje al callback
                                                    Log.e("Firestore", "Error al cargar la liga: " + e.getMessage());
                                                    callback.onError(e.getMessage());
                                                });
                                    }
                                } else {
                                    // Si no es un Map, loguear el tipo inesperado o manejarlo
                                    Log.w("Firestore", "Valor inesperado para la liga " + ligaId + ": " + ligaValue.getClass().getName());
                                }
                            }
                        }
                    } else {
                        // No hay datos en el usuario
                        Log.d("Firestore", "No hay ligas para este usuario.");
                        callback.onLigasLoaded(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    // En caso de error, pasar el mensaje al callback
                    Log.e("Firestore", "Error al cargar las ligas: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }



    public interface LigasCallback {
        void onLigasLoaded(List<Liga> ligas); // Se ejecuta cuando las ligas se cargan correctamente
        void onError(String errorMessage);    // Se ejecuta si ocurre un error
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

    public void uploadProfileImage(Uri imageUri, UploadCallback callback) {
        if (imageUri == null) {
            callback.onFailure("No se ha seleccionado ninguna imagen");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        String userId = currentUser.getUid();
        StorageReference profileImageRef = storage.getReference()
                .child("profile_images")
                .child(userId + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de la imagen
                    profileImageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();

                                // Actualizar el documento del usuario en Firestore
                                db.collection("users").document(userId)
                                        .update("profileImageUrl", imageUrl)
                                        .addOnSuccessListener(aVoid -> callback.onSuccess(imageUrl))
                                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                            })
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
    public void obtenerDatosLigaPorId(String ligaName, FirestoreCallback1 callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError("Usuario no autenticado.");
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Buscar en la colección "users" y obtener los datos del usuario
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        if (userData != null) {
                            // Verificar si la liga está en la lista de ligas del usuario
                            if (userData.containsKey(ligaName)) {
                                Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaName);
                                callback.onSuccess(ligaData);
                            } else {
                                callback.onError("No se encontró la liga: " + ligaName);
                            }
                        }
                    } else {
                        callback.onError("El documento del usuario no existe.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error al obtener datos de la liga: " + e.getMessage()));
    }


    // Interfaz callback
    public interface FirestoreCallback1 {
        void onSuccess(Map<String, Object> ligaData);
        void onError(String errorMessage);
    }



    public void getProfileImage(ImageView imageView) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "Usuario no autenticado");
            return;
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "Buscando imagen de perfil para usuario: " + userId);

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("profileImageUrl");
                        Log.d(TAG, "URL de imagen encontrada: " + imageUrl);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(imageUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .error(R.drawable.ic_profile_placeholder)
                                    .into(imageView);
                        } else {
                            Log.d(TAG, "No hay URL de imagen en el documento");
                            // Establecer la imagen por defecto si no hay URL
                            imageView.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                    } else {
                        Log.d(TAG, "Documento de usuario no encontrado");
                        imageView.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener la imagen de perfil: " + e.getMessage());
                    imageView.setImageResource(R.drawable.ic_profile_placeholder);
                });
    }
    public void getProfileImageForUser(String userId, ImageView imageView) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("profileImageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(imageUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .error(R.drawable.ic_profile_placeholder)
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                })
                .addOnFailureListener(e -> {
                    imageView.setImageResource(R.drawable.ic_profile_placeholder);
                });
    }

    public void upgradeEstadio(String userId, String ligaId, String nivelKey, final FireStoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> userData = documentSnapshot.getData();
                if (userData != null && userData.containsKey(ligaId)) {
                    Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaId);
                    if (ligaData != null) {
                        long currentMoney = ligaData.containsKey("dinero") ? (long) ligaData.get("dinero") : 0;
                        Map<String, Object> estadio = (Map<String, Object>) ligaData.get("estadio");

                        int currentLevel;
                        if (estadio != null && estadio.containsKey(nivelKey)) {
                            currentLevel = ((Long) estadio.get(nivelKey)).intValue();
                        } else {
                            currentLevel = 0;
                        }

                        if (currentLevel >= 10) {
                            callback.onFailure("Este ítem ya está al nivel máximo.");
                            return;
                        }

                        long upgradeCost = 500000 + (currentLevel * 100000);

                        if (currentMoney >= upgradeCost) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put(ligaId + ".dinero", currentMoney - upgradeCost);
                            updates.put(ligaId + ".estadio." + nivelKey, currentLevel + 1);

                            userRef.update(updates)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess("Mejora realizada con éxito. Nuevo nivel: " + (currentLevel + 1)))
                                    .addOnFailureListener(e -> callback.onFailure("Error al realizar la mejora: " + e.getMessage()));
                        } else {
                            callback.onFailure("No tienes suficiente dinero. Necesitas " + upgradeCost + " €.");
                        }
                    } else {
                        callback.onFailure("Datos de la liga no encontrados.");
                    }
                } else {
                    callback.onFailure("Liga no encontrada para este usuario.");
                }
            } else {
                callback.onFailure("Datos del usuario no encontrados.");
            }
        }).addOnFailureListener(e -> callback.onFailure("Error al leer datos: " + e.getMessage()));
    }

    public void obtenerNivelEstadio(String userId, String ligaId, String nivelKey, final NivelCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains(ligaId)) {
                Map<String, Object> ligaData = (Map<String, Object>) documentSnapshot.get(ligaId);
                Map<String, Object> estadio = (Map<String, Object>) ligaData.get("estadio");

                int nivelActual = 0;
                if (estadio != null && estadio.containsKey(nivelKey)) {
                    nivelActual = ((Long) estadio.get(nivelKey)).intValue();
                }

                callback.onSuccess(nivelActual);
            } else {
                callback.onFailure("No se encontró la liga o los datos del estadio.");
            }
        }).addOnFailureListener(e -> callback.onFailure("Error al leer nivel: " + e.getMessage()));
    }

    // Interfaz del callback
    public interface NivelCallback {
        void onSuccess(int nivelActual);
        void onFailure(String errorMessage);
    }


    public void obtenerJugadoresPorLiga(String ligaName, JugadoresCallback callback) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data != null && data.containsKey(ligaName)) {
                            Map<String, Object> ligaData = (Map<String, Object>) data.get(ligaName);
                            List<Map<String, Object>> jugadoresList = (List<Map<String, Object>>) ligaData.get("jugadores");

                            List<Jugador> jugadores = new ArrayList<>();
                            if (jugadoresList != null) {
                                for (Map<String, Object> jugadorMap : jugadoresList) {
                                    Jugador jugador = new Jugador(
                                            (String) jugadorMap.get("nombre"),
                                            (String) jugadorMap.get("posicion"),
                                            ((Long) jugadorMap.get("overall")).intValue(),
                                            ((Long) jugadorMap.get("precio")).intValue(),
                                            (String) jugadorMap.get("url")

                                    );
                                    jugadores.add(jugador);
                                }
                            }
                            callback.onJugadoresCargados(jugadores);
                        } else {
                            callback.onJugadoresCargados(new ArrayList<>());
                        }
                    } else {
                        callback.onJugadoresCargados(new ArrayList<>());
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void actualizarJugador(String ligaName, Jugador jugador, SimpleCallback callback) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Map<String, Object> userData = document.getData();
                        Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaName);
                        List<Map<String, Object>> jugadoresList = (List<Map<String, Object>>) ligaData.get("jugadores");

                        for (Map<String, Object> jugadorMap : jugadoresList) {
                            if (jugadorMap.get("nombre").equals(jugador.getNombre())) {
                                jugadorMap.put("overall", jugador.getOverall());
                                break;
                            }
                        }

                        ligaData.put("jugadores", jugadoresList);

                        db.collection("users").document(userId).update(ligaName, ligaData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
    public interface JugadoresCallback {
        void onJugadoresCargados(List<Jugador> jugadores);
        void onError(Exception e);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void deleteLiga(String ligaId, final FireStoreCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);
        CollectionReference ligasRef = db.collection("ligas");
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> userData = documentSnapshot.getData();
                if (userData != null && userData.containsKey(ligaId)) {
                    // Eliminar la liga del usuario
                    userRef.update(ligaId, com.google.firebase.firestore.FieldValue.delete())
                            .addOnSuccessListener(aVoid -> {
                                // Eliminar la liga de la colección "ligas"
                                ligasRef.document(ligaId).delete()
                                        .addOnSuccessListener(aVoid2 -> callback.onSuccess("Liga eliminada correctamente"))
                                        .addOnFailureListener(e -> callback.onFailure("Error al eliminar la liga de 'ligas': " + e.getMessage()));
                            })
                            .addOnFailureListener(e -> callback.onFailure("Error al eliminar la liga del usuario: " + e.getMessage()));
                } else {
                    callback.onFailure("Liga no encontrada para este usuario");
                }
            } else {
                callback.onFailure("Datos del usuario no encontrados");
            }
        }).addOnFailureListener(e -> callback.onFailure("Error al leer datos: " + e.getMessage()));
    }

    public void saveLineup(String userId, String ligaName, String posicionClave, String nombreJugador) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Mapa para alineacion
        Map<String, Object> alineacionMap = new HashMap<>();
        alineacionMap.put(posicionClave, nombreJugador); // ej: "def1" -> "Carvajal"

        // Mapa que contiene "alineacion" como subdocumento de la liga
        Map<String, Object> ligaMap = new HashMap<>();
        ligaMap.put("alineacion", alineacionMap);

        // Mapa final que contiene la liga (ej: "yh") como clave
        Map<String, Object> update = new HashMap<>();
        update.put(ligaIdHash, ligaMap);

        userDocRef.set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Jugador alineado correctamente"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar alineación: " + e.getMessage()));
    }
    public void cargarAlineacion(String userId, String ligaName, Map<String, TextView> textViews) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> ligas = (Map<String, Object>) documentSnapshot.get(ligaIdHash);
                        if (ligas != null && ligas.containsKey("alineacion")) {
                            Map<String, Object> alineacion = (Map<String, Object>) ligas.get("alineacion");
                            for (String posicion : textViews.keySet()) {
                                String jugador = (String) alineacion.get(posicion);
                                textViews.get(posicion).setText(jugador != null ? jugador : "");
                            }
                        }
                    }
                });
    }
    public void cargarAlineacion(String userId, String ligaName, Map<String, TextView> textViews, Map<String, String> selectedPlayers) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> ligas = (Map<String, Object>) documentSnapshot.get(ligaIdHash);
                        if (ligas != null && ligas.containsKey("alineacion")) {
                            Map<String, Object> alineacion = (Map<String, Object>) ligas.get("alineacion");
                            for (String posicion : textViews.keySet()) {
                                String jugador = (String) alineacion.get(posicion);
                                if (jugador != null && !jugador.trim().isEmpty()) {
                                    textViews.get(posicion).setText(jugador);
                                    selectedPlayers.put(posicion, jugador);
                                }
                            }
                        }
                    }
                });
    }

    public void verificarAlineacionCompleta(String userId, String ligaName, AlineacionCallback callback) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> ligas = (Map<String, Object>) documentSnapshot.get(ligaIdHash);
                        if (ligas != null && ligas.containsKey("alineacion")) {
                            Map<String, Object> alineacion = (Map<String, Object>) ligas.get("alineacion");

                            int jugadoresAsignados = 0;
                            for (Object value : alineacion.values()) {
                                if (value != null && !value.toString().isEmpty()) {
                                    jugadoresAsignados++;
                                }
                            }

                            callback.onCheckCompleted(jugadoresAsignados >= 11);
                        } else {
                            callback.onCheckCompleted(false);
                        }
                    } else {
                        callback.onCheckCompleted(false);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error al verificar alineación: " + e.getMessage());
                });
    }

    public interface AlineacionCallback {
        void onCheckCompleted(boolean alineacionCompleta);
        void onError(String error);
    }

    public void comprarJugador(String ligaId, Map<String, Object> jugador, final FireStoreCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            try {
                Map<String, Object> userData = documentSnapshot.getData();
                if (userData != null && userData.containsKey(ligaId)) {
                    Object rawLigaData = userData.get(ligaId);
                    if (rawLigaData instanceof Map) {
                        Map<String, Object> ligaData = (Map<String, Object>) rawLigaData;
                        long dinero = ((Number) ligaData.get("dinero")).longValue();
                        long precio = ((Number) jugador.get("precio")).longValue();

                        List<Map<String, Object>> jugadores = (List<Map<String, Object>>) ligaData.get("jugadores");
                        if (jugadores == null) jugadores = new ArrayList<>();

                        for (Map<String, Object> j : jugadores) {
                            if (j.get("nombre").equals(jugador.get("nombre"))) {
                                callback.onFailure("Ya tienes a este jugador en tu equipo");
                                return;
                            }
                        }

                        if (dinero < precio) {
                            callback.onFailure("No tienes suficiente dinero. Precio: " + MainActivity.formatearDinero((int) precio));
                            return;
                        }

                        jugadores.add(jugador);
                        ligaData.put("jugadores", jugadores);
                        ligaData.put("dinero", dinero - precio);

                        userRef.update(ligaId, ligaData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess("Has comprado a " + jugador.get("nombre") + " por " + MainActivity.formatearDinero((int) precio)))
                                .addOnFailureListener(e -> callback.onFailure("Error al actualizar jugadores: " + e.getMessage()));
                    } else {
                        callback.onFailure("Formato de liga incorrecto");
                    }
                } else {
                    callback.onFailure("Liga no encontrada para este usuario");
                }
            } catch (Exception e) {
                callback.onFailure("Error procesando datos del usuario: " + e.getMessage());
            }
        });
    }
    public void venderJugador(String ligaId, Jugador jugador, final FireStoreCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            try {
                Map<String, Object> userData = documentSnapshot.getData();
                if (userData != null && userData.containsKey(ligaId)) {
                    Object rawLigaData = userData.get(ligaId);
                    if (rawLigaData instanceof Map) {
                        Map<String, Object> ligaData = (Map<String, Object>) rawLigaData;

                        long dinero = ((Number) ligaData.get("dinero")).longValue();

                        List<Map<String, Object>> jugadores = (List<Map<String, Object>>) ligaData.get("jugadores");
                        if (jugadores == null) {
                            callback.onFailure("No tienes jugadores para vender");
                            return;
                        }

                        boolean encontrado = false;
                        for (int i = 0; i < jugadores.size(); i++) {
                            Map<String, Object> j = jugadores.get(i);
                            if (j.get("nombre").equals(jugador.getNombre())) {
                                long precio = ((Number) j.get("precio")).longValue();
                                dinero += precio;
                                jugadores.remove(i);
                                encontrado = true;
                                break;
                            }
                        }

                        if (!encontrado) {
                            callback.onFailure("Jugador no encontrado en tu equipo");
                            return;
                        }

                        // Actualizar lista de jugadores y dinero
                        ligaData.put("jugadores", jugadores);
                        ligaData.put("dinero", dinero);

                        // Eliminar jugador de la alineación si está
                        List<String> posicionesAEliminar = new ArrayList<>();
                        if (ligaData.containsKey("alineacion")) {
                            Map<String, Object> alineacion = (Map<String, Object>) ligaData.get("alineacion");
                            boolean modificado = false;
                            for (Map.Entry<String, Object> entry : alineacion.entrySet()) {
                                if (jugador.getNombre().equals(entry.getValue())) {
                                    entry.setValue(null); // Borrar posición
                                    posicionesAEliminar.add(entry.getKey()); // Guardar clave para eliminar también en alineacionImg
                                    modificado = true;
                                }
                            }
                            if (modificado) {
                                ligaData.put("alineacion", alineacion);
                            }
                        }

                        // Eliminar también en alineacionImg usando las mismas posiciones
                        if (ligaData.containsKey("alineacionImg")) {
                            Map<String, Object> alineacionImg = (Map<String, Object>) ligaData.get("alineacionImg");
                            boolean modificadoImg = false;
                            for (String pos : posicionesAEliminar) {
                                if (alineacionImg.containsKey(pos)) {
                                    alineacionImg.put(pos, null);
                                    modificadoImg = true;
                                }
                            }
                            if (modificadoImg) {
                                ligaData.put("alineacionImg", alineacionImg);
                            }
                        }


                        userRef.update(ligaId, ligaData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess("Has vendido a " + jugador.getNombre()))
                                .addOnFailureListener(e -> callback.onFailure("Error al vender jugador: " + e.getMessage()));
                    } else {
                        callback.onFailure("Formato de liga incorrecto");
                    }
                } else {
                    callback.onFailure("Liga no encontrada para este usuario");
                }
            } catch (Exception e) {
                callback.onFailure("Error procesando datos del usuario: " + e.getMessage());
            }
        });
    }
    public void cargarJugadoresDelUsuario(String ligaId, final JugadorListCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            try {
                Map<String, Object> data = (Map<String, Object>) documentSnapshot.get(ligaId);
                if (data != null && data.containsKey("jugadores")) {
                    List<Map<String, Object>> jugadoresMap = (List<Map<String, Object>>) data.get("jugadores");
                    List<Jugador> jugadores = new ArrayList<>();

                    for (Map<String, Object> j : jugadoresMap) {
                        jugadores.add(new Jugador(
                                (String) j.get("nombre"),
                                (String) j.get("posicion"),
                                ((Number) j.get("overall")).intValue(),
                                ((Number) j.get("precio")).intValue(),
                                (String) j.get("url")

                        ));
                    }

                    callback.onSuccess(jugadores);
                } else {
                    callback.onSuccess(new ArrayList<>()); // No hay jugadores
                }
            } catch (Exception e) {
                callback.onFailure("Error procesando los datos: " + e.getMessage());
            }
        }).addOnFailureListener(e -> callback.onFailure("Error de base de datos: " + e.getMessage()));
    }

    public interface JugadorListCallback {
        void onSuccess(List<Jugador> jugadores);
        void onFailure(String errorMessage);
    }

    public void saveTactic(String userId, String ligaName, String tacticKey, int intensityValue) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Mapa para tácticas
        Map<String, Object> tacticasMap = new HashMap<>();
        tacticasMap.put(tacticKey, intensityValue); // ej: "Agresividad" -> 4

        // Mapa que contiene "tacticas" como subdocumento de la liga
        Map<String, Object> ligaMap = new HashMap<>();
        ligaMap.put("tacticas", tacticasMap);

        // Mapa final que contiene la liga (ej: "yh") como clave
        Map<String, Object> update = new HashMap<>();
        update.put(ligaIdHash, ligaMap);

        userDocRef.set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Táctica guardada correctamente"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar táctica: " + e.getMessage()));
    }
    public void getTactic(String userId, String ligaName, String tacticKey, FirestoreCallbackWithInt callback) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Map<String, Object> ligaData = (Map<String, Object>) doc.get(ligaIdHash);
                if (ligaData != null && ligaData.containsKey("tacticas")) {
                    Map<String, Object> tacticas = (Map<String, Object>) ligaData.get("tacticas");
                    Number intensidad = (Number) tacticas.get(tacticKey);
                    if (intensidad != null) {
                        callback.onSuccess(intensidad.intValue());
                        return;
                    }
                }
            }
            // Si no se encuentra, usar valor por defecto (Media = 3)
            callback.onSuccess(3);
        }).addOnFailureListener(e -> callback.onFailure("Error al obtener intensidad: " + e.getMessage()));
    }

    // Interfaz para el callback
    public interface FirestoreCallbackWithInt {
        void onSuccess(int value);
        void onFailure(String errorMessage);
    }

    public void calcularMediaEquipo(String userId, String ligaName, MediaCallback callback) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> ligas = (Map<String, Object>) documentSnapshot.get(ligaIdHash);
                if (ligas != null) {
                    Map<String, Object> alineacion = (Map<String, Object>) ligas.get("alineacion");
                    List<Map<String, Object>> jugadores = (List<Map<String, Object>>) ligas.get("jugadores");

                    if (alineacion != null && jugadores != null) {
                        int sumaOveralls = 0;

                        for (Object jugadorNombreObj : alineacion.values()) {
                            String jugadorNombre = (String) jugadorNombreObj;

                            for (Map<String, Object> jugador : jugadores) {
                                String nombre = (String) jugador.get("nombre");
                                if (nombre.equals(jugadorNombre)) {
                                    Long overall = (Long) jugador.get("overall");
                                    sumaOveralls += overall.intValue();
                                    break;
                                }
                            }
                        }

                        int media = Math.round((float) sumaOveralls / 11);
                        callback.onMediaCalculated(media);

                    } else {
                        callback.onMediaCalculated(0);
                    }
                } else {
                    callback.onMediaCalculated(0);
                }
            }
        }).addOnFailureListener(e -> {
            callback.onError(e.getMessage());
        });
    }
    public interface MediaCallback {
        void onMediaCalculated(int media);
        void onError(String error);
    }



    public void fetchRivalAverageByTeamName(String rivalName, AverageCallback callback) {
        if (rivalName == null || rivalName.isEmpty()) {
            callback.onError("Nombre del rival vacío");
            return;
        }

        // Mapeo de nombres a funciones Firebase
        Map<String, String> teamFunctionMap = new HashMap<>();
        teamFunctionMap.put("Barcelona", "getBarcelonaPlayers");
        teamFunctionMap.put("Real Madrid", "getMadridPlayers");
        teamFunctionMap.put("Atlético", "getAtleticoPlayers");
        teamFunctionMap.put("Atlético de Madrid", "getAtleticoPlayers");
        teamFunctionMap.put("Liverpool", "getLiverpoolPlayers");
        teamFunctionMap.put("Chelsea", "getChelseaPlayers");
        teamFunctionMap.put("Man City", "getManCityPlayers");
        teamFunctionMap.put("Manchester City", "getManCityPlayers");
        teamFunctionMap.put("Athletic Club", "getAthleticClubPlayers");
        teamFunctionMap.put("Arsenal", "getArsenalPlayers");

        String functionName = teamFunctionMap.get(rivalName);

        if (functionName == null) {
            callback.onError("No se encontró función para el rival: " + rivalName);
            return;
        }

        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        functions.getHttpsCallable(functionName)
                .call()
                .addOnSuccessListener(result -> {
                    if (result.getData() instanceof Map) {
                        Map<String, Object> responseMap = (Map<String, Object>) result.getData();
                        if (responseMap.containsKey("averageOverall")) {
                            double average = ((Number) responseMap.get("averageOverall")).doubleValue();
                            Log.d("FireStoreHelper", "Media del rival " + rivalName + " cargada: " + average); // Log añadido
                            callback.onAverageLoaded(average);
                        } else {
                            callback.onError("No se encontró 'averageOverall'");
                        }
                    } else {
                        callback.onError("Respuesta inválida");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreHelper", "Error al obtener media del rival: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }



    public interface AverageCallback {
        void onAverageLoaded(double average);
        void onError(String error);
    }

    public void descontarMillonPorInspeccion(String ligaId, InspeccionCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            try {
                Map<String, Object> userData = documentSnapshot.getData();
                if (userData != null && userData.containsKey(ligaId)) {
                    Object rawLigaData = userData.get(ligaId);
                    if (rawLigaData instanceof Map) {
                        Map<String, Object> ligaData = (Map<String, Object>) rawLigaData;
                        long dinero = ((Number) ligaData.get("dinero")).longValue();

                        if (dinero < 1_000_000) {
                            callback.onFailure("No tienes suficiente dinero");
                            return;
                        }

                        ligaData.put("dinero", dinero - 1_000_000);

                        userRef.update(ligaId, ligaData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess("Inspección realizada"))
                                .addOnFailureListener(e -> callback.onFailure("Error al descontar dinero: " + e.getMessage()));
                    } else {
                        callback.onFailure("Formato de liga incorrecto");
                    }
                } else {
                    callback.onFailure("Liga no encontrada");
                }
            } catch (Exception e) {
                callback.onFailure("Error procesando datos: " + e.getMessage());
            }
        });
    }
    public interface InspeccionCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface EstadioCallback {
        void onEstadioDataLoaded(int nivelCentroMedico, int nivelCiudadDeportiva, int nivelEstadio);
        void onError(String error);
    }

    public void getEstadioData(String userId, String ligaName, EstadioCallback callback) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
                    Map<String, Object> ligaData = (Map<String, Object>) document.get(ligaIdHash);

                    if (ligaData != null && ligaData.containsKey("estadio")) {
                        Map<String, Object> estadio = (Map<String, Object>) ligaData.get("estadio");

                        int nivelCentroMedico = estadio.containsKey("nivel_centro_medico")
                                ? ((Long) estadio.get("nivel_centro_medico")).intValue() : 0;

                        int nivelCiudadDeportiva = estadio.containsKey("nivel_ciudad_deportiva")
                                ? ((Long) estadio.get("nivel_ciudad_deportiva")).intValue() : 0;

                        int nivelEstadio = estadio.containsKey("nivel_estadio")
                                ? ((Long) estadio.get("nivel_estadio")).intValue() : 0;

                        callback.onEstadioDataLoaded(nivelCentroMedico, nivelCiudadDeportiva, nivelEstadio);
                    } else {
                        callback.onError("El campo 'estadio' no existe en la liga especificada.");
                    }
                } else {
                    callback.onError("Documento del usuario no encontrado.");
                }
            } else {
                callback.onError("Error al obtener el documento: " + task.getException());
            }
        });
    }

    public void getTacticaData(String userId, String ligaName, TacticasCallback callback){
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
                    Map<String, Object> ligaData = (Map<String, Object>) document.get(ligaIdHash);

                    if (ligaData != null && ligaData.containsKey("tacticas")) {
                        Map<String, Object> estadio = (Map<String, Object>) ligaData.get("tacticas");

                        int agresividad = estadio.containsKey("Agresividad")
                                ? ((Long) estadio.get("Agresividad")).intValue() : 0;

                        int contraataques = estadio.containsKey("Contraataques")
                                ? ((Long) estadio.get("Contraataques")).intValue() : 0;

                        int posesion = estadio.containsKey("Posesión")
                                ? ((Long) estadio.get("Posesión")).intValue() : 0;

                        int presion = estadio.containsKey("Presión")
                                ? ((Long) estadio.get("Presión")).intValue() : 0;

                       callback.onTacticasDataLoaded(agresividad, contraataques, posesion,presion);
                    } else {
                        callback.onError("El campo 'estadio' no existe en la liga especificada.");
                    }
                } else {
                    callback.onError("Documento del usuario no encontrado.");
                }
            } else {
                callback.onError("Error al obtener el documento: " + task.getException());
            }
        });

    }

    public interface TacticasCallback {
        void onTacticasDataLoaded(int agresividad, int contraataques, int posesion,int presion);
        void onError(String error);
    }
    public void actualizarDineroPorResultado(String ligaId, long resultadoDinero, FirestoreUpdateCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("ActualizarDinero", "Usuario no autenticado");
            if (callback != null) {
                callback.onFailure("Usuario no autenticado");
            }
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            Map<String, Object> userData = snapshot.getData();
            if (userData != null && userData.containsKey(ligaId)) {
                Object rawLigaData = userData.get(ligaId);
                if (rawLigaData instanceof Map) {
                    Map<String, Object> ligaData = (Map<String, Object>) rawLigaData;
                    long dinero = ((Number) ligaData.get("dinero")).longValue();

                    dinero+=resultadoDinero;

                    ligaData.put("dinero", dinero);
                    transaction.update(userRef, ligaId, ligaData);

                    Log.d("ActualizarDinero", "Dinero después de la actualización: " + (dinero + resultadoDinero));

                    return null;
                } else {
                    throw new FirebaseFirestoreException("Formato de liga incorrecto",
                            FirebaseFirestoreException.Code.INVALID_ARGUMENT);
                }
            } else {
                throw new FirebaseFirestoreException("Liga no encontrada",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d("ActualizarDinero", "Dinero actualizado con éxito");
            if (callback != null) {
                callback.onSuccess();
            }
        }).addOnFailureListener(e -> {
            Log.e("ActualizarDinero", "Error al actualizar dinero: " + e.getMessage());
            if (callback != null) {
                callback.onFailure("Error al actualizar dinero: " + e.getMessage());
            }
 });
}


    public void actualizarProgresoLiga(String ligaName, FirestoreUpdateCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> userData = documentSnapshot.getData();

                if (userData != null && userData.containsKey(ligaIdHash)) {
                    Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaIdHash);

                    if (ligaData != null) {
                        Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
                        if (progresoLiga == null) progresoLiga = new HashMap<>();

                        Map<String, Object> pendientesMapRaw = (Map<String, Object>) progresoLiga.get("pendientesJugar");
                        if (pendientesMapRaw == null || pendientesMapRaw.isEmpty()) {
                            callback.onFailure("No hay partidos pendientes.");
                            return;
                        }

                        Map<String, Integer> pendientesMap = new HashMap<>();
                        for (Map.Entry<String, Object> entry : pendientesMapRaw.entrySet()) {
                            try {
                                pendientesMap.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                            } catch (Exception e) {
                                Log.e("FirestoreHelper", "Valor inválido para partido pendiente: " + entry.getKey());
                            }
                        }

                        if (pendientesMap.isEmpty()) {
                            callback.onFailure("No hay partidos pendientes válidos.");
                            return;
                        }

                        String rivalActual = Collections.max(pendientesMap.entrySet(), Map.Entry.comparingByValue()).getKey();
                        int partidosRestantes = pendientesMap.getOrDefault(rivalActual, 0);

                        Log.d("FirestoreHelper", "Rival actual: " + rivalActual + " - Partidos antes: " + partidosRestantes);

                        String rutaPendientes = ligaIdHash + ".progresoLiga.pendientesJugar." + rivalActual;

                        Map<String, Object> updateMap = new HashMap<>();
                        if (partidosRestantes > 1) {
                            updateMap.put(rutaPendientes, partidosRestantes - 1);
                            Log.d("FirestoreHelper", "Actualizando a: " + (partidosRestantes - 1));
                        } else {
                            updateMap.put(rutaPendientes, FieldValue.delete());
                            Log.d("FirestoreHelper", "Eliminando rival: " + rivalActual + " (último partido jugado)");
                        }

                        userRef.update(updateMap)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FirestoreHelper", "Actualización exitosa en Firestore (update)");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreHelper", "Error al actualizar partidos pendientes: " + e.getMessage());
                                    callback.onFailure("Error al actualizar partidos pendientes: " + e.getMessage());
                                });

                    } else {
                        callback.onFailure("Datos de liga no encontrados.");
                    }
                } else {
                    callback.onFailure("Liga no encontrada para el usuario.");
                }
            } else {
                callback.onFailure("Usuario no encontrado.");
            }
        }).addOnFailureListener(e -> callback.onFailure("Error al acceder a Firestore: " + e.getMessage()));
    }



    public interface FirestoreUpdateCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void actualizarEstadisticasPartido(String ligaName, String equipoActual, String equipoRival, String resultado, FirestoreUpdateCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        DocumentReference userLigaRef = db.collection("users").document(userId);

        userLigaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> ligaData = (Map<String, Object>) documentSnapshot.get(ligaIdHash);
                if (ligaData == null) {
                    callback.onFailure("No se encontró la liga en el usuario.");
                    return;
                }

                Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
                if (progresoLiga == null) progresoLiga = new HashMap<>();

                // Actualizar conteo global (ganados, empatados, perdidos) para MI equipo
                String campo = "";
                switch (resultado) {
                    case "ganado": campo = "MIpartidosGanados"; break;
                    case "empatado": campo = "MIpartidosEmpatados"; break;
                    case "perdido": campo = "MIpartidosPerdidos"; break;
                    default: callback.onFailure("Resultado no válido"); return;
                }

                Long valorActual = progresoLiga.get(campo) instanceof Long ? (Long) progresoLiga.get(campo) : 0L;
                progresoLiga.put(campo, valorActual + 1);

                // Actualizar la clasificación (lista)
                List<Map<String, Object>> clasificacion = (List<Map<String, Object>>) progresoLiga.get("clasificacion");
                if (clasificacion == null) {
                    callback.onFailure("No se encontró la clasificación.");
                    return;
                }

                // Función helper para actualizar stats del equipo en la clasificación
                BiConsumer<String, String> actualizarEquipo = (equipo, res) -> {
                    for (Map<String, Object> equipoStats : clasificacion) {
                        if (equipo.equalsIgnoreCase((String) equipoStats.get("equipo"))) {
                            // Obtener actuales
                            int ganados = ((Long) equipoStats.getOrDefault("partidosGanados", 0L)).intValue();
                            int empatados = ((Long) equipoStats.getOrDefault("partidosEmpatados", 0L)).intValue();
                            int perdidos = ((Long) equipoStats.getOrDefault("partidosPerdidos", 0L)).intValue();

                            switch (res) {
                                case "ganado": ganados++; break;
                                case "empatado": empatados++; break;
                                case "perdido": perdidos++; break;
                            }

                            equipoStats.put("partidosGanados", ganados);
                            equipoStats.put("partidosEmpatados", empatados);
                            equipoStats.put("partidosPerdidos", perdidos);
                            break;
                        }
                    }
                };

                // Actualizamos el equipo actual según resultado
                actualizarEquipo.accept(equipoActual, resultado);

                // Para el equipo rival, el resultado es inverso:
                String resultadoRival = "";
                switch (resultado) {
                    case "ganado": resultadoRival = "perdido"; break;
                    case "empatado": resultadoRival = "empatado"; break;
                    case "perdido": resultadoRival = "ganado"; break;
                }
                actualizarEquipo.accept(equipoRival, resultadoRival);

                // Guardar actualización en Firestore
                progresoLiga.put("clasificacion", clasificacion);
                ligaData.put("progresoLiga", progresoLiga);

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put(ligaIdHash, ligaData);

                userLigaRef.set(updateMap, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));

            } else {
                callback.onFailure("No se encontró el documento del usuario.");
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface ClasificacionCallback {
        void onSuccess(Map<String, Object> equipoPropio);
        void onFailure(String errorMessage);
    }

    public void obtenerEstadisticasClasificacion(String ligaName, ClasificacionCallback callback) {
        obtenerDatosLigaPorId(ligaName, new FirestoreCallback1() {
            @Override
            public void onSuccess(Map<String, Object> ligaData) {
                if (ligaData == null) {
                    callback.onFailure("Datos de la liga no encontrados.");
                    return;
                }

                Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
                if (progresoLiga == null) {
                    callback.onFailure("Progreso de liga no encontrado.");
                    return;
                }

                int ganados = ((Long) progresoLiga.getOrDefault("MIpartidosGanados", 0L)).intValue();
                int empatados = ((Long) progresoLiga.getOrDefault("MIpartidosEmpatados", 0L)).intValue();
                int perdidos = ((Long) progresoLiga.getOrDefault("MIpartidosPerdidos", 0L)).intValue();
                int golesAfavor= ((Long) progresoLiga.getOrDefault("MIgolesAFavor", 0L)).intValue();
                int golesContra = ((Long) progresoLiga.getOrDefault("MIgolesEnContra", 0L)).intValue();
                int puntos = (ganados * 3) + (empatados);

                // Aquí recuperas el nombre del equipo
                String nombreEquipo = (String) ligaData.get("equipo");
                if (nombreEquipo == null) nombreEquipo = "Equipo desconocido";

                Map<String, Object> equipoPropio = new HashMap<>();
                equipoPropio.put("equipo", nombreEquipo);
                equipoPropio.put("partidosGanados", ganados);
                equipoPropio.put("partidosEmpatados", empatados);
                equipoPropio.put("partidosPerdidos", perdidos);
                equipoPropio.put("golesAfavor", golesAfavor);
                equipoPropio.put("golesContra", golesContra);
                equipoPropio.put("puntos", puntos);

                callback.onSuccess(equipoPropio);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

    public void cargarAlineacionConImagenes(String userId, String ligaName, Map<String, ImageView> imageViews) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        // Establecer imagen por defecto para todos los ImageView
        for (ImageView imageView : imageViews.values()) {
            Glide.with(imageView.getContext())
                    .load(R.drawable.defaultplayer)
                    .circleCrop()
                    .into(imageView);
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> ligas = (Map<String, Object>) documentSnapshot.get(ligaIdHash);
                        if (ligas != null && ligas.containsKey("alineacionImg")) {
                            Map<String, Object> alineacion = (Map<String, Object>) ligas.get("alineacionImg");
                            for (String posicion : imageViews.keySet()) {
                                String imageUrl = (String) alineacion.get(posicion);
                                ImageView imageView = imageViews.get(posicion);
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
                                }
                            }
                        }
                    }
                });
    }


    public void guardarAlineacionConImagenes(String userId, String ligaName, String posicionClave, String imageUrl, Runnable onSuccess) {
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        Map<String, Object> alineacionMap = new HashMap<>();
        alineacionMap.put(posicionClave, imageUrl);

        Map<String, Object> ligaMap = new HashMap<>();
        ligaMap.put("alineacionImg", alineacionMap);

        Map<String, Object> update = new HashMap<>();
        update.put(ligaIdHash, ligaMap);

        userDocRef.set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Alineación guardada correctamente con imagen");
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar alineación con imagen: " + e.getMessage()));
    }


    public interface JugadorImgListCallback {
        void onSuccess(List<Jugador> jugadores);
        void onFailure(String error);
    }

    public void cargarJugadoresConImagenes(String ligaId, final JugadorImgListCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuario no autenticado");
            return;
        }

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            try {
                if (documentSnapshot.exists()) {
                    Map<String, Object> allData = documentSnapshot.getData();
                    String ligaIdHash = ligaId.toLowerCase().replaceAll("[^a-z0-9]", "_");

                    Map<String, Object> ligaData = (Map<String, Object>) allData.get(ligaIdHash);
                    if (ligaData != null && ligaData.containsKey("jugadores")) {
                        List<Map<String, Object>> jugadoresMap = (List<Map<String, Object>>) ligaData.get("jugadores");
                        List<Jugador> jugadores = new ArrayList<>();

                        for (Map<String, Object> j : jugadoresMap) {
                            if (j.containsKey("nombre") && j.containsKey("posicion") && j.containsKey("overall") &&
                                    j.containsKey("precio") && j.containsKey("url")) {

                                String nombre = (String) j.get("nombre");
                                String posicion = (String) j.get("posicion");
                                int overall = ((Number) j.get("overall")).intValue();
                                int precio = ((Number) j.get("precio")).intValue();
                                String imageUrl = (String) j.get("url");

                                if (imageUrl == null || imageUrl.isEmpty()) continue;

                                Jugador jugador = new Jugador(nombre, posicion, overall, precio, imageUrl);
                                jugadores.add(jugador);
                            }
                        }

                        callback.onSuccess(jugadores);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    callback.onSuccess(new ArrayList<>());
                }
            } catch (Exception e) {
                callback.onFailure("Error procesando los datos: " + e.getMessage());
            }
        }).addOnFailureListener(e -> {
            callback.onFailure("Error de base de datos: " + e.getMessage());
        });
    }




    public void obtenerClasificacionCompleta(String ligaName, ListaClasificacionCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        if (userData != null && userData.containsKey(ligaName)) {
                            Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaName);
                            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");

                            List<Map<String, Object>> clasificacion = (List<Map<String, Object>>) progresoLiga.get("clasificacion");
                            callback.onSuccess(clasificacion);
                        } else {
                            callback.onFailure("Liga no encontrada.");
                        }
                    } else {
                        callback.onFailure("Documento de usuario no encontrado.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface ListaClasificacionCallback {
        void onSuccess(List<Map<String, Object>> clasificacion);
        void onFailure(String errorMessage);
    }
    public void actualizarClasificacionEnFirestore(String ligaName,String equipoA, String equipoB, String resultado) {
        // Ejemplo para obtener y actualizar en Firestore la clasificación de la liga
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        DocumentReference ligaRef = db.collection("users").document(userId);

        ligaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e("ACTUALIZAR_CLASIFICACION", "No existe el documento del usuario");
                return;
            }

            Map<String, Object> userData = documentSnapshot.getData();
            if (userData == null || !userData.containsKey(ligaIdHash)) {
                Log.e("ACTUALIZAR_CLASIFICACION", "No existe la liga en el documento");
                return;
            }

            Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaIdHash);
            if (ligaData == null || !ligaData.containsKey("progresoLiga")) {
                Log.e("ACTUALIZAR_CLASIFICACION", "No existe progresoLiga");
                return;
            }

            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
            if (progresoLiga == null || !progresoLiga.containsKey("clasificacion")) {
                Log.e("ACTUALIZAR_CLASIFICACION", "No existe la clasificación");
                return;
            }

            List<Map<String, Object>> clasificacion = (List<Map<String, Object>>) progresoLiga.get("clasificacion");
            if (clasificacion == null) return;

            // Buscar y actualizar estadísticas para equipoA y equipoB
            for (Map<String, Object> equipoStats : clasificacion) {
                String equipoNombre = (String) equipoStats.get("equipo");
                if (equipoNombre.equalsIgnoreCase(equipoA)) {
                    if ("A".equals(resultado)) { // equipoA gana
                        int ganados = ((Long) equipoStats.getOrDefault("partidosGanados", 0L)).intValue();
                        equipoStats.put("partidosGanados", ganados + 1);
                    } else if ("E".equals(resultado)) { // empate
                        int empatados = ((Long) equipoStats.getOrDefault("partidosEmpatados", 0L)).intValue();
                        equipoStats.put("partidosEmpatados", empatados + 1);
                    } else { // pierde
                        int perdidos = ((Long) equipoStats.getOrDefault("partidosPerdidos", 0L)).intValue();
                        equipoStats.put("partidosPerdidos", perdidos + 1);
                    }
                } else if (equipoNombre.equalsIgnoreCase(equipoB)) {
                    if ("B".equals(resultado)) { // equipoB gana
                        int ganados = ((Long) equipoStats.getOrDefault("partidosGanados", 0L)).intValue();
                        equipoStats.put("partidosGanados", ganados + 1);
                    } else if ("E".equals(resultado)) { // empate
                        int empatados = ((Long) equipoStats.getOrDefault("partidosEmpatados", 0L)).intValue();
                        equipoStats.put("partidosEmpatados", empatados + 1);
                    } else { // pierde
                        int perdidos = ((Long) equipoStats.getOrDefault("partidosPerdidos", 0L)).intValue();
                        equipoStats.put("partidosPerdidos", perdidos + 1);
                    }
                }
            }

            // Guardar de nuevo la clasificación actualizada
            progresoLiga.put("clasificacion", clasificacion);
            ligaData.put("progresoLiga", progresoLiga);
            userData.put(ligaIdHash, ligaData);

            ligaRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("ACTUALIZAR_CLASIFICACION", "Clasificación actualizada correctamente"))
                    .addOnFailureListener(e -> Log.e("ACTUALIZAR_CLASIFICACION", "Error actualizando clasificación: " + e.getMessage()));

        }).addOnFailureListener(e -> Log.e("ACTUALIZAR_CLASIFICACION", "Error obteniendo usuario: " + e.getMessage()));
    }
    public void darRecompensaPorVideo(String userId, String ligaName, RecompensaCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Map<String, Object> userData = doc.getData();
                if (userData != null && userData.containsKey(ligaName)) {
                    Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaName);
                    if (ligaData != null && ligaData.containsKey("dinero")) {
                        long dineroActual = ((Number) ligaData.get("dinero")).longValue();
                        ligaData.put("dinero", dineroActual + 1_000_000);

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put(ligaName, ligaData);

                        db.collection("users").document(userId).set(updateMap, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> callback.onSuccess("Dinero actualizado"))
                                .addOnFailureListener(e -> callback.onFailure("Error al actualizar dinero: " + e.getMessage()));
                    } else {
                        callback.onFailure("No se encontró el campo 'dinero' en la liga especificada");
                    }
                } else {
                    callback.onFailure("Liga no encontrada");
                }
            } else {
                callback.onFailure("Documento no existe");
            }
        }).addOnFailureListener(e -> callback.onFailure("Error al obtener usuario: " + e.getMessage()));
    }

    public interface RecompensaCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
    public interface UsernameCallback {
        void onUsernameFetched(String username);
        void onError();
    }

    public void fetchUsername(String uid, UsernameCallback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onUsernameFetched(documentSnapshot.getString("username"));
                    } else {
                        callback.onError();
                    }
                })
                .addOnFailureListener(e -> callback.onError());
    }

    public interface UserListsCallback {
        void onListsFetched(List<User> paraSeguir, List<User> siguiendo, List<User> seguidores);
        void onError();
    }

    public void cargarListasUsuarios(String currentUserId, @NonNull UserListsCallback callback) {
        db.collection("users").get().addOnSuccessListener(querySnapshots -> {
            List<User> todosUsuarios = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshots) {
                if (doc.getId().equals(currentUserId)) continue;
                String username = doc.getString("username");
                String email = doc.getString("email");
                if (username != null) {
                    todosUsuarios.add(new User(doc.getId(), username, email));
                }
            }

            db.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> siguiendo = (List<String>) documentSnapshot.get("siguiendo");
                    List<String> seguidores = (List<String>) documentSnapshot.get("seguidores");

                    if (siguiendo == null) siguiendo = new ArrayList<>();
                    if (seguidores == null) seguidores = new ArrayList<>();

                    List<User> listaSiguiendo = new ArrayList<>();
                    List<User> listaSeguidores = new ArrayList<>();
                    List<User> listaParaSeguir = new ArrayList<>();

                    for (User user : todosUsuarios) {
                        String uid = user.getUid();
                        boolean loSigo = siguiendo.contains(uid);
                        boolean meSigue = seguidores.contains(uid);

                        if (loSigo) listaSiguiendo.add(user);
                        if (meSigue) listaSeguidores.add(user);
                        if (!loSigo) listaParaSeguir.add(user);
                    }

                    callback.onListsFetched(listaParaSeguir, listaSiguiendo, listaSeguidores);
                }
            }).addOnFailureListener(e -> callback.onError());
        }).addOnFailureListener(e -> callback.onError());
    }

    public void seguir(Context context, String currentUserId, String uidObjetivo, Runnable onSuccess) {
        db.collection("users").document(currentUserId)
                .update("siguiendo", FieldValue.arrayUnion(uidObjetivo))
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(uidObjetivo).get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    List<String> seguidores = (List<String>) doc.get("seguidores");
                                    if (seguidores == null) {
                                        db.collection("users").document(uidObjetivo)
                                                .update("seguidores", new ArrayList<String>())
                                                .addOnSuccessListener(v -> agregarSeguidor(context, uidObjetivo, currentUserId, onSuccess));
                                    } else {
                                        agregarSeguidor(context, uidObjetivo, currentUserId, onSuccess);
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Error al obtener usuario objetivo", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al seguir", Toast.LENGTH_SHORT).show());
    }

    private void agregarSeguidor(Context context, String uidObjetivo, String currentUserId, Runnable onSuccess) {
        db.collection("users").document(uidObjetivo)
                .update("seguidores", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Ahora sigues al usuario", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al agregar seguidor", Toast.LENGTH_SHORT).show());
    }

    public void dejarDeSeguir(Context context, String currentUserId, String uidObjetivo, Runnable onSuccess) {
        db.collection("users").document(currentUserId)
                .update("siguiendo", FieldValue.arrayRemove(uidObjetivo))
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(uidObjetivo)
                            .update("seguidores", FieldValue.arrayRemove(currentUserId))
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(context, "Dejaste de seguir", Toast.LENGTH_SHORT).show();
                                onSuccess.run();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Error al actualizar seguidores", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al actualizar siguiendo", Toast.LENGTH_SHORT).show());
    }

    public void eliminarSeguidor(Context context, String currentUserId, String uidSeguidor, Runnable onSuccess) {
        db.collection("users").document(currentUserId)
                .update("seguidores", FieldValue.arrayRemove(uidSeguidor))
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(uidSeguidor)
                            .update("siguiendo", FieldValue.arrayRemove(currentUserId))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(context, "Seguidor eliminado", Toast.LENGTH_SHORT).show();
                                onSuccess.run();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar en siguiendo", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar seguidor", Toast.LENGTH_SHORT).show());
    }
    public void getSeguidoresMutuos(String uid, ListaCallback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<String> siguiendo = (List<String>) document.get("siguiendo");
                        List<String> seguidores = (List<String>) document.get("seguidores");

                        if (siguiendo == null) siguiendo = new ArrayList<>();
                        if (seguidores == null) seguidores = new ArrayList<>();

                        // Intersección de siguiendo y seguidores
                        List<String> mutuos = new ArrayList<>();
                        for (String s : siguiendo) {
                            if (seguidores.contains(s)) {
                                mutuos.add(s);
                            }
                        }

                        callback.onListaObtenida(mutuos);

                    } else {
                        callback.onError();
                    }
                }).addOnFailureListener(e -> callback.onError());
    }

    public interface ListaCallback {
        void onListaObtenida(List<String> lista);
        void onError();
    }

    public void enviarMensaje(String chatId, Mensaje mensaje) {
        db.collection("chats")
                .document(chatId)
                .collection("mensajes")
                .add(mensaje);
    }

    public void escucharMensajes(String chatId, EventListener<QuerySnapshot> listener) {
        db.collection("chats")
                .document(chatId)
                .collection("mensajes")
                .orderBy("timestamp")
                .addSnapshotListener(listener);
    }

    public String generarChatId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
    public interface UsuarioCallback {
        void onUsuarioObtenido(User usuario);
        void onError();
    }

    public void getUsuario(String uid, UsuarioCallback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String username = document.getString("username");
                        callback.onUsuarioObtenido(new User(uid, username));
                    } else {
                        callback.onError();
                    }
                }).addOnFailureListener(e -> callback.onError());
    }
    public void enviarMensajeMap(String chatId, Map<String, Object> mensajeMap) {
        FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("mensajes")
                .add(mensajeMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FireStoreHelper", "Mensaje enviado con éxito: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreHelper", "Error al enviar mensaje", e);
                });
    }

    public void userHasLigaConNombre(String ligaName, LigaNameCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nombreFormateado = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> datosUsuario = documentSnapshot.getData();
                        if (datosUsuario != null && datosUsuario.containsKey(nombreFormateado)) {
                            callback.onResult(true); // Ya existe una liga con ese nombre
                        } else {
                            callback.onResult(false); // No existe, se puede crear
                        }
                    } else {
                        callback.onResult(false); // No hay documento, luego no tiene ligas
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error al acceder a Firestore: " + e.getMessage()));
    }

    public interface LigaNameCallback {
        void onResult(boolean exists);
        void onError(String error);
    }
    public void actualizarGolesEnClasificacion(String ligaName, String equipoA, String equipoB, int golesA, int golesB, Runnable onComplete) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        DocumentReference ligaRef = db.collection("users").document(userId);

        ligaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e("ACTUALIZAR_GOLES2", "No existe el documento del usuario");
                return;
            }

            Map<String, Object> userData = documentSnapshot.getData();
            if (userData == null || !userData.containsKey(ligaIdHash)) {
                Log.e("ACTUALIZAR_GOLES2", "No existe la liga en el documento");
                return;
            }

            Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaIdHash);
            if (ligaData == null || !ligaData.containsKey("progresoLiga")) {
                Log.e("ACTUALIZAR_GOLES2", "No existe progresoLiga");
                return;
            }

            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");
            if (progresoLiga == null || !progresoLiga.containsKey("clasificacion")) {
                Log.e("ACTUALIZAR_GOLES2", "No existe la clasificación dentro de progresoLiga");
                return;
            }

            List<Map<String, Object>> clasificacion = (List<Map<String, Object>>) progresoLiga.get("clasificacion");
            if (clasificacion == null) {
                Log.e("ACTUALIZAR_GOLES2", "clasificacion es null dentro de progresoLiga");
                return;
            }

            for (Map<String, Object> equipoStats : clasificacion) {
                String nombreEquipo = (String) equipoStats.get("equipo");
                if (nombreEquipo.equalsIgnoreCase(equipoA)) {
                    int gf = ((Number) equipoStats.getOrDefault("golesAFavor", 0)).intValue();
                    int gc = ((Number) equipoStats.getOrDefault("golesEnContra", 0)).intValue();
                    equipoStats.put("golesAFavor", gf + golesA);
                    equipoStats.put("golesEnContra", gc + golesB);
                } else if (nombreEquipo.equalsIgnoreCase(equipoB)) {
                    int gf = ((Number) equipoStats.getOrDefault("golesAFavor", 0)).intValue();
                    int gc = ((Number) equipoStats.getOrDefault("golesEnContra", 0)).intValue();
                    equipoStats.put("golesAFavor", gf + golesB);
                    equipoStats.put("golesEnContra", gc + golesA);
                }
            }

            // Guardar cambios
            progresoLiga.put("clasificacion", clasificacion);
            ligaData.put("progresoLiga", progresoLiga);
            userData.put(ligaIdHash, ligaData);

            ligaRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ACTUALIZAR_GOLES2", "Goles actualizados correctamente.");
                        if (onComplete != null) onComplete.run();
                    })
                    .addOnFailureListener(e -> Log.e("ACTUALIZAR_GOLES2", "Error al actualizar goles: " + e.getMessage()));

        }).addOnFailureListener(e -> Log.e("ACTUALIZAR_GOLES2", "Error al obtener usuario: " + e.getMessage()));
    }
    public void actualizarGolesAFavorYEnContra(String ligaName, String equipoRival, int golesEquipo, int golesRival) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ligaIdHash = ligaName.toLowerCase().replaceAll("[^a-z0-9]", "_");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            Map<String, Object> userData = snapshot.getData();

            // Asegúrate de que la liga y progresoLiga existan
            if (userData == null || !userData.containsKey(ligaIdHash)) {

                Log.d("ACTUALIZAR_GOLES", "Liga no encontrada");

            }

            Map<String, Object> ligaData = (Map<String, Object>) userData.get(ligaIdHash);
            if (ligaData == null || !ligaData.containsKey("progresoLiga")) {

                Log.d("ACTUALIZAR_GOLES", "progresoLiga no encontrado");

            }

            Map<String, Object> progresoLiga = (Map<String, Object>) ligaData.get("progresoLiga");

            // Actualiza los goles
            int golesAFavor = ((Number) progresoLiga.getOrDefault("MIgolesAFavor", 0)).intValue();
            int golesEnContra = ((Number) progresoLiga.getOrDefault("MIgolesEnContra", 0)).intValue();
            progresoLiga.put("MIgolesAFavor", golesAFavor + golesEquipo);
            progresoLiga.put("MIgolesEnContra", golesEnContra + golesRival);

            // Actualiza la clasificación
            List<Map<String, Object>> clasificacion = (List<Map<String, Object>>) progresoLiga.get("clasificacion");
            for (Map<String, Object> equipoStats : clasificacion) {
                String nombre = (String) equipoStats.get("equipo");
                if (nombre != null && nombre.equalsIgnoreCase(equipoRival)) {
                    int gf = ((Number) equipoStats.getOrDefault("golesAFavor", 0)).intValue();
                    int gc = ((Number) equipoStats.getOrDefault("golesEnContra", 0)).intValue();
                    equipoStats.put("golesAFavor", gf + golesRival);
                    equipoStats.put("golesEnContra", gc + golesEquipo);
                    break;
                }
            }

            // Actualiza el documento en la transacción
            transaction.set(userRef, userData, SetOptions.merge());

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("ACTUALIZAR_GOLES", "Transacción completada con éxito.");
        }).addOnFailureListener(e -> {
            Log.e("ACTUALIZAR_GOLES", "Error en la transacción.", e);
        });

    }

}