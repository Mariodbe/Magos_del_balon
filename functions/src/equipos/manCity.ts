import * as functions from "firebase-functions";

export const getManCityPlayers = functions.https.onRequest(
  (request, response) => {
    const manCityPlayers = [
      {
        name: "Ederson",
        position: "Goalkeeper",
        overall: 88,
        pace: 56,
        shooting: 45,
        passing: 85,
        dribbling: 65,
        defending: 45,
        physical: 70,
      },
      {
        name: "Rúben Dias",
        position: "Defender",
        overall: 88,
        pace: 63,
        shooting: 50,
        passing: 75,
        dribbling: 65,
        defending: 90,
        physical: 85,
      },
      {
        name: "John Stones",
        position: "Defender",
        overall: 85,
        pace: 65,
        shooting: 52,
        passing: 76,
        dribbling: 70,
        defending: 84,
        physical: 80,
      },
      {
        name: "Rodri",
        position: "Midfielder",
        overall: 89,
        pace: 66,
        shooting: 70,
        passing: 88,
        dribbling: 80,
        defending: 85,
        physical: 84,
      },
      {
        name: "Kevin De Bruyne",
        position: "Midfielder",
        overall: 91,
        pace: 76,
        shooting: 88,
        passing: 94,
        dribbling: 87,
        defending: 65,
        physical: 78,
      },
      {
        name: "Bernardo Silva",
        position: "Midfielder",
        overall: 88,
        pace: 80,
        shooting: 82,
        passing: 86,
        dribbling: 90,
        defending: 60,
        physical: 70,
      },
      {
        name: "Erling Haaland",
        position: "Forward",
        overall: 91,
        pace: 90,
        shooting: 92,
        passing: 72,
        dribbling: 80,
        defending: 45,
        physical: 88,
      },
      {
        name: "Phil Foden",
        position: "Forward",
        overall: 86,
        pace: 85,
        shooting: 82,
        passing: 84,
        dribbling: 88,
        defending: 45,
        physical: 70,
      },
      {
        name: "Jack Grealish",
        position: "Forward",
        overall: 84,
        pace: 80,
        shooting: 78,
        passing: 82,
        dribbling: 86,
        defending: 40,
        physical: 70,
      },
    ];

    response.json({data: {players: manCityPlayers}});
  }
);
