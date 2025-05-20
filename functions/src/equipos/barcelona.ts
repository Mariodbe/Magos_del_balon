import * as functions from "firebase-functions";

export const getBarcelonaPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/FcBarcelona/";
    const barcelonaPlayers = [
      {
        name: "Marc-André ter Stegen",
        position: "Goalkeeper",
        overall: 89,
        precio: 7921000,
        url: `${storageBaseUrl}TerStegen.png`,
      },
      {
        name: "Iñaki Peña",
        position: "Goalkeeper",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}iñaki.png`,
      },
      {
        name: "Wojciech Szczęsny",
        position: "Goalkeeper",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}tek.webp`,
      },
      {
        name: "Pau Cubarsí",
        position: "Defender",
        overall: 78,
        precio: 6084000,
        url: `${storageBaseUrl}cubarsi.png`,
      },
      {
        name: "Alejandro Balde",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}balde.png`,
      },
      {
        name: "Ronald Araujo",
        position: "Defender",
        overall: 86,
        precio: 7396000,
        url: `${storageBaseUrl}araujo.png`,
      },
      {
        name: "Iñigo Martínez",
        position: "Defender",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}iñigo.png`,
      },
      {
        name: "Andreas Christensen",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Christensen.png`,
      },
      {
        name: "Jules Koundé",
        position: "Defender",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Kounde.png`,
      },
      {
        name: "Eric García",
        position: "Defender",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}EricGarcia.png`,
      },
      {
        name: "Frenkie de Jong",
        position: "Midfielder",
        overall: 87,
        precio: 7569000,
        url: `${storageBaseUrl}DeJong.png`,
      },
      {
        name: "Pedri",
        position: "Midfielder",
        overall: 88,
        precio: 7744000,
        url: `${storageBaseUrl}Pedri.png`,
      },
      {
        name: "Gavi",
        position: "Midfielder",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}gavi.png`,
      },
      {
        name: "Casadó",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}casadó.png`,
      },
      {
        name: "Torre",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}PabloTorre.png`,
      },
      {
        name: "Ansu Fati",
        position: "Forward",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Ansu.png`,
      },
      {
        name: "Ferran Torres",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}FerranTorres.png`,
      },
      {
        name: "Robert Lewandowski",
        position: "Forward",
        overall: 91,
        precio: 8281000,
        url: `${storageBaseUrl}Lewandowski.png`,
      },
      {
        name: "Olmo",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Olmo.webp`,
      },
      {
        name: "Pau Victor",
        position: "Forward",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}PauVictor.png`,
      },
      {
        name: "Raphinha",
        position: "Forward",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Raphinha.png`,
      },
      {
        name: "Yamal",
        position: "Forward",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Yamal.webp`,
      },
    ];
    const totalOverall = barcelonaPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / barcelonaPlayers.length;

    response.json({data: {players: barcelonaPlayers, averageOverall}});
  }
);
