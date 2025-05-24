import * as functions from "firebase-functions";

export const getMadridPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/RMadrid/";
    const madridPlayers = [
      {
        name: "Thibaut Courtois",
        position: "Portero",
        overall: 90,
        precio: 22000000,
        url: `${storageBaseUrl}Courtois.png`,
      },
      {
        name: "Andriy Lunin",
        position: "Portero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Lunin.png`,
      },
      {
        name: "Dani Carvajal",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Carvajal.png`,
      },
      {
        name: "Éder Militão",
        position: "Defensa",
        overall: 86,
        precio: 15000000,
        url: `${storageBaseUrl}Militao.png`,
      },
      {
        name: "David Alaba",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Alaba.png`,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defensa",
        overall: 86,
        precio: 15000000,
        url: `${storageBaseUrl}Rudiger.png`,
      },
      {
        name: "Ferland Mendy",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Mendy.png`,
      },
      {
        name: "Aurélien Tchouaméni",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Tchouaméni.png`,
      },
      {
        name: "Luka Modrić",
        position: "Mediocentro",
        overall: 87,
        precio: 15000000,
        url: `${storageBaseUrl}Modric.webp`,
      },
      {
        name: "Dani Ceballos",
        position: "Mediocentro",
        overall: 82,
        precio: 10000000,
        url: `${storageBaseUrl}Ceballos.png`,
      },
      {
        name: "Eduardo Camavinga",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Camavinga.png`,
      },
      {
        name: "Jude Bellingham",
        position: "Mediocentro",
        overall: 87,
        precio: 15000000,
        url: `${storageBaseUrl}Bellingham.png`,
      },
      {
        name: "Federico Valverde",
        position: "Mediocentro",
        overall: 86,
        precio: 15000000,
        url: `${storageBaseUrl}Valverde.png`,
      },
      {
        name: "Arda Güler",
        position: "Mediocentro",
        overall: 78,
        precio: 5000000,
        url: `${storageBaseUrl}Güler.png`,
      },
      {
        name: "Vinícius Jr.",
        position: "Delantero",
        overall: 89,
        precio: 22000000,
        url: `${storageBaseUrl}Vinícius.png`,
      },
      {
        name: "Rodrygo",
        position: "Delantero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Rodrygo.webp`,
      },
      {
        name: "Brahim Díaz",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Brahim.png`,
      },
      {
        name: "Kylian Mbappé",
        position: "Delantero",
        overall: 90,
        precio: 25000000,
        url: `${storageBaseUrl}Kylian.png`,
      },
    ];
    const totalOverall = madridPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / madridPlayers.length;

    response.json({data: {players: madridPlayers, averageOverall}});
  }
);
