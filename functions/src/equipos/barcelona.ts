import * as functions from "firebase-functions";

export const getBarcelonaPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/FcBarcelona/";
    const barcelonaPlayers = [
      {
        name: "Marc-André ter Stegen",
        position: "Portero",
        overall: 89,
        precio: 22000000,
        url: `${storageBaseUrl}TerStegen.png`,
      },
      {
        name: "Iñaki Peña",
        position: "Portero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}iñaki.png`,
      },
      {
        name: "Wojciech Szczęsny",
        position: "Portero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}tek.webp`,
      },
      {
        name: "Pau Cubarsí",
        position: "Defensa",
        overall: 78,
        precio: 4000000,
        url: `${storageBaseUrl}cubarsi.png`,
      },
      {
        name: "Alejandro Balde",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}balde.png`,
      },
      {
        name: "Ronald Araujo",
        position: "Defensa",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}araujo.png`,
      },
      {
        name: "Iñigo Martínez",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}iñigo.png`,
      },
      {
        name: "Andreas Christensen",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Christensen.png`,
      },
      {
        name: "Jules Koundé",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Kounde.png`,
      },
      {
        name: "Eric García",
        position: "Defensa",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}EricGarcia.png`,
      },
      {
        name: "Frenkie de Jong",
        position: "Mediocentro",
        overall: 87,
        precio: 22000000,
        url: `${storageBaseUrl}DeJong.png`,
      },
      {
        name: "Pedri",
        position: "Mediocentro",
        overall: 88,
        precio: 25000000,
        url: `${storageBaseUrl}Pedri.png`,
      },
      {
        name: "Gavi",
        position: "Mediocentro",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}gavi.png`,
      },
      {
        name: "Casadó",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}casadó.png`,
      },
      {
        name: "Torre",
        position: "Mediocentro",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}PabloTorre.png`,
      },
      {
        name: "Ansu Fati",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Ansu.png`,
      },
      {
        name: "Ferran Torres",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}FerranTorres.png`,
      },
      {
        name: "Robert Lewandowski",
        position: "Delantero",
        overall: 91,
        precio: 30000000,
        url: `${storageBaseUrl}Lewandowski.png`,
      },
      {
        name: "Olmo",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Olmo.webp`,
      },
      {
        name: "Pau Victor",
        position: "Delantero",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}PauVictor.png`,
      },
      {
        name: "Raphinha",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Raphinha.png`,
      },
      {
        name: "Yamal",
        position: "Delantero",
        overall: 90,
        precio: 25000000,
        url: `${storageBaseUrl}Yamal.webp`,
      },
    ];
    const totalOverall = barcelonaPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / barcelonaPlayers.length;

    response.json({data: {players: barcelonaPlayers, averageOverall}});
  }
);
