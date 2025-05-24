import * as functions from "firebase-functions";

export const getChelseaPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Chelsea/";
    const chelseaPlayers = [
      {
        name: "Robert Sánchez",
        position: "Portero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Robert.png`,
      },
      {
        name: "Marcus Bettinelli",
        position: "Portero",
        overall: 72,
        precio: 2000000,
        url: `${storageBaseUrl}Bettinelli.png`,
      },
      {
        name: "Reece James",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}James.png`,
      },
      {
        name: "Ben Chilwell",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Chilwell.png`,
      },
      {
        name: "Marc Cucurella",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Cucurella.png`,
      },
      {
        name: "Benoît Badiashile",
        position: "Defensa",
        overall: 79,
        precio: 4000000,
        url: `${storageBaseUrl}Badiashile.png`,
      },
      {
        name: "Wesley Fofana",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Fofana.png`,
      },
      {
        name: "Enzo Fernández",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Enzo.png`,
      },
      {
        name: "Omari Kellyman",
        position: "Mediocentro",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Kellyman.png`,
      },
      {
        name: "Moisés Caicedo",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Caicedo.png`,
      },
      {
        name: "Cesare Casadei",
        position: "Mediocentro",
        overall: 74,
        precio: 3000000,
        url: `${storageBaseUrl}Casadei.png`,
      },
      {
        name: "Renato Veiga",
        position: "Mediocentro",
        overall: 72,
        precio: 2000000,
        url: `${storageBaseUrl}Veiga.png`,
      },
      {
        name: "Romeo Lavia",
        position: "Mediocentro",
        overall: 75,
        precio: 3500000,
        url: `${storageBaseUrl}Lavia.png`,
      },
      {
        name: "Raheem Sterling",
        position: "Delantero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Sterling.png`,
      },
      {
        name: "Mykhailo Mudryk",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Mudryk.png`,
      },
      {
        name: "Christopher Nkunku",
        position: "Delantero",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}Nkunku.png`,
      },
      {
        name: "Nicolas Jackson",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Jackson.png`,
      },
      {
        name: "Cole Palmer",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Palmer.png`,
      },
      {
        name: "João Félix",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Félix.png`,
      },
      {
        name: "Jadon Sancho",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Sancho.png`,
      },
      {
        name: "Pedro Neto",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Neto.png`,
      },
    ];
    const totalOverall = chelseaPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / chelseaPlayers.length;

    response.json({data: {players: chelseaPlayers, averageOverall}});
  }
);
