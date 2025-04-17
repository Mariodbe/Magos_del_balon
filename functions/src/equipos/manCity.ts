import * as functions from "firebase-functions";

export const getManCityPlayers = functions.https.onRequest(
  (request, response) => {
    const manCityPlayers = [
      // Porteros
      {
        name: "Ederson",
        position: "Goalkeeper",
        overall: 88,
      },
      {
        name: "Stefan Ortega",
        position: "Goalkeeper",
        overall: 80,
      },
      {
        name: "Scott Carson",
        position: "Goalkeeper",
        overall: 70,
      },

      // Defensores
      {
        name: "Kyle Walker",
        position: "Defender",
        overall: 84,
      },
      {
        name: "Rúben Dias",
        position: "Defender",
        overall: 88,
      },
      {
        name: "John Stones",
        position: "Defender",
        overall: 85,
      },
      {
        name: "Nathan Aké",
        position: "Defender",
        overall: 83,
      },
      {
        name: "Joško Gvardiol",
        position: "Defender",
        overall: 84,
      },
      {
        name: "Manuel Akanji",
        position: "Defender",
        overall: 82,
      },
      {
        name: "Rico Lewis",
        position: "Defender",
        overall: 75,
      },
      {
        name: "Vitor Reis",
        position: "Defender",
        overall: 70,
      },
      {
        name: "Abdukodir Khusanov",
        position: "Defender",
        overall: 72,
      },

      // Centrocampistas
      {
        name: "Rodri",
        position: "Midfielder",
        overall: 91,
      },
      {
        name: "Kevin De Bruyne",
        position: "Midfielder",
        overall: 90,
      },
      {
        name: "Bernardo Silva",
        position: "Midfielder",
        overall: 88,
      },
      {
        name: "Mateo Kovačić",
        position: "Midfielder",
        overall: 84,
      },
      {
        name: "İlkay Gündoğan",
        position: "Midfielder",
        overall: 85,
      },
      {
        name: "Matheus Nunes",
        position: "Midfielder",
        overall: 82,
      },
      {
        name: "Nico González",
        position: "Midfielder",
        overall: 80,
      },
      {
        name: "James McAtee",
        position: "Midfielder",
        overall: 75,
      },
      {
        name: "Nico O'Reilly",
        position: "Midfielder",
        overall: 70,
      },

      // Delanteros
      {
        name: "Erling Haaland",
        position: "Forward",
        overall: 91,
      },
      {
        name: "Phil Foden",
        position: "Forward",
        overall: 88,
      },
      {
        name: "Jack Grealish",
        position: "Forward",
        overall: 84,
      },
      {
        name: "Jérémy Doku",
        position: "Forward",
        overall: 83,
      },
      {
        name: "Savinho",
        position: "Forward",
        overall: 78,
      },
      {
        name: "Oscar Bobb",
        position: "Forward",
        overall: 75,
      },
      {
        name: "Omar Marmoush",
        position: "Forward",
        overall: 80,
      },
      {
        name: "Claudio Echeverri",
        position: "Forward",
        overall: 72,
      },
    ];

    response.json({data: {players: manCityPlayers}});
  }
);
