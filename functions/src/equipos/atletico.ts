import * as functions from "firebase-functions";

export const getAtleticoPlayers = functions.https.onRequest(
  (request, response) => {
    const atleticoPlayers = [
      {
        name: "Jan Oblak",
        position: "Goalkeeper",
        overall: 88,

      },
      {
        name: "Ivo Grbić",
        position: "Goalkeeper",
        overall: 77,

      },
      {
        name: "José María Giménez",
        position: "Defender",
        overall: 84,

      },
      {
        name: "Stefan Savić",
        position: "Defender",
        overall: 82,

      },
      {
        name: "Mario Hermoso",
        position: "Defender",
        overall: 81,

      },
      {
        name: "Nahuel Molina",
        position: "Defender",
        overall: 80,

      },
      {
        name: "Reinildo Mandava",
        position: "Defender",
        overall: 79,

      },
      {
        name: "Axel Witsel",
        position: "Midfielder",
        overall: 81,

      },
      {
        name: "Rodrigo De Paul",
        position: "Midfielder",
        overall: 83,

      },
      {
        name: "Koke",
        position: "Midfielder",
        overall: 84,

      },
      {
        name: "Marcos Llorente",
        position: "Midfielder",
        overall: 85,

      },
      {
        name: "Saúl Ñíguez",
        position: "Midfielder",
        overall: 80,

      },
      {
        name: "Thomas Lemar",
        position: "Midfielder",
        overall: 81,

      },
      {
        name: "Antoine Griezmann",
        position: "Forward",
        overall: 88,

      },
      {
        name: "Álvaro Morata",
        position: "Forward",
        overall: 84,

      },
      {
        name: "Memphis Depay",
        position: "Forward",
        overall: 83,

      },
      {
        name: "Ángel Correa",
        position: "Forward",
        overall: 82,

      },
      {
        name: "Samuel Lino",
        position: "Forward",
        overall: 80,

      },
    ];

    response.json({data: {players: atleticoPlayers}});
  }
);
