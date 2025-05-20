import * as functions from "firebase-functions";

export const getManCityPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/MCity/";
    const manCityPlayers = [
      {
        name: "Ederson",
        position: "Goalkeeper",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Ederson.png`,
      },
      {
        name: "Stefan Ortega",
        position: "Goalkeeper",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}Ortega.png`,
      },
      {
        name: "Kyle Walker",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Walker.png`,
      },
      {
        name: "Rúben Dias",
        position: "Defender",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Dias.png`,
      },
      {
        name: "John Stones",
        position: "Defender",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Stones.png`,
      },
      {
        name: "Nathan Aké",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Aké.png`,
      },
      {
        name: "Joško Gvardiol",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Gvardiol.png`,
      },
      {
        name: "Manuel Akanji",
        position: "Defender",
        overall: 82,
        precio: 6806000,
        url: `${storageBaseUrl}Akanji.png`,
      },
      {
        name: "Rico Lewis",
        position: "Defender",
        overall: 75,
        precio: 6225000,
        url: `${storageBaseUrl}Lewis.png`,
      },
      {
        name: "Abdukodir Khusanov",
        position: "Defender",
        overall: 72,
        precio: 5976000,
        url: `${storageBaseUrl}Khusanov.png`,
      },
      {
        name: "Rodri",
        position: "Midfielder",
        overall: 91,
        precio: 7553000,
        url: `${storageBaseUrl}Rodri.png`,
      },
      {
        name: "Kevin De Bruyne",
        position: "Midfielder",
        overall: 90,
        precio: 7470000,
        url: `${storageBaseUrl}Bruyne.png`,
      },
      {
        name: "Bernardo Silva",
        position: "Midfielder",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Silva.png`,
      },
      {
        name: "Mateo Kovačić",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Kovačić.png`,
      },
      {
        name: "İlkay Gündoğan",
        position: "Midfielder",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Gündoğan.png`,
      },
      {
        name: "Matheus Nunes",
        position: "Midfielder",
        overall: 82,
        precio: 6806000,
        url: `${storageBaseUrl}Nunes.png`,
      },
      {
        name: "Nico González",
        position: "Midfielder",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}González.png`,
      },
      {
        name: "James McAtee",
        position: "Midfielder",
        overall: 75,
        precio: 6225000,
        url: `${storageBaseUrl}McAtee.png`,
      },
      {
        name: "Erling Haaland",
        position: "Forward",
        overall: 91,
        precio: 7553000,
        url: `${storageBaseUrl}Haaland.png`,
      },
      {
        name: "Phil Foden",
        position: "Forward",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Foden.png`,
      },
      {
        name: "Jack Grealish",
        position: "Forward",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Grealish.png`,
      },
      {
        name: "Jérémy Doku",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Doku.png`,
      },
      {
        name: "Savinho",
        position: "Forward",
        overall: 78,
        precio: 6474000,
        url: `${storageBaseUrl}Savinho.png`,
      },
      {
        name: "Omar Marmoush",
        position: "Forward",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}Marmoush.webp`,
      },
      {
        name: "Claudio Echeverri",
        position: "Forward",
        overall: 72,
        precio: 5976000,
        url: `${storageBaseUrl}Echeverri.png`,
      },
    ];

    const totalOverall = manCityPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / manCityPlayers.length;

    response.json({data: {players: manCityPlayers, averageOverall}});
  }
);
