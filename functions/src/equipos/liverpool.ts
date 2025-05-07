import * as functions from "firebase-functions";

export const getLiverpoolPlayers = functions.https.onRequest(
  (request, response) => {
    const liverpoolPlayers = [
      {
        name: "Alisson Becker",
        position: "Goalkeeper",
        overall: 89,
        precio: 7387000,
      },
      {
        name: "Caoimhín Kelleher",
        position: "Goalkeeper",
        overall: 78,
        precio: 6474000,
      },
      {
        name: "Vítězslav Jaroš",
        position: "Goalkeeper",
        overall: 70,
        precio: 5810000,
      },
      {
        name: "Harvey Davies",
        position: "Goalkeeper",
        overall: 68,
        precio: 5644000,
      },
      {
        name: "Virgil van Dijk",
        position: "Defender",
        overall: 89,
        precio: 7387000,
      },
      {
        name: "Ibrahima Konaté",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Joe Gomez",
        position: "Defender",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "Andy Robertson",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Trent Alexander-Arnold",
        position: "Defender",
        overall: 87,
        precio: 7221000,
      },
      {
        name: "Kostas Tsimikas",
        position: "Defender",
        overall: 77,
        precio: 6381000,
      },
      {
        name: "Jarell Quansah",
        position: "Defender",
        overall: 70,
        precio: 5810000,
      },
      {
        name: "Conor Bradley",
        position: "Defender",
        overall: 72,
        precio: 5976000,
      },
      {
        name: "Wataru Endo",
        position: "Midfielder",
        overall: 81,
        precio: 6723000,
      },
      {
        name: "Alexis Mac Allister",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Dominik Szoboszlai",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Curtis Jones",
        position: "Midfielder",
        overall: 78,
        precio: 6474000,
      },
      {
        name: "Harvey Elliott",
        position: "Midfielder",
        overall: 77,
        precio: 6381000,
      },
      {
        name: "Ryan Gravenberch",
        position: "Midfielder",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "James McConnell",
        position: "Midfielder",
        overall: 68,
        precio: 5644000,
      },
      {
        name: "Trey Nyoni",
        position: "Midfielder",
        overall: 65,
        precio: 5395000,
      },
      {
        name: "Mohamed Salah",
        position: "Forward",
        overall: 90,
        precio: 7470000,
      },
      {
        name: "Luis Díaz",
        position: "Forward",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Darwin Núñez",
        position: "Forward",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Diogo Jota",
        position: "Forward",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Cody Gakpo",
        position: "Forward",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Federico Chiesa",
        position: "Forward",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Ben Doak",
        position: "Forward",
        overall: 70,
        precio: 5810000,
      },
      {
        name: "Trent Koné-Doherty",
        position: "Forward",
        overall: 68,
        precio: 5644000,
      },
    ];
    const totalOverall = liverpoolPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / liverpoolPlayers.length;

    response.json({data: {players: liverpoolPlayers, averageOverall}});
  }
);
