import * as functions from "firebase-functions";

export const getArsenalPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Arsenal/";
    const arsenalPlayers = [
      {
        name: "Aaron Ramsdale",
        position: "Goalkeeper",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Ramsdale.png`,
      },
      {
        name: "David Raya",
        position: "Goalkeeper",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Raya.png`,
      },
      {
        name: "William Saliba",
        position: "Defender",
        overall: 87,
        precio: 7569000,
        url: `${storageBaseUrl}Saliba.png`,
      },
      {
        name: "Gabriel Magalhães",
        position: "Defender",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Magalhães.png`,
      },
      {
        name: "Ben White",
        position: "Defender",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}White.png`,
      },
      {
        name: "Oleksandr Zinchenko",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Zinchenko.png`,
      },
      {
        name: "Takehiro Tomiyasu",
        position: "Defender",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Tomiyasu.png`,
      },
      {
        name: "Jakub Kiwior",
        position: "Defender",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Kiwior.png`,
      },
      {
        name: "Jorginho",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Jorginho.png`,
      },
      {
        name: "Declan Rice",
        position: "Midfielder",
        overall: 88,
        precio: 7744000,
        url: `${storageBaseUrl}Rice.png`,
      },
      {
        name: "Martin Ødegaard",
        position: "Midfielder",
        overall: 87,
        precio: 7569000,
        url: `${storageBaseUrl}Ødegaard.png`,
      },
      {
        name: "Thomas Partey",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Partey.png`,
      },
      {
        name: "Fabio Vieira",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}Vieira.png`,
      },
      {
        name: "Emile Smith Rowe",
        position: "Midfielder",
        overall: 82,
        precio: 6724000,
        url: `${storageBaseUrl}Rowe.png`,
      },
      {
        name: "Bukayo Saka",
        position: "Forward",
        overall: 89,
        precio: 7921000,
        url: `${storageBaseUrl}Saka.png`,
      },
      {
        name: "Gabriel Jesus",
        position: "Forward",
        overall: 85,
        precio: 7225000,
        url: `${storageBaseUrl}Jesus.png`,
      },
      {
        name: "Gabriel Martinelli",
        position: "Forward",
        overall: 86,
        precio: 7396000,
        url: `${storageBaseUrl}Martinelli.png`,
      },
      {
        name: "Leandro Trossard",
        position: "Forward",
        overall: 84,
        precio: 7056000,
        url: `${storageBaseUrl}Trossard.png`,
      },
      {
        name: "Eddie Nketiah",
        position: "Forward",
        overall: 81,
        precio: 6561000,
        url: `${storageBaseUrl}Nketiah.png`,
      },
      {
        name: "Reiss Nelson",
        position: "Forward",
        overall: 80,
        precio: 6400000,
        url: `${storageBaseUrl}Nelson.png`,
      },
    ];

    const totalOverall = arsenalPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / arsenalPlayers.length;

    response.json({data: {players: arsenalPlayers, averageOverall}});
  }
);
