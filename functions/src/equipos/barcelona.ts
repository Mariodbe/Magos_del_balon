import * as functions from "firebase-functions";

export const getBarcelonaPlayers = functions.https.onRequest(
  (request, response) => {
    const barcelonaPlayers = [
      {
        name: "Marc-André ter Stegen",
        position: "Goalkeeper",
        overall: 89,

      },
      {
        name: "Iñaki Peña",
        position: "Goalkeeper",
        overall: 82,

      },
      {
        name: "Wojciech Szczęsny",
        position: "Goalkeeper",
        overall: 85,

      },
      {
        name: "Pau Cubarsí",
        position: "Defender",
        overall: 78,

      },
      {
        name: "Alejandro Balde",
        position: "Defender",
        overall: 80,

      },
      {
        name: "Ronald Araujo",
        position: "Defender",
        overall: 86,

      },
      {
        name: "Iñigo Martínez",
        position: "Defender",
        overall: 84,

      },
      {
        name: "Andreas Christensen",
        position: "Defender",
        overall: 83,

      },
      {
        name: "Jules Koundé",
        position: "Defender",
        overall: 85,

      },
      {
        name: "Eric García",
        position: "Defender",
        overall: 81,

      },
      {
        name: "Gerard Martín",
        position: "Defender",
        overall: 79,

      },
      {
        name: "Frenkie de Jong",
        position: "Midfielder",
        overall: 87,

      },
      {
        name: "Pedri",
        position: "Midfielder",
        overall: 88,

      },
      {
        name: "Gavi",
        position: "Midfielder",
        overall: 85,

      },
      {
        name: "Casadó",
        position: "Midfielder",
        overall: 80,

      },
      {
        name: "Darvich",
        position: "Midfielder",
        overall: 79,

      },
      {
        name: "Torre",
        position: "Midfielder",
        overall: 81,

      },
      {
        name: "Ansu Fati",
        position: "Forward",
        overall: 84,

      },
      {
        name: "Fermín López",
        position: "Forward",
        overall: 82,

      },
      {
        name: "Ferran Torres",
        position: "Forward",
        overall: 82,

      },
      {
        name: "Robert Lewandowski",
        position: "Forward",
        overall: 91,

      },
      {
        name: "Olmo",
        position: "Forward",
        overall: 83,

      },
      {
        name: "Pau Victor",
        position: "Forward",
        overall: 81,

      },
      {
        name: "Raphinha",
        position: "Forward",
        overall: 84,

      },
      {
        name: "Yamal",
        position: "Forward",
        overall: 80,

      },
    ];

    response.json({data: {players: barcelonaPlayers}});
  }
);
