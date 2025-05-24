import * as functions from "firebase-functions";

export const getAthleticClubPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Athletic/";
    const athleticPlayers = [
      {
        name: "Unai Simón",
        position: "Portero",
        overall: 86,
        precio: 18000000,
        url: `${storageBaseUrl}Simón.png`,
      },
      {
        name: "Julen Agirrezabala",
        position: "Portero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Agirrezabala.webp`,
      },
      {
        name: "Yeray Álvarez",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Yeray.png`,
      },
      {
        name: "Íñigo Lekue",
        position: "Defensa",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}Lekue.png`,
      },
      {
        name: "Aitor Paredes",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Paredes.png`,
      },
      {
        name: "Dani Vivian",
        position: "Defensa",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Vivian.png`,
      },
      {
        name: "Yuri Berchiche",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Berchiche.png`,
      },
      {
        name: "Óscar de Marcos",
        position: "Defensa",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}DeMarcos.png`,
      },
      {
        name: "Mikel Vesga",
        position: "Mediocentro",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}Vesga.webp`,
      },
      {
        name: "Dani García",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}García.png`,
      },
      {
        name: "Oihan Sancet",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Sancet.webp`,
      },
      {
        name: "Iñigo Ruiz de Galarreta",
        position: "Mediocentro",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Galarreta.png`,
      },
      {
        name: "Beñat Prados",
        position: "Mediocentro",
        overall: 78,
        precio: 4000000,
        url: `${storageBaseUrl}Prados.png`,
      },
      {
        name: "Iker Muniain",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Muniain.webp`,
      },
      {
        name: "Unai Gómez",
        position: "Mediocentro",
        overall: 78,
        precio: 4000000,
        url: `${storageBaseUrl}Gómez.png`,
      },
      {
        name: "Ander Herrera",
        position: "Mediocentro",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Herrera.png`,
      },
      {
        name: "Álex Berenguer",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Berenguer.png`,
      },
      {
        name: "Nico Williams",
        position: "Delantero",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}Williams.png`,
      },
      {
        name: "Iñaki Williams",
        position: "Delantero",
        overall: 85,
        precio: 18000000,
        url: `${storageBaseUrl}Iñaki.png`,
      },
      {
        name: "Gorka Guruzeta",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Guruzeta.png`,
      },
      {
        name: "Asier Villalibre",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Villalibre.png`,
      },
      {
        name: "Malcom Adu Ares",
        position: "Delantero",
        overall: 77,
        precio: 3000000,
        url: `${storageBaseUrl}Ares.webp`,
      },
    ];

    const totalOverall = athleticPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / athleticPlayers.length;

    response.json({data: {players: athleticPlayers, averageOverall}});
  }
);
