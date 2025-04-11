package com.example.magosdelbalon;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

public class FireStoreHelper {
    private static final String TAG="FireStoreHelper";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    public FireStoreHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void registerUser(String username, String email, String password, Context context) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid(); // Obtener el UID del usuario
                            Log.d("Register", "UID del nuevo usuario: " + userId);

                            // Crear objeto usuario sin la contraseña
                            User user = new User(username, email);

                            // Guardar usuario en Firestore usando el UID como ID del documento
                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
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
            // Intentar iniciar sesión con correo electrónico y contraseña
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                // Obtener el nombre de usuario desde Firestore
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
            // Si no es un correo electrónico, buscar por nombre de usuario
            FirebaseFirestore.getInstance().collection("users").whereEqualTo("username", emailOrUsername)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String userId = document.getId();
                            // Intentar iniciar sesión con el correo electrónico asociado al nombre de usuario
                            String email = document.getString("email");
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
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

                    // Crear o actualizar el documento del usuario en Firestore
                    User user = new User(username, email);
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                            .set(user, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                callback.onSuccess(username);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(context, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
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

                        // Aquí cambiamos la función para obtener jugadores dependiendo del equipo seleccionado
                        fetchPlayersForTeam(equipoName, new FireStoreHelper.PlayersCallback() {
                            @Override
                            public void onPlayersLoaded(List<Jugador> players) {
                                // Asegurarse de que players no sea nulo
                                if (players != null) {
                                    // Convertir la lista de jugadores a una lista de mapas para almacenarla en Firestore
                                    List<Map<String, Object>> playersMapList = new ArrayList<>();
                                    for (Jugador jugador : players) {
                                        Map<String, Object> playerMap = new HashMap<>();
                                        playerMap.put("nombre", jugador.getNombre());
                                        playerMap.put("posicion", jugador.getPosicion());
                                        playerMap.put("overall", jugador.getOverall());
                                        playerMap.put("ritmo", jugador.getRitmo());
                                        playerMap.put("disparo", jugador.getDisparo());
                                        playerMap.put("pase", jugador.getPase());
                                        playerMap.put("regate", jugador.getRegate());
                                        playerMap.put("defensa", jugador.getDefensa());
                                        playerMap.put("fisico", jugador.getFisico());
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
            case "Manchester City":
                fetchManCityPlayers(callback);
                break;
            case "Liverpool":
                fetchLiverpoolPlayers(callback);
                break;
            case "Chelsea":
                fetchChelseaPlayers(callback);
                break;
            default:
                callback.onError("Equipo no reconocido.");
                break;
        }
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
                                            ((Number) playerData.get("pace")).intValue(),
                                            ((Number) playerData.get("shooting")).intValue(),
                                            ((Number) playerData.get("passing")).intValue(),
                                            ((Number) playerData.get("dribbling")).intValue(),
                                            ((Number) playerData.get("defending")).intValue(),
                                            ((Number) playerData.get("physical")).intValue()
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
                                            ((Number) playerData.get("pace")).intValue(),
                                            ((Number) playerData.get("shooting")).intValue(),
                                            ((Number) playerData.get("passing")).intValue(),
                                            ((Number) playerData.get("dribbling")).intValue(),
                                            ((Number) playerData.get("defending")).intValue(),
                                            ((Number) playerData.get("physical")).intValue()
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
                                            ((Number) playerData.get("pace")).intValue(),
                                            ((Number) playerData.get("shooting")).intValue(),
                                            ((Number) playerData.get("passing")).intValue(),
                                            ((Number) playerData.get("dribbling")).intValue(),
                                            ((Number) playerData.get("defending")).intValue(),
                                            ((Number) playerData.get("physical")).intValue()
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
                                            ((Number) playerData.get("pace")).intValue(),
                                            ((Number) playerData.get("shooting")).intValue(),
                                            ((Number) playerData.get("passing")).intValue(),
                                            ((Number) playerData.get("dribbling")).intValue(),
                                            ((Number) playerData.get("defending")).intValue(),
                                            ((Number) playerData.get("physical")).intValue()
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
                                            ((Number) playerData.get("pace")).intValue(),
                                            ((Number) playerData.get("shooting")).intValue(),
                                            ((Number) playerData.get("passing")).intValue(),
                                            ((Number) playerData.get("dribbling")).intValue(),
                                            ((Number) playerData.get("defending")).intValue(),
                                            ((Number) playerData.get("physical")).intValue()
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
                                            ((Number) playerData.get("pace")).intValue(),
                                            ((Number) playerData.get("shooting")).intValue(),
                                            ((Number) playerData.get("passing")).intValue(),
                                            ((Number) playerData.get("dribbling")).intValue(),
                                            ((Number) playerData.get("defending")).intValue(),
                                            ((Number) playerData.get("physical")).intValue()
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
                        String ligaKey = "liga_" + ligaSlot; // Assuming the keys are named like liga_1, liga_2, etc.
                        //Check if a key like liga_1, liga_2, liga_3 or liga_4 exists.
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

}
