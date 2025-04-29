import * as functions from "firebase-functions";

export const getBarcelonaPlayers = functions.https.onRequest(
  (request, response) => {
    const barcelonaPlayers = [
      {
        name: "Marc-André ter Stegen",
        position: "Goalkeeper",
        overall: 89,
        precio: 7921000,
      },
      {
        name: "Iñaki Peña",
        position: "Goalkeeper",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Wojciech Szczęsny",
        position: "Goalkeeper",
        overall: 85,
        precio: 7225000,
      },
      {
        name: "Pau Cubarsí",
        position: "Defender",
        overall: 78,
        precio: 6084000,
      },
      {
        name: "Alejandro Balde",
        position: "Defender",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Ronald Araujo",
        position: "Defender",
        overall: 86,
        precio: 7396000,
      },
      {
        name: "Iñigo Martínez",
        position: "Defender",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Andreas Christensen",
        position: "Defender",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Jules Koundé",
        position: "Defender",
        overall: 85,
        precio: 7225000,
      },
      {
        name: "Eric García",
        position: "Defender",
        overall: 81,
        precio: 6561000,
      },
      {
        name: "Gerard Martín",
        position: "Defender",
        overall: 79,
        precio: 6241000,
      },
      {
        name: "Frenkie de Jong",
        position: "Midfielder",
        overall: 87,
        precio: 7569000,
      },
      {
        name: "Pedri",
        position: "Midfielder",
        overall: 88,
        precio: 7744000,
      },
      {
        name: "Gavi",
        position: "Midfielder",
        overall: 85,
        precio: 7225000,
      },
      {
        name: "Casadó",
        position: "Midfielder",
        overall: 80,
        precio: 6400000,
      },
      {
        name: "Darvich",
        position: "Midfielder",
        overall: 79,
        precio: 6241000,
      },
      {
        name: "Torre",
        position: "Midfielder",
        overall: 81,
        precio: 6561000,
      },
      {
        name: "Ansu Fati",
        position: "Forward",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Fermín López",
        position: "Forward",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Ferran Torres",
        position: "Forward",
        overall: 82,
        precio: 6724000,
      },
      {
        name: "Robert Lewandowski",
        position: "Forward",
        overall: 91,
        precio: 8281000,
      },
      {
        name: "Olmo",
        position: "Forward",
        overall: 83,
        precio: 6889000,
      },
      {
        name: "Pau Victor",
        position: "Forward",
        overall: 81,
        precio: 6561000,
      },
      {
        name: "Raphinha",
        position: "Forward",
        overall: 84,
        precio: 7056000,
      },
      {
        name: "Yamal",
        position: "Forward",
        overall: 80,
        precio: 6400000,
      },
    ];

    response.json({data: {players: barcelonaPlayers}});
  }
);
