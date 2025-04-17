import * as functions from "firebase-functions";

export const getLiverpoolPlayers = functions.https.onRequest(
  (request, response) => {
    const liverpoolPlayers = [
      // Porteros
      {
        name: "Alisson Becker",
        position: "Goalkeeper",
        overall: 89,
      },
      {
        name: "Caoimhín Kelleher",
        position: "Goalkeeper",
        overall: 78,
      },
      {
        name: "Vítězslav Jaroš",
        position: "Goalkeeper",
        overall: 70,
      },
      {
        name: "Harvey Davies",
        position: "Goalkeeper",
        overall: 68,
      },

      // Defensas
      {
        name: "Virgil van Dijk",
        position: "Defender",
        overall: 89,
      },
      {
        name: "Ibrahima Konaté",
        position: "Defender",
        overall: 83,
      },
      {
        name: "Joe Gomez",
        position: "Defender",
        overall: 80,
      },
      {
        name: "Andy Robertson",
        position: "Defender",
        overall: 84,
      },
      {
        name: "Trent Alexander-Arnold",
        position: "Defender",
        overall: 87,
      },
      {
        name: "Kostas Tsimikas",
        position: "Defender",
        overall: 77,
      },
      {
        name: "Jarell Quansah",
        position: "Defender",
        overall: 70,
      },
      {
        name: "Conor Bradley",
        position: "Defender",
        overall: 72,
      },

      // Centrocampistas
      {
        name: "Wataru Endo",
        position: "Midfielder",
        overall: 81,
      },
      {
        name: "Alexis Mac Allister",
        position: "Midfielder",
        overall: 84,
      },
      {
        name: "Dominik Szoboszlai",
        position: "Midfielder",
        overall: 83,
      },
      {
        name: "Curtis Jones",
        position: "Midfielder",
        overall: 78,
      },
      {
        name: "Harvey Elliott",
        position: "Midfielder",
        overall: 77,
      },
      {
        name: "Ryan Gravenberch",
        position: "Midfielder",
        overall: 80,
      },
      {
        name: "James McConnell",
        position: "Midfielder",
        overall: 68,
      },
      {
        name: "Trey Nyoni",
        position: "Midfielder",
        overall: 65,
      },

      // Delanteros
      {
        name: "Mohamed Salah",
        position: "Forward",
        overall: 90,
      },
      {
        name: "Luis Díaz",
        position: "Forward",
        overall: 85,
      },
      {
        name: "Darwin Núñez",
        position: "Forward",
        overall: 84,
      },
      {
        name: "Diogo Jota",
        position: "Forward",
        overall: 83,
      },
      {
        name: "Cody Gakpo",
        position: "Forward",
        overall: 82,
      },
      {
        name: "Federico Chiesa",
        position: "Forward",
        overall: 85,
      },
      {
        name: "Ben Doak",
        position: "Forward",
        overall: 70,
      },
      {
        name: "Trent Koné-Doherty",
        position: "Forward",
        overall: 68,
      },
    ];

    response.json({data: {players: liverpoolPlayers}});
  }
);
