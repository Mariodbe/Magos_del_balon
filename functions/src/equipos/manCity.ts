import * as functions from "firebase-functions";

export const getManCityPlayers = functions.https.onRequest(
  (request, response) => {
    const manCityPlayers = [
      {
        name: "Ederson",
        position: "Goalkeeper",
        overall: 88,

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
    ];

    response.json({data: {players: manCityPlayers}});
  }
);
