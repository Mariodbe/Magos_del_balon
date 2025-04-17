import * as functions from "firebase-functions";

export const getChelseaPlayers = functions.https.onRequest(
  (request, response) => {
    const chelseaPlayers = [
      // Porteros
      {
        name: "Robert Sánchez",
        position: "Goalkeeper",
        overall: 83,
      },
      {
        name: "Marcus Bettinelli",
        position: "Goalkeeper",
        overall: 72,
      },
      {
        name: "Filip Jørgensen",
        position: "Goalkeeper",
        overall: 75,
      },
      {
        name: "Lucas Bergström",
        position: "Goalkeeper",
        overall: 70,
      },

      // Defensores
      {
        name: "Reece James",
        position: "Defender",
        overall: 85,
      },
      {
        name: "Ben Chilwell",
        position: "Defender",
        overall: 83,
      },
      {
        name: "Marc Cucurella",
        position: "Defender",
        overall: 80,
      },
      {
        name: "Axel Disasi",
        position: "Defender",
        overall: 78,
      },
      {
        name: "Benoît Badiashile",
        position: "Defender",
        overall: 79,
      },
      {
        name: "Levi Colwill",
        position: "Defender",
        overall: 77,
      },
      {
        name: "Tosin Adarabioyo",
        position: "Defender",
        overall: 76,
      },
      {
        name: "Malo Gusto",
        position: "Defender",
        overall: 75,
      },
      {
        name: "Wesley Fofana",
        position: "Defender",
        overall: 80,
      },
      {
        name: "Josh Acheampong",
        position: "Defender",
        overall: 68,
      },
      {
        name: "Richard Olise",
        position: "Defender",
        overall: 68,
      },
      {
        name: "Harrison Murray-Campbell",
        position: "Defender",
        overall: 67,
      },
      {
        name: "Kaiden Wilson",
        position: "Defender",
        overall: 67,
      },

      // Centrocampistas
      {
        name: "Enzo Fernández",
        position: "Midfielder",
        overall: 84,
      },
      {
        name: "Conor Gallagher",
        position: "Midfielder",
        overall: 82,
      },
      {
        name: "Moisés Caicedo",
        position: "Midfielder",
        overall: 83,
      },
      {
        name: "Kiernan Dewsbury-Hall",
        position: "Midfielder",
        overall: 78,
      },
      {
        name: "Carney Chukwuemeka",
        position: "Midfielder",
        overall: 75,
      },
      {
        name: "Cesare Casadei",
        position: "Midfielder",
        overall: 74,
      },
      {
        name: "Omari Kellyman",
        position: "Midfielder",
        overall: 70,
      },
      {
        name: "Renato Veiga",
        position: "Midfielder",
        overall: 72,
      },
      {
        name: "Romeo Lavia",
        position: "Midfielder",
        overall: 75,
      },

      // Delanteros
      {
        name: "Raheem Sterling",
        position: "Forward",
        overall: 85,
      },
      {
        name: "Mykhailo Mudryk",
        position: "Forward",
        overall: 80,
      },
      {
        name: "Noni Madueke",
        position: "Forward",
        overall: 78,
      },
      {
        name: "Christopher Nkunku",
        position: "Forward",
        overall: 86,
      },
      {
        name: "Nicolas Jackson",
        position: "Forward",
        overall: 83,
      },
      {
        name: "Cole Palmer",
        position: "Forward",
        overall: 80,
      },
      {
        name: "João Félix",
        position: "Forward",
        overall: 84,
      },
      {
        name: "Jadon Sancho",
        position: "Forward",
        overall: 82,
      },
      {
        name: "Marc Guiu",
        position: "Forward",
        overall: 70,
      },
      {
        name: "Deivid Washington",
        position: "Forward",
        overall: 70,
      },
      {
        name: "Pedro Neto",
        position: "Forward",
        overall: 82,
      },
    ];

    response.json({data: {players: chelseaPlayers}});
  }
);
