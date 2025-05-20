import * as functions from "firebase-functions";

export const getChelseaPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Chelsea/";
    const chelseaPlayers = [
      {
        name: "Robert Sánchez",
        position: "Goalkeeper",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Robert.png`,
      },
      {
        name: "Marcus Bettinelli",
        position: "Goalkeeper",
        overall: 72,
        precio: 5184000,
        url: `${storageBaseUrl}Bettinelli.png`,
      },
      {
        name: "Reece James",
        position: "Defender",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}James.png`,
      },
      {
        name: "Ben Chilwell",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Chilwell.png`,
      },
      {
        name: "Marc Cucurella",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Cucurella.png`,
      },
      {
        name: "Benoît Badiashile",
        position: "Defender",
        overall: 79,
        precio: 6241000,
        url: `${storageBaseUrl}Badiashile.png`,
      },
      {
        name: "Wesley Fofana",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Fofana.png`,
      },
      {
        name: "Enzo Fernández",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Enzo.png`,
      },
      {
        name: "Omari Kellyman",
        position: "Midfielder",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Kellyman.png`,
      },
      {
        name: "Moisés Caicedo",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Caicedo.png`,
      },
      {
        name: "Cesare Casadei",
        position: "Midfielder",
        overall: 74,
        precio: 5476000,
        url: `${storageBaseUrl}Casadei.png`,
      },
      {
        name: "Renato Veiga",
        position: "Midfielder",
        overall: 72,
        precio: 5184000,
        url: `${storageBaseUrl}Veiga.png`,
      },
      {
        name: "Romeo Lavia",
        position: "Midfielder",
        overall: 75,
        precio: 5625000,
        url: `${storageBaseUrl}Lavia.png`,
      },
      {
        name: "Raheem Sterling",
        position: "Forward",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Sterling.png`,
      },
      {
        name: "Mykhailo Mudryk",
        position: "Forward",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Mudryk.png`,
      },
      {
        name: "Christopher Nkunku",
        position: "Forward",
        overall: 86,
        precio: 7396000,
        url: `${storageBaseUrl}Nkunku.png`,
      },
      {
        name: "Nicolas Jackson",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Jackson.png`,
      },
      {
        name: "Cole Palmer",
        position: "Forward",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Palmer.png`,
      },
      {
        name: "João Félix",
        position: "Forward",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Félix.png`,
      },
      {
        name: "Jadon Sancho",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Sancho.png`,
      },
      {
        name: "Pedro Neto",
        position: "Forward",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Neto.png`,
      },
    ];
    const totalOverall = chelseaPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / chelseaPlayers.length;

    response.json({data: {players: chelseaPlayers, averageOverall}});
  }
);
