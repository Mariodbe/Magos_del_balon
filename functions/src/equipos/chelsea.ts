import * as functions from "firebase-functions";

export const getChelseaPlayers = functions.https.onRequest(
  (request, response) => {
    const chelseaPlayers = [
      {
        name: "Robert Sánchez",
        position: "Goalkeeper",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Marcus Bettinelli",
        position: "Goalkeeper",
        overall: 72,
        precio: 5184000,
      },
      {
        name: "Filip Jørgensen",
        position: "Goalkeeper",
        overall: 75,
        precio: 5625000,
      },
      {
        name: "Lucas Bergström",
        position: "Goalkeeper",
        overall: 70,
        precio: 4900000,
      },
      {
        name: "Reece James",
        position: "Defender",
        overall: 85,
        precio: 7225000,
      },
      {
        name: "Ben Chilwell",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Marc Cucurella",
        position: "Defender",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Axel Disasi",
        position: "Defender",
        overall: 78,
        precio: 6084000,
      },
      {
        name: "Benoît Badiashile",
        position: "Defender",
        overall: 79,
        precio: 6241000,
      },
      {
        name: "Levi Colwill",
        position: "Defender",
        overall: 77,
        precio: 5929000,
      },
      {
        name: "Tosin Adarabioyo",
        position: "Defender",
        overall: 76,
        precio: 5776000,
      },
      {
        name: "Malo Gusto",
        position: "Defender",
        overall: 75,
        precio: 5625000,
      },
      {
        name: "Wesley Fofana",
        position: "Defender",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Josh Acheampong",
        position: "Defender",
        overall: 68,
        precio: 4624000,
      },
      {
        name: "Richard Olise",
        position: "Defender",
        overall: 68,
        precio: 4624000,
      },
      {
        name: "Harrison Murray-Campbell",
        position: "Defender",
        overall: 67,
        precio: 4489000,
      },
      {
        name: "Kaiden Wilson",
        position: "Defender",
        overall: 67,
        precio: 4489000,
      },
      {
        name: "Enzo Fernández",
        position: "Midfielder",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Conor Gallagher",
        position: "Midfielder",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Moisés Caicedo",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Kiernan Dewsbury-Hall",
        position: "Midfielder",
        overall: 78,
        precio: 6084000,
      },
      {
        name: "Carney Chukwuemeka",
        position: "Midfielder",
        overall: 75,
        precio: 5625000,
      },
      {
        name: "Cesare Casadei",
        position: "Midfielder",
        overall: 74,
        precio: 5476000,
      },
      {
        name: "Omari Kellyman",
        position: "Midfielder",
        overall: 70,
        precio: 4900000,
      },
      {
        name: "Renato Veiga",
        position: "Midfielder",
        overall: 72,
        precio: 5184000,
      },
      {
        name: "Romeo Lavia",
        position: "Midfielder",
        overall: 75,
        precio: 5625000,
      },
      {
        name: "Raheem Sterling",
        position: "Forward",
        overall: 85,
        precio: 7225000,
      },
      {
        name: "Mykhailo Mudryk",
        position: "Forward",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Noni Madueke",
        position: "Forward",
        overall: 78,
        precio: 6084000,
      },
      {
        name: "Christopher Nkunku",
        position: "Forward",
        overall: 86,
        precio: 7396000,
      },
      {
        name: "Nicolas Jackson",
        position: "Forward",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Cole Palmer",
        position: "Forward",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "João Félix",
        position: "Forward",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Jadon Sancho",
        position: "Forward",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Marc Guiu",
        position: "Forward",
        overall: 70,
        precio: 4900000,
      },
      {
        name: "Deivid Washington",
        position: "Forward",
        overall: 70,
        precio: 4900000,
      },
      {
        name: "Pedro Neto",
        position: "Forward",
        overall: 82,
        precio: 6724000,
      },
    ];
    const totalOverall = chelseaPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / chelseaPlayers.length;

    response.json({data: {players: chelseaPlayers, averageOverall}});
  }
);
