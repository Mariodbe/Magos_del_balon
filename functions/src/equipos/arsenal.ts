import * as functions from "firebase-functions";

export const getArsenalPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl =
      "gs://magosdelbalon-f8f08." + "firebasestorage.app/Arsenal/";
    const arsenalPlayers = [
      {
        name: "Aaron Ramsdale",
        position: "Portero",
        overall: 84,
        precio: 5000000,
        url: `${storageBaseUrl}Ramsdale.png`,
      },
      {
        name: "David Raya",
        position: "Portero",
        overall: 85,
        precio: 5500000,
        url: `${storageBaseUrl}Raya.png`,
      },
      {
        name: "William Saliba",
        position: "Defensa",
        overall: 87,
        precio: 20000000,
        url: `${storageBaseUrl}Saliba.png`,
      },
      {
        name: "Gabriel Magalhães",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Magalhães.png`,
      },
      {
        name: "Ben White",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}White.png`,
      },
      {
        name: "Oleksandr Zinchenko",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Zinchenko.png`,
      },
      {
        name: "Takehiro Tomiyasu",
        position: "Defensa",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Tomiyasu.png`,
      },
      {
        name: "Jakub Kiwior",
        position: "Defensa",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Kiwior.png`,
      },
      {
        name: "Jorginho",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Jorginho.png`,
      },
      {
        name: "Declan Rice",
        position: "Mediocentro",
        overall: 88,
        precio: 25000000,
        url: `${storageBaseUrl}Rice.png`,
      },
      {
        name: "Martin Ødegaard",
        position: "Mediocentro",
        overall: 87,
        precio: 22000000,
        url: `${storageBaseUrl}Ødegaard.png`,
      },
      {
        name: "Thomas Partey",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Partey.png`,
      },
      {
        name: "Fabio Vieira",
        position: "Mediocentro",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}Vieira.png`,
      },
      {
        name: "Emile Smith Rowe",
        position: "Mediocentro",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Rowe.png`,
      },
      {
        name: "Bukayo Saka",
        position: "Delantero",
        overall: 89,
        precio: 30000000,
        url: `${storageBaseUrl}Saka.png`,
      },
      {
        name: "Gabriel Jesus",
        position: "Delantero",
        overall: 85,
        precio: 18000000,
        url: `${storageBaseUrl}Jesus.png`,
      },
      {
        name: "Gabriel Martinelli",
        position: "Delantero",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}Martinelli.png`,
      },
      {
        name: "Leandro Trossard",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Trossard.png`,
      },
      {
        name: "Eddie Nketiah",
        position: "Delantero",
        overall: 81,
        precio: 7000000,
        url: `${storageBaseUrl}Nketiah.png`,
      },
      {
        name: "Reiss Nelson",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Nelson.png`,
      },
    ];

    const totalOverall = arsenalPlayers.reduce(
      (acc, player) => acc + player.overall,
      0
    );
    const averageOverall = totalOverall / arsenalPlayers.length;

    response.json({data: {players: arsenalPlayers, averageOverall}});
  }
);
