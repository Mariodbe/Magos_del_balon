import * as functions from "firebase-functions";

export const getAtleticoPlayers = functions.https.onRequest(
  (request, response) => {
    const atleticoPlayers = [
      {
        name: "Jan Oblak",
        position: "Goalkeeper",
        overall: 88,
        precio: 7744000,
      },
      {
        name: "Ivo Grbić",
        position: "Goalkeeper",
        overall: 77,
        precio: 5929000,
      },
      {
        name: "José María Giménez",
        position: "Defender",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Stefan Savić",
        position: "Defender",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Mario Hermoso",
        position: "Defender",
        overall: 81,
        precio: 6561000,
      },
      {
        name: "Nahuel Molina",
        position: "Defender",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Reinildo Mandava",
        position: "Defender",
        overall: 79,
        precio: 6241000,
      },
      {
        name: "Axel Witsel",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
      },
      {
        name: "Rodrigo De Paul",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Koke",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Marcos Llorente",
        position: "Midfielder",
        overall: 85,
        precio: 7225000,
      },
      {
        name: "Saúl Ñíguez",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Thomas Lemar",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
      },
      {
        name: "Antoine Griezmann",
        position: "Forward",
        overall: 88,
        precio: 7744000,
      },
      {
        name: "Álvaro Morata",
        position: "Forward",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Memphis Depay",
        position: "Forward",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Ángel Correa",
        position: "Forward",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Samuel Lino",
        position: "Forward",
        overall: 80,
        precio: 6400000,
      },
    ];

    const totalOverall = atleticoPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / atleticoPlayers.length;

    response.json({data: {players: atleticoPlayers, averageOverall}});
  }
);
