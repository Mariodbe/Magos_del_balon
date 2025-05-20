import * as functions from "firebase-functions";

export const getMadridPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/RMadrid/";
    const madridPlayers = [
      {
        name: "Thibaut Courtois",
        position: "Goalkeeper",
        overall: 90,
        precio: 7470000,
        url: `${storageBaseUrl}Courtois.png`,
      },
      {
        name: "Andriy Lunin",
        position: "Goalkeeper",
        overall: 82,
        precio: 6806000,
        url: `${storageBaseUrl}Lunin.png`,
      },
      {
        name: "Dani Carvajal",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Carvajal.png`,
      },
      {
        name: "Éder Militão",
        position: "Defender",
        overall: 86,
        precio: 7138000,
        url: `${storageBaseUrl}Militao.png`,
      },
      {
        name: "David Alaba",
        position: "Defender",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Alaba.png`,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defender",
        overall: 86,
        precio: 7138000,
        url: `${storageBaseUrl}Rudiger.png`,
      },
      {
        name: "Ferland Mendy",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Mendy.png`,
      },
      {
        name: "Aurélien Tchouaméni",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Tchouaméni.png`,
      },
      {
        name: "Luka Modrić",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
        url: `${storageBaseUrl}Modric.webp`,
      },
      {
        name: "Dani Ceballos",
        position: "Midfielder",
        overall: 82,
        precio: 7138000,
        url: `${storageBaseUrl}Ceballos.png`,
      },
      {
        name: "Eduardo Camavinga",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Camavinga.png`,
      },
      {
        name: "Jude Bellingham",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
        url: `${storageBaseUrl}Bellingham.png`,
      },
      {
        name: "Federico Valverde",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
        url: `${storageBaseUrl}Valverde.png`,
      },
      {
        name: "Arda Güler",
        position: "Midfielder",
        overall: 78,
        precio: 6474000,
        url: `${storageBaseUrl}Güler.png`,
      },
      {
        name: "Vinícius Jr.",
        position: "Forward",
        overall: 89,
        precio: 7387000,
        url: `${storageBaseUrl}Vinícius.png`,
      },
      {
        name: "Rodrygo",
        position: "Forward",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Rodrygo.webp`,
      },
      {
        name: "Brahim Díaz",
        position: "Forward",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}Brahim.png`,
      },
      {
        name: "Kylian Mbappé",
        position: "Forward",
        overall: 90,
        precio: 6474000,
        url: `${storageBaseUrl}Kylian.png`,
      },
    ];
    const totalOverall = madridPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / madridPlayers.length;

    response.json({data: {players: madridPlayers, averageOverall}});
  }
);
