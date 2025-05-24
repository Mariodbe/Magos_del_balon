import * as functions from "firebase-functions";

export const getManCityPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/MCity/";
    const manCityPlayers = [
      {
        name: "Ederson",
        position: "Portero",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Ederson.png`,
      },
      {
        name: "Stefan Ortega",
        position: "Portero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Ortega.png`,
      },
      {
        name: "Kyle Walker",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Walker.png`,
      },
      {
        name: "Rúben Dias",
        position: "Defensa",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Dias.png`,
      },
      {
        name: "John Stones",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Stones.png`,
      },
      {
        name: "Nathan Aké",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Aké.png`,
      },
      {
        name: "Joško Gvardiol",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Gvardiol.png`,
      },
      {
        name: "Manuel Akanji",
        position: "Defensa",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Akanji.png`,
      },
      {
        name: "Rico Lewis",
        position: "Defensa",
        overall: 75,
        precio: 3000000,
        url: `${storageBaseUrl}Lewis.png`,
      },
      {
        name: "Abdukodir Khusanov",
        position: "Defensa",
        overall: 72,
        precio: 2000000,
        url: `${storageBaseUrl}Khusanov.png`,
      },
      {
        name: "Rodri",
        position: "Mediocentro",
        overall: 91,
        precio: 25000000,
        url: `${storageBaseUrl}Rodri.png`,
      },
      {
        name: "Kevin De Bruyne",
        position: "Mediocentro",
        overall: 90,
        precio: 22000000,
        url: `${storageBaseUrl}Bruyne.png`,
      },
      {
        name: "Bernardo Silva",
        position: "Mediocentro",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Silva.png`,
      },
      {
        name: "Mateo Kovačić",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Kovačić.png`,
      },
      {
        name: "İlkay Gündoğan",
        position: "Mediocentro",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Gündoğan.png`,
      },
      {
        name: "Matheus Nunes",
        position: "Mediocentro",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Nunes.png`,
      },
      {
        name: "Nico González",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}González.png`,
      },
      {
        name: "James McAtee",
        position: "Mediocentro",
        overall: 75,
        precio: 3000000,
        url: `${storageBaseUrl}McAtee.png`,
      },
      {
        name: "Erling Haaland",
        position: "Delantero",
        overall: 91,
        precio: 25000000,
        url: `${storageBaseUrl}Haaland.png`,
      },
      {
        name: "Phil Foden",
        position: "Delantero",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Foden.png`,
      },
      {
        name: "Jack Grealish",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Grealish.png`,
      },
      {
        name: "Jérémy Doku",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Doku.png`,
      },
      {
        name: "Savinho",
        position: "Delantero",
        overall: 78,
        precio: 5000000,
        url: `${storageBaseUrl}Savinho.png`,
      },
      {
        name: "Omar Marmoush",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Marmoush.webp`,
      },
      {
        name: "Claudio Echeverri",
        position: "Delantero",
        overall: 72,
        precio: 2000000,
        url: `${storageBaseUrl}Echeverri.png`,
      },
    ];

    const totalOverall = manCityPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / manCityPlayers.length;

    response.json({data: {players: manCityPlayers, averageOverall}});
  }
);
