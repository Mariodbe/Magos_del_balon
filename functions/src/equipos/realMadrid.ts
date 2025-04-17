import * as functions from "firebase-functions";

export const getMadridPlayers = functions.https.onRequest(
  (request, response) => {
    const madridPlayers = [
      {
        name: "Thibaut Courtois",
        position: "Goalkeeper",
        overall: 90,
      },
      {
        name: "Andriy Lunin",
        position: "Goalkeeper",
        overall: 82,
      },
      {
        name: "Dani Carvajal",
        position: "Defender",
        overall: 84,
      },
      {
        name: "Éder Militão",
        position: "Defender",
        overall: 86,
      },
      {
        name: "David Alaba",
        position: "Defender",
        overall: 85,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defender",
        overall: 86,
      },
      {
        name: "Ferland Mendy",
        position: "Defender",
        overall: 83,
      },
      {
        name: "Aurélien Tchouaméni",
        position: "Midfielder",
        overall: 84,
      },
      {
        name: "Luka Modrić",
        position: "Midfielder",
        overall: 87,
      },
      {
        name: "Toni Kroos",
        position: "Midfielder",
        overall: 86,
      },
      {
        name: "Eduardo Camavinga",
        position: "Midfielder",
        overall: 83,
      },
      {
        name: "Jude Bellingham",
        position: "Midfielder",
        overall: 87,
      },
      {
        name: "Federico Valverde",
        position: "Midfielder",
        overall: 86,
      },
      {
        name: "Vinícius Jr.",
        position: "Forward",
        overall: 89,
      },
      {
        name: "Rodrygo",
        position: "Forward",
        overall: 85,
      },
      {
        name: "Joselu",
        position: "Forward",
        overall: 82,
      },
      {
        name: "Brahim Díaz",
        position: "Forward",
        overall: 80,
      },
      {
        name: "Arda Güler",
        position: "Forward",
        overall: 78,
      },
    ];

    response.json({data: {players: madridPlayers}});
  }
);
