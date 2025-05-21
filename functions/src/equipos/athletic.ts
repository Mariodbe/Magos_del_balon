import * as functions from "firebase-functions";

export const getAthleticClubPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Athletic/";
    const athleticPlayers = [
      {
        name: "Unai Simón",
        position: "Goalkeeper",
        overall: 86,
        precio: 7396000,
        url: `${storageBaseUrl}Simón.png`,
      },
      {
        name: "Julen Agirrezabala",
        position: "Goalkeeper",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Agirrezabala.webp`,
      },
      {
        name: "Yeray Álvarez",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Yeray.png`,
      },
      {
        name: "Íñigo Lekue",
        position: "Defender",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}Lekue.png`,
      },
      {
        name: "Aitor Paredes",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Paredes.png`,
      },
      {
        name: "Dani Vivian",
        position: "Defender",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Vivian.png`,
      },
      {
        name: "Yuri Berchiche",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Berchiche.png`,
      },
      {
        name: "Óscar de Marcos",
        position: "Defender",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}DeMarcos.png`,
      },
      {
        name: "Mikel Vesga",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}Vesga.webp`,
      },
      {
        name: "Dani García",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}García.png`,
      },
      {
        name: "Oihan Sancet",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Sancet.webp`,
      },
      {
        name: "Iñigo Ruiz de Galarreta",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Galarreta.png`,
      },
      {
        name: "Beñat Prados",
        position: "Midfielder",
        overall: 78,
        precio: 6084000,
        url: `${storageBaseUrl}Prados.png`,
      },
      {
        name: "Iker Muniain",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Muniain.webp`,
      },
      {
        name: "Unai Gómez",
        position: "Midfielder",
        overall: 78,
        precio: 6084000,
        url: `${storageBaseUrl}Gómez.png`,
      },
      {
        name: "Ander Herrera",
        position: "Midfielder",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Herrera.png`,
      },
      {
        name: "Álex Berenguer",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Berenguer.png`,
      },
      {
        name: "Nico Williams",
        position: "Forward",
        overall: 86,
        precio: 7396000,
        url: `${storageBaseUrl}Williams.png`,
      },
      {
        name: "Iñaki Williams",
        position: "Forward",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Iñaki.png`,
      },
      {
        name: "Gorka Guruzeta",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Guruzeta.png`,
      },
      {
        name: "Asier Villalibre",
        position: "Forward",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Villalibre.png`,
      },
      {
        name: "Malcom Adu Ares",
        position: "Forward",
        overall: 77,
        precio: 5923000,
        url: `${storageBaseUrl}Ares.webp`,
      },
    ];

    const totalOverall = athleticPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / athleticPlayers.length;

    response.json({data: {players: athleticPlayers, averageOverall}});
  }
);
