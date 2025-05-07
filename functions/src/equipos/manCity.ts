import * as functions from "firebase-functions";

export const getManCityPlayers = functions.https.onRequest(
  (request, response) => {
    const manCityPlayers = [
      {
        name: "Ederson",
        position: "Goalkeeper",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Stefan Ortega",
        position: "Goalkeeper",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "Scott Carson",
        position: "Goalkeeper",
        overall: 70,
        precio: 5810000,
      },
      {
        name: "Kyle Walker",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Rúben Dias",
        position: "Defender",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "John Stones",
        position: "Defender",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Nathan Aké",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Joško Gvardiol",
        position: "Defender",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Manuel Akanji",
        position: "Defender",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Rico Lewis",
        position: "Defender",
        overall: 75,
        precio: 6225000,
      },
      {
        name: "Vitor Reis",
        position: "Defender",
        overall: 70,
        precio: 5810000,
      },
      {
        name: "Abdukodir Khusanov",
        position: "Defender",
        overall: 72,
        precio: 5976000,
      },
      {
        name: "Rodri",
        position: "Midfielder",
        overall: 91,
        precio: 7553000,
      },
      {
        name: "Kevin De Bruyne",
        position: "Midfielder",
        overall: 90,
        precio: 7470000,
      },
      {
        name: "Bernardo Silva",
        position: "Midfielder",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Mateo Kovačić",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "İlkay Gündoğan",
        position: "Midfielder",
        overall: 85,
        precio: 7055000,
      },
      {
        name: "Matheus Nunes",
        position: "Midfielder",
        overall: 82,
        precio: 6806000,
      },
      {
        name: "Nico González",
        position: "Midfielder",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "James McAtee",
        position: "Midfielder",
        overall: 75,
        precio: 6225000,
      },
      {
        name: "Nico O'Reilly",
        position: "Midfielder",
        overall: 70,
        precio: 5810000,
      },
      {
        name: "Erling Haaland",
        position: "Forward",
        overall: 91,
        precio: 7553000,
      },
      {
        name: "Phil Foden",
        position: "Forward",
        overall: 88,
        precio: 7304000,
      },
      {
        name: "Jack Grealish",
        position: "Forward",
        overall: 84,
        precio: 6972000,
      },
      {
        name: "Jérémy Doku",
        position: "Forward",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Savinho",
        position: "Forward",
        overall: 78,
        precio: 6474000,
      },
      {
        name: "Oscar Bobb",
        position: "Forward",
        overall: 75,
        precio: 6225000,
      },
      {
        name: "Omar Marmoush",
        position: "Forward",
        overall: 80,
        precio: 6640000,
      },
      {
        name: "Claudio Echeverri",
        position: "Forward",
        overall: 72,
        precio: 5976000,
      },
    ];

    const totalOverall = manCityPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / manCityPlayers.length;

    response.json({data: {players: manCityPlayers, averageOverall}});
  }
);
