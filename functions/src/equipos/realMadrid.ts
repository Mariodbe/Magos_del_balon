import * as functions from "firebase-functions";

export const getMadridPlayers = functions.https.onRequest(
  (request, response) => {
    const madridPlayers = [
      {
        name: "Thibaut Courtois",
        position: "Goalkeeper",
        overall: 90,
        precio: 7470000,
      },
      {
        name: "Andriy Lunin",
        position: "Goalkeeper",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Dani Carvajal",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Éder Militão",
        position: "Defender",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "David Alaba",
        position: "Defender",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defender",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Ferland Mendy",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Aurélien Tchouaméni",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Luka Modrić",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Toni Kroos",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Eduardo Camavinga",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Jude Bellingham",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Federico Valverde",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
      },
      {
        name: "Vinícius Jr.",
        position: "Forward",
        overall: 89,
        precio: 7387000,
      },
      {
        name: "Rodrygo",
        position: "Forward",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Joselu",
        position: "Forward",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Brahim Díaz",
        position: "Forward",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "Arda Güler",
        position: "Forward",
        overall: 78,
        precio: 6474000,
      },
    ];

    response.json({ data: { players: madridPlayers } });
  }
);
