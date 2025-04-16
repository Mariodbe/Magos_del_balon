import * as functions from "firebase-functions";

export const getLiverpoolPlayers = functions.https.onRequest(
  (request, response) => {
    const liverpoolPlayers = [
      {
        name: "Alisson Becker",
        position: "Goalkeeper",
        overall: 89,

      },
      {
        name: "Virgil van Dijk",
        position: "Defender",
        overall: 89,

      },
      {
        name: "Trent Alexander-Arnold",
        position: "Defender",
        overall: 87,

      },
      {
        name: "Andy Robertson",
        position: "Defender",
        overall: 86,

      },
      {
        name: "Thiago Alcântara",
        position: "Midfielder",
        overall: 86,

      },
      {
        name: "Alexis Mac Allister",
        position: "Midfielder",
        overall: 84,

      },
      {
        name: "Mohamed Salah",
        position: "Forward",
        overall: 90,

      },
      {
        name: "Darwin Núñez",
        position: "Forward",
        overall: 84,

      },
      {
        name: "Luis Díaz",
        position: "Forward",
        overall: 85,

      },
    ];

    response.json({data: {players: liverpoolPlayers}});
  }
);
